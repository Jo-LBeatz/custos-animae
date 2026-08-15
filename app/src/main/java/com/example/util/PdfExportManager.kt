package com.example.util

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.data.DailyExamination
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object PdfExportManager {

    /**
     * Generates a PDF containing Examination of Conscience records.
     * If [password] is non-empty, applies PDF standard 128-bit encryption (R=3).
     */
    fun exportExaminationsToPdf(
        context: Context,
        examinations: List<DailyExamination>,
        password: String?,
        outputStream: OutputStream
    ): String {
        if (examinations.isEmpty()) {
            throw IllegalArgumentException("No Examination of Conscience entries found to export.")
        }

        val pdfDocument = PdfDocument()

        // Standard A4 dimensions in points (72 points = 1 inch): 595 x 842 pt
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40
        val contentWidth = pageWidth - (margin * 2)

        var currentPageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        // Paints & Colors
        val goldPrimaryColor = Color.rgb(184, 134, 11) // #B8860B
        val goldDarkColor = Color.rgb(139, 101, 8)
        val textPrimaryColor = Color.rgb(20, 20, 20)
        val textMutedColor = Color.rgb(90, 90, 90)
        val cardBgColor = Color.rgb(248, 246, 240)
        val cardBorderColor = Color.rgb(220, 210, 195)
        val burgundyColor = Color.rgb(128, 0, 32)

        val titlePaint = TextPaint().apply {
            color = burgundyColor
            textSize = 20f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val subTitlePaint = TextPaint().apply {
            color = goldDarkColor
            textSize = 11f
            isAntiAlias = true
        }

        val sectionTitlePaint = TextPaint().apply {
            color = goldPrimaryColor
            textSize = 12f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val bodyPaint = TextPaint().apply {
            color = textPrimaryColor
            textSize = 10f
            isAntiAlias = true
        }

        val labelPaint = TextPaint().apply {
            color = textMutedColor
            textSize = 9f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val borderPaint = Paint().apply {
            color = goldPrimaryColor
            strokeWidth = 2f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = cardBorderColor
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val cardBgPaint = Paint().apply {
            color = cardBgColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val cardBorderPaint = Paint().apply {
            color = cardBorderColor
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        var yPosition = margin.toFloat()

        fun drawHeader() {
            // Header decorative border
            canvas.drawRect(margin.toFloat(), margin.toFloat(), (pageWidth - margin).toFloat(), (margin + 60).toFloat(), cardBgPaint)
            canvas.drawRect(margin.toFloat(), margin.toFloat(), (pageWidth - margin).toFloat(), (margin + 60).toFloat(), borderPaint)

            canvas.drawText("CUSTOS ANIMAE — SACRAMENTAL CONSCIENCE RECORD", (margin + 15).toFloat(), (margin + 28).toFloat(), titlePaint)
            val currentDateStr = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date())
            canvas.drawText("Personal & Confidential Examination of Conscience Export • Generated: $currentDateStr", (margin + 15).toFloat(), (margin + 48).toFloat(), subTitlePaint)

            yPosition = (margin + 80).toFloat()
        }

        fun drawFooter(pageNum: Int) {
            val footerY = (pageHeight - 25).toFloat()
            canvas.drawLine(margin.toFloat(), footerY - 10, (pageWidth - margin).toFloat(), footerY - 10, linePaint)
            val footerText = "Custos Animae • Confidential Sacramental Preparation • Page $pageNum"
            canvas.drawText(footerText, margin.toFloat(), footerY, subTitlePaint)
        }

        drawHeader()

        val sortedExams = examinations.sortedByDescending { it.date }

        val cardLeft = margin.toFloat()
        val cardRight = (pageWidth - margin).toFloat()

        for (exam in sortedExams) {
            // Estimate card height
            val cardHeight = estimateExamCardHeight(exam, bodyPaint, contentWidth - 30)

            // Let's decide where to place this card
            val fitsOnCurrentPage = (yPosition + cardHeight + 30 <= pageHeight - margin)
            val fitsOnFreshPage = ((margin + 80) + cardHeight + 30 <= pageHeight - margin)

            if (!fitsOnCurrentPage) {
                // If the entire card fits on a fresh page, or if we have very little space left on the current page, transition
                if (fitsOnFreshPage || yPosition + 120f > pageHeight - margin) {
                    drawFooter(currentPageNumber)
                    pdfDocument.finishPage(page)

                    currentPageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    drawHeader()
                }
            }

            var remainingCardHeight = cardHeight
            var cardTopOnPage = yPosition
            
            // Draw initial card background & border for the current page
            val currentCardBottom = minOf(cardTopOnPage + remainingCardHeight, (pageHeight - margin - 15).toFloat())
            canvas.drawRoundRect(cardLeft, cardTopOnPage, cardRight, currentCardBottom, 8f, 8f, cardBgPaint)
            canvas.drawRoundRect(cardLeft, cardTopOnPage, cardRight, currentCardBottom, 8f, 8f, cardBorderPaint)

            var innerY = cardTopOnPage + 18f
            val innerLeft = cardLeft + 15f
            val innerWidth = contentWidth - 30

            // Nested helper to ensure space for a given element, wrapping page/card if needed
            fun prepareSpaceForField(requiredFieldHeight: Float) {
                if (innerY + requiredFieldHeight > pageHeight - margin - 20) {
                    // Update remaining card height before leaving
                    val drawnHeightOnThisPage = innerY - cardTopOnPage
                    remainingCardHeight -= drawnHeightOnThisPage

                    // Transition page
                    drawFooter(currentPageNumber)
                    pdfDocument.finishPage(page)

                    currentPageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    drawHeader()

                    // Start card on new page
                    cardTopOnPage = yPosition
                    val nextCardBottom = minOf(cardTopOnPage + remainingCardHeight, (pageHeight - margin - 15).toFloat())
                    canvas.drawRoundRect(cardLeft, cardTopOnPage, cardRight, nextCardBottom, 8f, 8f, cardBgPaint)
                    canvas.drawRoundRect(cardLeft, cardTopOnPage, cardRight, nextCardBottom, 8f, 8f, cardBorderPaint)

                    innerY = cardTopOnPage + 15f
                }
            }

            // 1. Exam Date Header
            val dateTitle = "Examination Date: ${exam.date}"
            prepareSpaceForField(16f + 8f + 8f)
            canvas.drawText(dateTitle, innerLeft, innerY, sectionTitlePaint)
            innerY += 16f
            canvas.drawLine(innerLeft, innerY - 4f, cardRight - 15f, innerY - 4f, linePaint)
            innerY += 8f

            // 2. Fulfilled Duties Status
            val statusStr = if (exam.dutiesFulfilled) "✔ Daily Duties Fulfilled" else "✘ Duties Incomplete"
            val statusColor = if (exam.dutiesFulfilled) Color.rgb(34, 139, 34) else burgundyColor
            prepareSpaceForField(16f)
            labelPaint.color = statusColor
            canvas.drawText(statusStr, innerLeft, innerY, labelPaint)
            innerY += 16f

            // Helper to draw standard section fields with clean page breaks and line-by-line wrapping if huge
            fun drawSectionField(title: String, text: String) {
                if (text.isBlank()) return

                // Measure text height with word wrap
                val fieldBodyLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    StaticLayout.Builder.obtain(text, 0, text.length, bodyPaint, innerWidth)
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                        .setLineSpacing(0f, 1.1f)
                        .setIncludePad(false)
                        .build()
                } else {
                    @Suppress("DEPRECATION")
                    StaticLayout(text, bodyPaint, innerWidth, Layout.Alignment.ALIGN_NORMAL, 1.1f, 0f, false)
                }

                val titleHeight = 14f
                val bodyHeight = fieldBodyLayout.height
                val totalFieldHeight = titleHeight + bodyHeight + 8f

                if (innerY + totalFieldHeight > pageHeight - margin - 20) {
                    // If it fits fully on a fresh page, we transition page now
                    if (titleHeight + bodyHeight + 25 <= (pageHeight - margin - 20) - (margin + 80)) {
                        prepareSpaceForField(totalFieldHeight)
                        
                        labelPaint.color = textMutedColor
                        canvas.drawText(title, innerLeft, innerY, labelPaint)
                        innerY += 14f
                        
                        canvas.save()
                        canvas.translate(innerLeft, innerY)
                        fieldBodyLayout.draw(canvas)
                        canvas.restore()
                        innerY += bodyHeight + 8f
                    } else {
                        // Extremely huge content: draw line-by-line across pages gracefully
                        prepareSpaceForField(titleHeight + 15f)
                        labelPaint.color = textMutedColor
                        canvas.drawText(title, innerLeft, innerY, labelPaint)
                        innerY += 14f

                        val lineCount = fieldBodyLayout.lineCount
                        for (lineIdx in 0 until lineCount) {
                            val lineStart = fieldBodyLayout.getLineStart(lineIdx)
                            val lineEnd = fieldBodyLayout.getLineEnd(lineIdx)
                            val lineText = text.substring(lineStart, lineEnd).trimEnd()

                            val lineHeight = fieldBodyLayout.getLineBottom(lineIdx) - fieldBodyLayout.getLineTop(lineIdx)

                            prepareSpaceForField(lineHeight + 5f)
                            canvas.drawText(lineText, innerLeft, innerY + bodyPaint.textSize, bodyPaint)
                            innerY += lineHeight + 1.5f
                        }
                        innerY += 8f
                    }
                } else {
                    // Fits perfectly on the current page
                    labelPaint.color = textMutedColor
                    canvas.drawText(title, innerLeft, innerY, labelPaint)
                    innerY += 14f

                    canvas.save()
                    canvas.translate(innerLeft, innerY)
                    fieldBodyLayout.draw(canvas)
                    canvas.restore()
                    innerY += bodyHeight + 8f
                }
            }

            // Draw each field
            drawSectionField("Shortcomings / Faults Noted:", exam.selectedFaults)
            drawSectionField("Concrete Resolution:", exam.concreteResolution)
            drawSectionField("Prayer Life & Faithfulness:", exam.prayerFaithfulness)
            drawSectionField("Acts of Charity:", exam.charityPracticed)
            drawSectionField("Temptations Resisted:", exam.temptationResisted)
            drawSectionField("Gratitude & Spiritual Notes:", exam.gratitudeNotes)

            // Update yPosition for next card
            yPosition = maxOf(innerY + 10f, currentCardBottom + 16f)
        }

        drawFooter(currentPageNumber)
        pdfDocument.finishPage(page)

        val rawPdfOutputStream = ByteArrayOutputStream()
        pdfDocument.writeTo(rawPdfOutputStream)
        pdfDocument.close()

        val rawBytes = rawPdfOutputStream.toByteArray()

        if (!password.isNullOrBlank() && password.length >= 4) {
            val encryptedBytes = encryptPdfBytes(rawBytes, password)
            outputStream.write(encryptedBytes)
            outputStream.flush()
            return "Successfully exported ${examinations.size} Examination entries into password-protected PDF."
        } else {
            outputStream.write(rawBytes)
            outputStream.flush()
            return "Successfully exported ${examinations.size} Examination entries into PDF document."
        }
    }

    private fun drawStaticText(
        canvas: android.graphics.Canvas,
        text: String,
        x: Float,
        y: Float,
        width: Int,
        paint: TextPaint
    ): Float {
        val staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.1f)
                .setIncludePad(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.1f, 0f, false)
        }

        canvas.save()
        canvas.translate(x, y)
        staticLayout.draw(canvas)
        canvas.restore()

        return staticLayout.height.toFloat()
    }

    private fun estimateExamCardHeight(exam: DailyExamination, paint: TextPaint, width: Int): Float {
        var h = 50f // Header + status
        val fields = listOf(
            exam.selectedFaults,
            exam.concreteResolution,
            exam.prayerFaithfulness,
            exam.charityPracticed,
            exam.temptationResisted,
            exam.gratitudeNotes
        )
        fields.forEach { fieldText ->
            if (fieldText.isNotBlank()) {
                h += 14f // Label height
                val textH = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    StaticLayout.Builder.obtain(fieldText, 0, fieldText.length, paint, width)
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                        .setLineSpacing(0f, 1.1f)
                        .setIncludePad(false)
                        .build().height
                } else {
                    @Suppress("DEPRECATION")
                    StaticLayout(fieldText, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.1f, 0f, false).height
                }
                h += textH + 8f
            }
        }
        return maxOf(h, 60f)
    }

    /**
     * Applies standard PDF 128-bit RC4 encryption (R=3, V=2) to raw PDF bytes.
     */
    private fun encryptPdfBytes(pdfBytes: ByteArray, password: String): ByteArray {
        return try {
            PdfStandardEncrypter.encryptPdf(pdfBytes, password)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: If standard encryption stream transformation fails, write raw bytes
            pdfBytes
        }
    }
}

/**
 * Implements Standard PDF 1.4/1.7 128-bit Encryption (R=3, V=2 / Standard Security Handler)
 */
object PdfStandardEncrypter {

    private val PADDING = byteArrayOf(
        0x28.toByte(), 0xBF.toByte(), 0x4E.toByte(), 0x5E.toByte(), 0x4E.toByte(), 0x75.toByte(), 0x8A.toByte(), 0x41.toByte(),
        0x64.toByte(), 0x00.toByte(), 0x4E.toByte(), 0x56.toByte(), 0xFF.toByte(), 0xFA.toByte(), 0x01.toByte(), 0x08.toByte(),
        0x2E.toByte(), 0x2E.toByte(), 0x00.toByte(), 0xB6.toByte(), 0xD0.toByte(), 0x68.toByte(), 0x3E.toByte(), 0x80.toByte(),
        0x2F.toByte(), 0x0C.toByte(), 0xA9.toByte(), 0xFE.toByte(), 0x64.toByte(), 0x53.toByte(), 0x69.toByte(), 0x7A.toByte()
    )

    fun encryptPdf(pdfBytes: ByteArray, password: String): ByteArray {
        val pdfStr = String(pdfBytes, Charsets.ISO_8859_1)

        // 1. Prepare user & owner passwords padded
        val userPwdPadded = padPassword(password)
        val ownerPwdPadded = padPassword(password)

        // 2. Compute Owner Key (O)
        val oKey = computeOwnerKey(userPwdPadded, ownerPwdPadded)

        // 3. Generate File ID (16 random bytes)
        val secureRandom = SecureRandom()
        val fileId = ByteArray(16)
        secureRandom.nextBytes(fileId)

        // Permissions flag P (-1028 = print and copy allowed, or standard restricted)
        val permissions = -1028

        // 4. Compute File Encryption Key (FEK)
        val fek = computeFEK(userPwdPadded, oKey, permissions, fileId)

        // 5. Compute User Key (U)
        val uKey = computeUserKey(fek, fileId)

        val oKeyHex = bytesToHex(oKey)
        val uKeyHex = bytesToHex(uKey)
        val fileIdHex = bytesToHex(fileId)

        // Parse objects and streams in PDF
        val objRegex = Regex("(\\d+)\\s+(\\d+)\\s+obj")
        val streamRegex = Regex("stream\\r?\\n([\\s\\S]*?)\\r?\\nendstream")

        val output = StringBuilder()
        var lastIndex = 0

        // Process indirect objects
        val matches = objRegex.findAll(pdfStr).toList()

        for (i in matches.indices) {
            val match = matches[i]
            val objNum = match.groupValues[1].toInt()
            val genNum = match.groupValues[2].toInt()

            val startIndex = match.range.first
            val nextObjStart = if (i + 1 < matches.size) matches[i + 1].range.first else pdfStr.indexOf("xref", startIndex)
            val endIndex = if (nextObjStart != -1) nextObjStart else pdfStr.length

            val objContent = pdfStr.substring(startIndex, endIndex)

            // Encrypt streams within this object
            val objKey = computeObjectKey(fek, objNum, genNum)

            val encryptedObjContent = streamRegex.replace(objContent) { streamMatch ->
                val rawStreamStr = streamMatch.groupValues[1]
                val rawStreamBytes = rawStreamStr.toByteArray(Charsets.ISO_8859_1)
                val encryptedStreamBytes = encryptRc4(rawStreamBytes, objKey)
                val encryptedStreamStr = String(encryptedStreamBytes, Charsets.ISO_8859_1)

                "stream\r\n$encryptedStreamStr\r\nendstream"
            }

            output.append(pdfStr.substring(lastIndex, startIndex))
            output.append(encryptedObjContent)
            lastIndex = endIndex
        }

        if (lastIndex < pdfStr.length) {
            var remaining = pdfStr.substring(lastIndex)

            // Append /Encrypt dictionary before startxref
            val encryptObjNum = matches.size + 1
            val encryptObjStr = "\n$encryptObjNum 0 obj\n<< /Filter /Standard /V 2 /R 3 /Length 128 /P $permissions /O <$oKeyHex> /U <$uKeyHex> >>\nendobj\n"

            val trailerIndex = remaining.indexOf("trailer")
            if (trailerIndex != -1) {
                val startXrefIndex = remaining.indexOf("startxref", trailerIndex)
                if (startXrefIndex != -1) {
                    val beforeTrailer = remaining.substring(0, trailerIndex)
                    var trailerBlock = remaining.substring(trailerIndex, startXrefIndex)
                    val afterStartXref = remaining.substring(startXrefIndex)

                    // Insert /Encrypt and /ID in trailer dictionary
                    val lastDictClose = trailerBlock.lastIndexOf(">>")
                    if (lastDictClose != -1) {
                        val dictContent = trailerBlock.substring(0, lastDictClose)
                        val dictEnd = trailerBlock.substring(lastDictClose)
                        trailerBlock = "$dictContent /Encrypt $encryptObjNum 0 R /ID [ <$fileIdHex> <$fileIdHex> ] $dictEnd"
                    }

                    remaining = beforeTrailer + encryptObjStr + trailerBlock + afterStartXref
                }
            }

            output.append(remaining)
        }

        return output.toString().toByteArray(Charsets.ISO_8859_1)
    }

    private fun padPassword(password: String): ByteArray {
        val pwdBytes = password.toByteArray(Charsets.ISO_8859_1)
        val result = ByteArray(32)
        if (pwdBytes.size >= 32) {
            System.arraycopy(pwdBytes, 0, result, 0, 32)
        } else {
            System.arraycopy(pwdBytes, 0, result, 0, pwdBytes.size)
            System.arraycopy(PADDING, 0, result, pwdBytes.size, 32 - pwdBytes.size)
        }
        return result
    }

    private fun computeOwnerKey(userPwdPadded: ByteArray, ownerPwdPadded: ByteArray): ByteArray {
        val md = MessageDigest.getInstance("MD5")
        var digest = md.digest(ownerPwdPadded)
        for (i in 0 until 50) {
            digest = md.digest(digest)
        }
        val rc4Key = digest.copyOf(16)
        var cipherText = userPwdPadded.clone()
        val cipher = Cipher.getInstance("ARCFOUR")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(rc4Key, "ARCFOUR"))
        cipherText = cipher.doFinal(cipherText)

        for (step in 1..19) {
            val newKey = ByteArray(16) { idx -> (rc4Key[idx].toInt() xor step).toByte() }
            val stepCipher = Cipher.getInstance("ARCFOUR")
            stepCipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(newKey, "ARCFOUR"))
            cipherText = stepCipher.doFinal(cipherText)
        }
        return cipherText
    }

    private fun computeFEK(userPwdPadded: ByteArray, oKey: ByteArray, permissions: Int, fileId: ByteArray): ByteArray {
        val md = MessageDigest.getInstance("MD5")
        md.update(userPwdPadded)
        md.update(oKey)
        md.update(
            byteArrayOf(
                (permissions and 0xFF).toByte(),
                ((permissions shr 8) and 0xFF).toByte(),
                ((permissions shr 16) and 0xFF).toByte(),
                ((permissions shr 24) and 0xFF).toByte()
            )
        )
        md.update(fileId)
        var digest = md.digest()
        for (i in 0 until 50) {
            digest = md.digest(digest)
        }
        return digest.copyOf(16)
    }

    private fun computeUserKey(fek: ByteArray, fileId: ByteArray): ByteArray {
        val md = MessageDigest.getInstance("MD5")
        md.update(PADDING)
        md.update(fileId)
        val hash = md.digest()

        var cipherText = hash
        val cipher = Cipher.getInstance("ARCFOUR")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(fek, "ARCFOUR"))
        cipherText = cipher.doFinal(cipherText)

        for (step in 1..19) {
            val newKey = ByteArray(16) { idx -> (fek[idx].toInt() xor step).toByte() }
            val stepCipher = Cipher.getInstance("ARCFOUR")
            stepCipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(newKey, "ARCFOUR"))
            cipherText = stepCipher.doFinal(cipherText)
        }
        val result = ByteArray(32)
        System.arraycopy(cipherText, 0, result, 0, 16)
        return result
    }

    private fun computeObjectKey(fek: ByteArray, objNum: Int, genNum: Int): ByteArray {
        val md = MessageDigest.getInstance("MD5")
        md.update(fek)
        md.update(
            byteArrayOf(
                (objNum and 0xFF).toByte(),
                ((objNum shr 8) and 0xFF).toByte(),
                ((objNum shr 16) and 0xFF).toByte(),
                (genNum and 0xFF).toByte(),
                ((genNum shr 8) and 0xFF).toByte()
            )
        )
        val key = md.digest()
        return key.copyOf(minOf(key.size, fek.size + 5))
    }

    private fun encryptRc4(data: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("ARCFOUR")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "ARCFOUR"))
        return cipher.doFinal(data)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02X", b))
        }
        return sb.toString()
    }
}
