package com.example.truelineapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TrueLineLogo(size: Dp = 40.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .border(BorderStroke(size * 0.05f, Secondary.copy(alpha = 0.4f)), CircleShape)
            .padding(size * 0.2f),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left bars
            LogoBar(heightFactor = 0.4f, color = Secondary)
            LogoBar(heightFactor = 0.7f, color = Secondary)
            // Center bar (Tallest)
            LogoBar(heightFactor = 1.0f, color = Accent)
            // Right bars
            LogoBar(heightFactor = 0.6f, color = Secondary)
            LogoBar(heightFactor = 0.3f, color = Secondary)
        }
    }
}

@Composable
private fun LogoBar(heightFactor: Float, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxHeight(heightFactor)
            .width(3.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color)
    )
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
    androidx.compose.material3.Surface(
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
