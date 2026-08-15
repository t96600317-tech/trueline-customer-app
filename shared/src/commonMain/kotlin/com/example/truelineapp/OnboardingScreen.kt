package com.example.truelineapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class OnboardingItem(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: String,
    val accentColor: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val items = listOf(
        OnboardingItem(
            title = "Someone who actually listens.",
            subtitle = "Koi jo aapki baat sune.",
            description = "TrueLine connects you with real people who are here to listen, not just hear. Voice-first calling for India.",
            icon = "🎧",
            accentColor = TrueLinePrimary
        ),
        OnboardingItem(
            title = "100% ID-Verified.",
            subtitle = "Sahi log, sahi baat.",
            description = "Every listener completes government ID and face verification. No bots, no fake profiles, just real humans.",
            icon = "✅",
            accentColor = TrueLineOnline
        ),
        OnboardingItem(
            title = "Transparent & Private.",
            subtitle = "Poori tarah private.",
            description = "No photos are ever shown. See the exact cost (₹3.43/min) before every call. Your identity stays safe.",
            icon = "🛡️",
            accentColor = TrueLineAccent
        ),
        OnboardingItem(
            title = "Ready to talk?",
            subtitle = "Shuru karein?",
            description = "To start calling, we'll need microphone access. Your calls are always 1-on-1 and secure.",
            icon = "🎙️",
            accentColor = TrueLinePrimary
        )
    )

    val pagerState = rememberPagerState(pageCount = { items.size })

    Box(modifier = Modifier.fillMaxSize().background(TrueLineLightBg)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPage(items[page])
        }

        if (pagerState.currentPage < items.size - 1) {
            TextButton(
                onClick = onGetStarted,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, end = 16.dp)
            ) {
                Text("Skip", color = Color.Gray, fontWeight = FontWeight.SemiBold)
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(items.size) { iteration ->
                    val isSelected = pagerState.currentPage == iteration
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) TrueLinePrimary else Color.LightGray.copy(alpha = 0.5f))
                            .size(if (isSelected) 10.dp else 8.dp)
                    )
                }
            }

            Button(
                onClick = {
                    if (pagerState.currentPage == items.size - 1) {
                        onGetStarted()
                    } else {
                        onGetStarted() 
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TrueLinePrimary)
            ) {
                Text(
                    text = if (pagerState.currentPage == items.size - 1) "Get Started" else "Next",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun OnboardingPage(item: OnboardingItem) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TrueLineLogo(size = 120.dp)
            Spacer(modifier = Modifier.height(24.dp))
            TrueLineBrandText(fontSize = 32.sp, textColor = TrueLineDarkBg)
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(item.accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(140.dp).clip(CircleShape).background(item.accentColor.copy(alpha = 0.05f)))
                Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(item.accentColor.copy(alpha = 0.05f)))
                
                Text(
                    text = item.icon,
                    fontSize = 60.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = item.subtitle,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TrueLinePrimary,
            textAlign = TextAlign.Center,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.title,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp,
            color = TrueLineDarkBg
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = item.description,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            lineHeight = 26.sp
        )
        
        Spacer(modifier = Modifier.height(100.dp)) 
    }
}
