package com.example.truelineapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TrueLineLogo(size: Dp = 40.dp, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.25f))
    ) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val cy = h / 2f

        // 1. Outer Dark Teal Squircle Background
        drawRoundRect(
            color = Color(0xFF1E4E4E),
            size = this.size,
            cornerRadius = CornerRadius(w * 0.25f, h * 0.25f)
        )

        // 2. Inner Circular Ring
        val ringRadius = w * 0.36f
        val ringStrokeWidth = w * 0.038f
        drawCircle(
            color = Color(0xFF4D7E7F),
            radius = ringRadius,
            center = Offset(cx, cy),
            style = Stroke(width = ringStrokeWidth)
        )

        // 3. 5 Vertical Pill Soundwave Bars
        val barWidth = w * 0.076f
        val cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
        val dx = w * 0.108f

        val bars = listOf(
            Triple(-2, w * 0.18f, Color(0xFF5DA8D8)), // Soft Sky Blue (Left Outer)
            Triple(-1, w * 0.44f, Color(0xFFFFFFFF)), // White (Left Inner)
            Triple(0,  w * 0.58f, Color(0xFFF39C38)), // Warm Amber/Orange (Center)
            Triple(1,  w * 0.44f, Color(0xFFFFFFFF)), // White (Right Inner)
            Triple(2,  w * 0.18f, Color(0xFF5DA8D8))  // Soft Sky Blue (Right Outer)
        )

        for ((idx, barHeight, color) in bars) {
            val bx = cx + idx * dx
            val left = bx - barWidth / 2f
            val top = cy - barHeight / 2f
            drawRoundRect(
                color = color,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = cornerRadius
            )
        }
    }
}

@Composable
fun TrueLineBrandHeader(
    modifier: Modifier = Modifier,
    logoSize: Dp = 38.dp,
    titleSize: androidx.compose.ui.unit.TextUnit = 28.sp,
    showSubtitle: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TrueLineLogo(size = logoSize)
        Spacer(modifier = Modifier.width(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "True",
                fontSize = titleSize,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A),
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Line",
                fontSize = titleSize,
                fontWeight = FontWeight.Bold,
                color = TrueLinePrimary,
                letterSpacing = (-0.5).sp
            )
        }
    }
}

@Composable
fun TrueLineBrandText(fontSize: androidx.compose.ui.unit.TextUnit = 24.sp, textColor: Color = Color.White) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "True",
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
            fontSize = fontSize,
            letterSpacing = (-0.5).sp
        )
        Text(
            text = "Line",
            fontWeight = FontWeight.Normal,
            color = Secondary,
            fontSize = fontSize,
            letterSpacing = (-0.5).sp
        )
    }
}

@Composable
fun CoinLogo(size: Dp) {
    Surface(
        modifier = Modifier.size(size),
        color = Accent,
        shape = RoundedCornerShape(size * 0.25f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(size * 0.2f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.fillMaxHeight(0.5f).width(size * 0.08f).background(Color.Black.copy(alpha = 0.6f)))
            Box(modifier = Modifier.fillMaxHeight(1.0f).width(size * 0.08f).background(Color.Black.copy(alpha = 0.6f)))
            Box(modifier = Modifier.fillMaxHeight(0.6f).width(size * 0.08f).background(Color.Black.copy(alpha = 0.6f)))
        }
    }
}
