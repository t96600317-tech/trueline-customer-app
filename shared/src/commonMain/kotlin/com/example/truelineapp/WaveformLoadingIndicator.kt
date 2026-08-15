package com.example.truelineapp

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WaveformLoadingIndicator(
    modifier: Modifier = Modifier,
    barWidth: Dp = 4.dp,
    maxBarHeight: Dp = 24.dp,
    gap: Dp = 3.dp
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    // Symmetrical heights based on the logo reference (shorter on edges, tallest in middle)
    val barCount = 7
    val baseHeights = listOf(0.3f, 0.5f, 0.8f, 1.0f, 0.8f, 0.5f, 0.3f)
    val colors = listOf(
        TrueLineSecondary, 
        TrueLineSecondary, 
        TrueLineSecondary, 
        TrueLineAccent, // Center is Amber
        TrueLineSecondary, 
        TrueLineSecondary, 
        TrueLineSecondary
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(gap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        baseHeights.forEachIndexed { index, baseScale ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400 + (index * 50), easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(maxBarHeight * baseScale * scale)
                    .clip(RoundedCornerShape(barWidth / 2))
                    .background(colors[index])
            )
        }
    }
}
