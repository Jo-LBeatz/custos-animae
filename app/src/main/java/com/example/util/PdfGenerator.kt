package com.example.util

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {

    fun generatePdf(
        context: Context,
        folderName: String,
        fileName: String,
        contentMap: Map<String, String>
    ) {
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 50
        val contentWidth = pageWidth - (margin * 2)

        var currentPageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        // Paints & Colors matching Custos Animae classic / Dark Cathedral styling
        val goldPrimaryColor = Color.rgb(184, 134, 11) // #B8860B
        val goldDarkColor = Color.rgb(139, 101, 8)
        val textPrimaryColor = Color.rgb(20, 20, 20)
        val textMutedColor = Color.rgb(90, 90, 90)
        val cardBgColor = Color.rgb(248, 246, 240)
        val cardBorderColor = Color.rgb(220, 210, 195)
        val burgundyColor = Color.rgb(128, 0, 32)

        val titlePaint = TextPaint().apply {
            color = burgundyColor
            textSize = 16f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val metaPaint = TextPaint().apply {
            color = goldDarkColor
            textSize = 9.5f
            isAntiAlias = true
        }

        val keyPaint = TextPaint().apply {
            color = burgundyColor
            textSize = 12f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val bodyPaint = TextPaint().apply {
            color = textPrimaryColor
            textSize = 10.5f
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = cardBorderColor
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val headerBgPaint = Paint().apply {
            color = cardBgColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        var yPosition = margin.toFloat()

        fun drawHeader() {
            // Elegant top decorative header block
            canvas.drawRect(
                margin.toFloat(),
                margin.toFloat(),
                (pageWidth - margin).toFloat(),
                (margin + 55).toFloat(),
                headerBgPaint
            )
            val borderPaint = Paint().apply {
                color = goldPrimaryColor
                strokeWidth = 1.5f
                style = Paint.Style.STROKE
                isAntiAlias = true
            }
            canvas.drawRect(
                margin.toFloat(),
                margin.toFloat(),
                (pageWidth - margin).toFloat(),
                (margin + 55).toFloat(),
                borderPaint
            )

            canvas.drawText("CUSTOS ANIMAE — SPIRITUAL RECORD", (margin + 15).toFloat(), (margin + 24).toFloat(), titlePaint)
            
            val dateFormat = SimpleDateFormat("MMMM d, yyyy • HH:mm", Locale.getDefault())
            val dateStr = "Category: $folderName • Generated: ${dateFormat.format(Date())}"
            canvas.drawText(dateStr, (margin + 15).toFloat(), (margin + 42).toFloat(), metaPaint)

            yPosition = (margin + 75).toFloat()
        }

        fun drawFooter(pageNum: Int) {
            val footerY = (pageHeight - 30).toFloat()
            canvas.drawLine(margin.toFloat(), footerY - 8, (pageWidth - margin).toFloat(), footerY - 8, linePaint)
            val footerText = "Custos Animae • Confidential Personal Journal • Page $pageNum"
            canvas.drawText(footerText, margin.toFloat(), footerY + 8, metaPaint)
        }

        drawHeader()

        // Draw Content Map cleanly
        for ((key, value) in contentMap) {
            if (value.isBlank()) continue

            // 1. Draw section title / key
            val keyLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(key, 0, key.length, keyPaint, contentWidth)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0f, 1.1f)
                    .setIncludePad(false)
                    .build()
            } else {
                @Suppress("DEPRECATION")
                StaticLayout(key, keyPaint, contentWidth, Layout.Alignment.ALIGN_NORMAL, 1.1f, 0f, false)
            }

            val keyHeight = keyLayout.height

            // Page break check for Section Title
            if (yPosition + keyHeight + 40 > pageHeight - margin) {
                drawFooter(currentPageNumber)
                pdfDocument.finishPage(page)

                currentPageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                drawHeader()
            }

            canvas.save()
            canvas.translate(margin.toFloat(), yPosition)
            keyLayout.draw(canvas)
            canvas.restore()

            yPosition += keyHeight + 5f

            // Tiny line below section header
            canvas.drawLine(margin.toFloat(), yPosition, (pageWidth - margin).toFloat(), yPosition, linePaint)
            yPosition += 10f

            // 2. Draw paragraph(s) of value
            val paragraphs = value.split("\n")
            for (paragraph in paragraphs) {
                val trimmedPara = paragraph.trim()
                if (trimmedPara.isEmpty()) {
                    yPosition += 6f
                    continue
                }

                val bodyLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    StaticLayout.Builder.obtain(trimmedPara, 0, trimmedPara.length, bodyPaint, contentWidth)
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                        .setLineSpacing(0f, 1.15f)
                        .setIncludePad(false)
                        .build()
                } else {
                    @Suppress("DEPRECATION")
                    StaticLayout(trimmedPara, bodyPaint, contentWidth, Layout.Alignment.ALIGN_NORMAL, 1.15f, 0f, false)
                }

                val bodyHeight = bodyLayout.height
                if (yPosition + bodyHeight + 20 <= pageHeight - margin) {
                    // Fits perfectly on current page
                    canvas.save()
                    canvas.translate(margin.toFloat(), yPosition)
                    bodyLayout.draw(canvas)
                    canvas.restore()
                    yPosition += bodyHeight + 8f
                } else {
                    // Doesn't fit in one block, draw line-by-line with page-breaks
                    val lineCount = bodyLayout.lineCount
                    for (lineIdx in 0 until lineCount) {
                        val lineStart = bodyLayout.getLineStart(lineIdx)
                        val lineEnd = bodyLayout.getLineEnd(lineIdx)
                        val lineText = trimmedPara.substring(lineStart, lineEnd).trimEnd()

                        val lineHeight = bodyLayout.getLineBottom(lineIdx) - bodyLayout.getLineTop(lineIdx)

                        if (yPosition + lineHeight + 25 > pageHeight - margin) {
                            drawFooter(currentPageNumber)
                            pdfDocument.finishPage(page)

                            currentPageNumber++
                            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
                            page = pdfDocument.startPage(pageInfo)
                            canvas = page.canvas
                            drawHeader()
                        }

                        canvas.drawText(lineText, margin.toFloat(), yPosition + bodyPaint.textSize, bodyPaint)
                        yPosition += lineHeight + 1.5f
                    }
                    yPosition += 8f
                }
            }
            // Section spacer
            yPosition += 12f
        }

        drawFooter(currentPageNumber)
        pdfDocument.finishPage(page)

        // Save file
        val rootDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "Custos Animae/$folderName"
        )
        if (!rootDir.exists()) {
            rootDir.mkdirs()
        }

        val file = File(rootDir, "$fileName.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context, "PDF saved to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving PDF", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}
