package com.example.truelineapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import truelineapp.shared.generated.resources.Res
import truelineapp.shared.generated.resources.onboarding_talk

data class OnboardingSlide(
    val titleNormal: String,
    val titleHighlight: String,
    val subtitle: String,
    val description: String,
    val accentColor: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val items = listOf(
        OnboardingSlide(
            titleNormal = "Someone who",
            titleHighlight = "actually listens.",
            subtitle = "Koi jo aapki baat sune",
            description = "TrueLine connects you with real people who are here to listen, not just hear. Voice-first calling for India.",
            accentColor = Primary
        ),
        OnboardingSlide(
            titleNormal = "100% ID-Verified",
            titleHighlight = "real listeners.",
            subtitle = "Sahi log, sahi baat",
            description = "Every listener completes government ID and face verification. No bots, no fake profiles, just verified listeners.",
            accentColor = OnlineSuccess
        ),
        OnboardingSlide(
            titleNormal = "Transparent &",
            titleHighlight = "100% Private.",
            subtitle = "Poori tarah private",
            description = "No photos or personal details are ever shown. See exact costs before every call. Your identity stays completely safe.",
            accentColor = Accent
        ),
        OnboardingSlide(
            titleNormal = "Ready to connect &",
            titleHighlight = "start talking?",
            subtitle = "Shuru karein?",
            description = "To start calling, we'll need microphone access. Your calls are always 1-on-1, confidential and secure.",
            accentColor = Primary
        )
    )

    val pagerState = rememberPagerState(pageCount = { items.size })

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Light
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. TOP HEADER (Brand Logo + Skip Button)
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TrueLineBrandHeader(
                        logoSize = 44.dp,
                        titleSize = 22.sp
                    )

                    if (pagerState.currentPage < items.size - 1) {
                        TextButton(
                            onClick = onGetStarted,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Text(
                                text = "Skip",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. SEGMENTED PROGRESS BAR (matching trueline_listener design)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (index in 0 until items.size) {
                        val isActive = index <= pagerState.currentPage
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isActive) Primary else BorderSubtle)
                        )
                    }
                }

                // Step Indicator Label
                Text(
                    text = "STEP ${pagerState.currentPage + 1} OF ${items.size}",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.0.sp
                )
            }

            // 3. HORIZONTAL PAGER CONTENT
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                OnboardingSlideContent(slide = items[page], pageIndex = page)
            }

            // 4. BOTTOM ACTION BUTTON & TRUST FOOTER
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        if (pagerState.currentPage == items.size - 1) {
                            onGetStarted()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage == items.size - 1) "Get Started" else "Next",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Empathetic, anonymous & 100% private voice network.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun OnboardingSlideContent(slide: OnboardingSlide, pageIndex: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Dynamic Illustration Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp),
            contentAlignment = Alignment.Center
        ) {
            when (pageIndex) {
                0 -> {
                    // Seamless half-body illustration
                    Image(
                        painter = painterResource(Res.drawable.onboarding_talk),
                        contentDescription = "Man and Woman on phone call",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                1 -> {
                    // ID Verified Shield Card
                    Surface(
                        modifier = Modifier.size(130.dp),
                        shape = CircleShape,
                        color = SurfaceWhite,
                        border = BorderStroke(2.dp, OnlineSuccess.copy(alpha = 0.4f)),
                        shadowElevation = 6.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(OnlineSuccess.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.VerifiedUser,
                                    contentDescription = "Verified ID",
                                    tint = OnlineSuccess,
                                    modifier = Modifier.size(52.dp)
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // 100% Privacy Shield Card
                    Surface(
                        modifier = Modifier.size(130.dp),
                        shape = CircleShape,
                        color = SurfaceWhite,
                        border = BorderStroke(2.dp, Accent.copy(alpha = 0.4f)),
                        shadowElevation = 6.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(Accent.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Shield,
                                    contentDescription = "Privacy Shield",
                                    tint = Accent,
                                    modifier = Modifier.size(52.dp)
                                )
                            }
                        }
                    }
                }
                else -> {
                    // Microphone Voice Wave Card
                    Surface(
                        modifier = Modifier.size(130.dp),
                        shape = CircleShape,
                        color = SurfaceWhite,
                        border = BorderStroke(2.dp, Primary.copy(alpha = 0.35f)),
                        shadowElevation = 6.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(Primary.copy(alpha = 0.10f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Mic,
                                    contentDescription = "Microphone",
                                    tint = Primary,
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hindi Subtitle Pill
        Surface(
            color = Primary.copy(alpha = 0.08f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = slide.subtitle,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Two-Tone Heading (matching trueline_listener typography)
        val headingText = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp
                )
            ) {
                append("${slide.titleNormal} ")
            }
            withStyle(
                SpanStyle(
                    color = Primary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp
                )
            ) {
                append(slide.titleHighlight)
            }
        }

        Text(
            text = headingText,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Description Body Text
        Text(
            text = slide.description,
            fontSize = 14.5.sp,
            textAlign = TextAlign.Center,
            color = TextSecondary,
            lineHeight = 21.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}
