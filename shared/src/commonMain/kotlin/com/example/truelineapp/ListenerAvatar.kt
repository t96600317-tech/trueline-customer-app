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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ListenerAvatar(
    name: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 22.sp,
    isOnline: Boolean = false,
    badgeSize: Dp = 14.dp,
    backgroundColor: Color? = null,
    textColor: Color = Color.White
) {
    val initial = name.trim().take(1).uppercase().ifBlank { "?" }

    val gradientPalettes = listOf(
        listOf(Color(0xFF0D9488), Color(0xFF14B8A6)), // Teal
        listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)), // Indigo
        listOf(Color(0xFFF59E0B), Color(0xFFEA580C)), // Warm Amber
        listOf(Color(0xFFEC4899), Color(0xFFF43F5E)), // Rose Pink
        listOf(Color(0xFF2563EB), Color(0xFF06B6D4)), // Ocean Blue
        listOf(Color(0xFF059669), Color(0xFF10B981)), // Emerald
        listOf(Color(0xFF7C3AED), Color(0xFFA855F7))  // Royal Purple
    )
    val colorIndex = kotlin.math.abs(name.hashCode()) % gradientPalettes.size

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            shadowElevation = 2.dp,
            border = BorderStroke(1.5.dp, Color.White),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (backgroundColor != null) {
                            Modifier.background(backgroundColor)
                        } else {
                            Modifier.background(Brush.linearGradient(gradientPalettes[colorIndex]))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    color = textColor,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (isOnline) {
            Surface(
                shape = CircleShape,
                color = TrueLineOnline,
                border = BorderStroke(2.dp, Color.White),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .size(badgeSize)
                    .align(Alignment.BottomEnd)
            ) {}
        }
    }
}
