package com.example.truelineapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import truelineapp.shared.generated.resources.Res
import truelineapp.shared.generated.resources.app_logo

@Composable
fun TrueLineLogo(size: Dp = 40.dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.app_logo),
        contentDescription = "TrueLine Logo",
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.28f)),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun TrueLineBrandHeader(
    modifier: Modifier = Modifier,
    logoSize: Dp = 38.dp,
    titleSize: androidx.compose.ui.unit.TextUnit = 26.sp,
    showSubtitle: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TrueLineLogo(size = logoSize)
        Spacer(modifier = Modifier.width(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "True",
                fontSize = titleSize,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2A2E),
                letterSpacing = (-0.2).sp
            )
            Text(
                text = "Line",
                fontSize = titleSize,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF245255),
                letterSpacing = (-0.2).sp
            )
        }
    }
}

@Composable
fun TrueLineBrandText(fontSize: androidx.compose.ui.unit.TextUnit = 24.sp, textColor: Color = Color.White) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "True",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontSize = fontSize,
            letterSpacing = (-0.2).sp
        )
        Text(
            text = "Line",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF245255),
            fontSize = fontSize,
            letterSpacing = (-0.2).sp
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
