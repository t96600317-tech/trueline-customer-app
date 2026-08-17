package com.example.truelineapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import org.jetbrains.compose.resources.painterResource
import truelineapp.shared.generated.resources.Res
import truelineapp.shared.generated.resources.onboarding_talk

data class OnboardingItem(
    val title: String,
    val subtitle: String,
    val description: String,
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
            accentColor = TrueLinePrimary
        ),
        OnboardingItem(
            title = "100% ID-Verified.",
            subtitle = "Sahi log, sahi baat.",
            description = "Every listener completes government ID and face verification. No bots, no fake profiles, just real humans.",
            accentColor = TrueLineOnline
        ),
        OnboardingItem(
            title = "Transparent & Private.",
            subtitle = "Poori tarah private.",
            description = "No photos are ever shown. See the exact cost (₹3.43/min) before every call. Your identity stays safe.",
            accentColor = TrueLineAccent
        ),
        OnboardingItem(
            title = "Ready to talk?",
            subtitle = "Shuru karein?",
            description = "To start calling, we'll need microphone access. Your calls are always 1-on-1 and secure.",
            accentColor = TrueLinePrimary
        )
    )

    val pagerState = rememberPagerState(pageCount = { items.size })

    Box(modifier = Modifier.fillMaxSize().background(TrueLineLightBg)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPage(items[page], pageIndex = page)
        }

        // CONSTANT TOP HEADER (Logo + Brand Text stays stationary during page swipes)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 76.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TrueLineLogo(size = 110.dp)
            Spacer(modifier = Modifier.height(16.dp))
            TrueLineBrandText(fontSize = 32.sp, textColor = TrueLineDarkBg)
        }

        if (pagerState.currentPage < items.size - 1) {
            TextButton(
                onClick = onGetStarted,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 76.dp, end = 16.dp)
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
fun OnboardingIllustration(pageIndex: Int, accentColor: Color) {
    if (pageIndex == 0) {
        Image(
            painter = painterResource(Res.drawable.onboarding_talk),
            contentDescription = "Man and Woman talking on phone",
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp),
            contentScale = ContentScale.Fit
        )
    } else if (pageIndex == 3) {
        // Original concentric rings with mic emoji as it was originally
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(140.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.05f)))
            Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.05f)))
            
            Text(
                text = "🎙️",
                fontSize = 60.sp
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(136.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(104.dp),
                    shape = CircleShape,
                    color = accentColor,
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        when (pageIndex) {
                            1 -> Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Verified ID",
                                tint = Color.White,
                                modifier = Modifier.size(52.dp)
                            )
                            else -> Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Privacy Shield",
                                tint = Color.White,
                                modifier = Modifier.size(52.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(item: OnboardingItem, pageIndex: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.padding(top = 215.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnboardingIllustration(pageIndex = pageIndex, accentColor = item.accentColor)
        }

        Spacer(modifier = Modifier.height(28.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = item.description,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            lineHeight = 26.sp
        )
        
        Spacer(modifier = Modifier.height(60.dp)) 
    }
}
