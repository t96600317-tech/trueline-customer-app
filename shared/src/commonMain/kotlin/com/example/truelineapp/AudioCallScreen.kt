package com.example.truelineapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import truelineapp.shared.generated.resources.Res
import truelineapp.shared.generated.resources.profile_girl

@Composable
fun AudioCallScreen(listenerName: String, onHangUp: () -> Unit) {
    var callDuration by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            callDuration++
        }
    }

    val minutes = (callDuration / 60).toString().padStart(2, '0')
    val seconds = (callDuration % 60).toString().padStart(2, '0')

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TrueLineDarkBg), // STRICT: Dark background for calls
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Info
        Column(
            modifier = Modifier.padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Image(
                    painter = painterResource(Res.drawable.profile_girl),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = listenerName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$minutes:$seconds",
                fontSize = 18.sp,
                color = TrueLineOnline,
                fontWeight = FontWeight.Medium
            )
        }

        // Bottom Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 60.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CallControlButton(icon = Icons.Default.Mic, tint = Color.White)
            
            IconButton(
                onClick = onHangUp,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE53935))
            ) {
                Icon(
                    Icons.Default.CallEnd,
                    contentDescription = "Hang up",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            CallControlButton(icon = Icons.Default.VolumeUp, tint = Color.White)
        }
    }
}

@Composable
fun CallControlButton(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color) {
    Surface(
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.1f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        }
    }
}
