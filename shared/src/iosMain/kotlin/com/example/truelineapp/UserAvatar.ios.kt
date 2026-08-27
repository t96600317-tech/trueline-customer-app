package com.example.truelineapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
actual fun UserAvatar(
    photoPath: String?,
    name: String,
    modifier: Modifier,
    size: Dp
) {
    val initial = name.trim().take(1).uppercase().ifBlank { "U" }
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.2f),
        border = BorderStroke(3.dp, Color.White),
        shadowElevation = 3.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF0D9488), Color(0xFF14B8A6))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                fontSize = (size.value * 0.42f).sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
