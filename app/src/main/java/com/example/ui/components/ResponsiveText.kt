package com.example.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun ResponsiveText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    minFontSize: TextUnit = 8.sp,
    maxLines: Int = 3,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null
) {
    val targetFontSize = if (fontSize != TextUnit.Unspecified) fontSize else style.fontSize
    var currentFontSize by remember(text, targetFontSize) { mutableStateOf(targetFontSize) }
    var readyToDraw by remember(text, targetFontSize) { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        style = style.copy(fontSize = currentFontSize),
        fontWeight = fontWeight,
        maxLines = maxLines,
        softWrap = true,
        textAlign = textAlign,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = { textLayoutResult ->
            if ((textLayoutResult.didOverflowWidth || textLayoutResult.didOverflowHeight) && currentFontSize.value > minFontSize.value) {
                currentFontSize *= 0.9f
            } else {
                readyToDraw = true
            }
        }
    )
}
