package com.example.truelineapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
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
            ) {
                ListenerAvatar(
                    name = listenerName,
                    modifier = Modifier.fillMaxSize(),
                    fontSize = 56.sp
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

        // Middle Section (Floating Gift Button)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = { /* TODO: Trigger gifting flow */ },
                colors = ButtonDefaults.buttonColors(containerColor = TrueLineAccent), // STRICT: Amber for Gift CTA
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Gift", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Bottom Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 60.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            var isMuted by remember { mutableStateOf(false) }
            var isSpeakerOn by remember { mutableStateOf(true) }

            CallControlButton(
                icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic, 
                tint = if (isMuted) TrueLineAccent else Color.White,
                onClick = { isMuted = !isMuted }
            )
            
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

            CallControlButton(
                icon = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff, 
                tint = if (!isSpeakerOn) TrueLineAccent else Color.White,
                onClick = { isSpeakerOn = !isSpeakerOn }
            )
        }
    }
}

@Composable
fun CallControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    tint: Color,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.1f),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        }
    }
}
