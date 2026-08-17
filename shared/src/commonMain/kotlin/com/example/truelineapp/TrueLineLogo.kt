package com.example.truelineapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import truelineapp.shared.generated.resources.Res
import truelineapp.shared.generated.resources.app_logo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface

@Composable
fun TrueLineLogo(size: Dp = 40.dp, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.size(size),
        color = Color(0xFF2D6A6B),
        shape = RoundedCornerShape(size * 0.26f),
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.size(size * 0.52f),
                shape = CircleShape,
                color = Color(0xFF235556),
                border = BorderStroke((size * 0.02f).coerceAtLeast(1.dp), Color.White.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = size * 0.08f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Bar 1 (Short White Pill)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.30f)
                            .width((size * 0.055f).coerceAtLeast(2.dp))
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    // Bar 2 (Medium White Pill)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.65f)
                            .width((size * 0.055f).coerceAtLeast(2.dp))
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    // Bar 3 (Tall Orange Pill)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.90f)
                            .width((size * 0.055f).coerceAtLeast(2.dp))
                            .clip(CircleShape)
                            .background(Color(0xFFEE9037))
                    )
                    // Bar 4 (Medium White Pill)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.55f)
                            .width((size * 0.055f).coerceAtLeast(2.dp))
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }
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
