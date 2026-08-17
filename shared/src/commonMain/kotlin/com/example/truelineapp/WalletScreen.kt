package com.example.truelineapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WalletScreen(balance: Int, onBack: () -> Unit, onRecharge: (Int) -> Unit) {
    var selectedPackageAmount by remember { mutableIntStateOf(260) }
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = TrueLineLightBg,
        bottomBar = {
            var isLoading by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TrueLineLightBg)
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TrustPoint(text = "100% real humans, never AI")
                    Spacer(modifier = Modifier.width(16.dp))
                    TrustPoint(text = "No autopay, ever")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Row 2
                TrustPoint(text = "Delete your account any time")
                
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { 
                        scope.launch {
                            isLoading = true
                            delay(1500) // Simulation
                            onRecharge(selectedPackageAmount)
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TrueLinePrimary,
                        disabledContainerColor = TrueLinePrimary
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        WaveformLoadingIndicator(
                            maxBarHeight = 32.dp,
                            barWidth = 5.dp,
                            gap = 4.dp
                        )
                    } else {
                        Text("Proceed to Recharge", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TrueLineDarkBg)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "My Wallet",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrueLineDarkBg
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- BALANCE DISPLAY ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Current Balance",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoinLogo(size = 36.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = balance.toString(),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TrueLineDarkBg
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- RECHARGE PACKS TITLE ---
            Text(
                "Choose a recharge pack",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TrueLineDarkBg
            )
            Text(
                "9 coins per minute (prices incl. GST)",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- RECHARGE PACKS GRID/ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RechargePackCard(
                    coins = 130, 
                    price = "₹49", 
                    isSelected = selectedPackageAmount == 130,
                    onClick = { selectedPackageAmount = 130 },
                    modifier = Modifier.weight(1f)
                )
                RechargePackCard(
                    coins = 260, 
                    price = "₹99", 
                    isBestValue = true,
                    isSelected = selectedPackageAmount == 260,
                    onClick = { selectedPackageAmount = 260 },
                    modifier = Modifier.weight(1.1f)
                )
                RechargePackCard(
                    coins = 530, 
                    price = "₹199", 
                    isSelected = selectedPackageAmount == 530,
                    onClick = { selectedPackageAmount = 530 },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TrustPoint(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "✓", 
            color = Color(0xFF13B59D), // Specific teal for checkmark
            fontSize = 14.sp, 
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text, 
            color = Color(0xFF4B5563), // Muted dark grey
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun RechargePackCard(
    coins: Int,
    price: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBestValue: Boolean = false
) {
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (isBestValue) 12.dp else 0.dp)
                .clickable { onClick() },
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
            shape = RoundedCornerShape(16.dp),
            border = if (isSelected) BorderStroke(1.dp, TrueLineAccent) else BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f)),
            shadowElevation = if (isSelected) 4.dp else 0.dp
        ) {
            Column(
                modifier = Modifier.padding(vertical = 24.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoinLogo(size = 18.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = coins.toString(), 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 20.sp, 
                        color = TrueLineDarkBg
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = price, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 16.sp, 
                    color = TrueLineOnline 
                )
            }
        }

        if (isBestValue) {
            Surface(
                color = TrueLineAccent,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Text(
                    "Best value",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}
