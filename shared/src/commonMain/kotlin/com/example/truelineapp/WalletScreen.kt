package com.example.truelineapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.truelineapp.network.customer.TransactionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    balance: Int,
    isProcessing: Boolean = false,
    transactions: List<TransactionItem> = emptyList(),
    isTransactionsLoading: Boolean = false,
    selectedLanguageCode: String = "en",
    onBack: () -> Unit,
    onRecharge: (amountPaise: Long, coins: Long) -> Unit,
    onRefreshTransactions: () -> Unit = {}
) {
    val strings = com.example.truelineapp.i18n.getAppStrings(selectedLanguageCode)
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Top Up, 1: History
    var selectedPackageAmount by remember { mutableIntStateOf(260) }

    val packageMap = mapOf(
        130 to (4900L to 130L),
        260 to (9900L to 260L),
        530 to (19900L to 530L)
    )

    LaunchedEffect(selectedTab) {
        if (selectedTab == 1) {
            onRefreshTransactions()
        }
    }

    Scaffold(
        containerColor = TrueLineLightBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.walletTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrueLineDarkBg
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TrueLineDarkBg
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            if (selectedTab == 0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TrueLineLightBg)
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val selectedPrice = when (selectedPackageAmount) {
                        130 -> "₹49"
                        260 -> "₹99"
                        530 -> "₹199"
                        else -> "₹99"
                    }

                    Button(
                        onClick = {
                            val pair = packageMap[selectedPackageAmount] ?: (9900L to 260L)
                            onRecharge(pair.first, pair.second)
                        },
                        enabled = !isProcessing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TrueLineAccent,
                            disabledContainerColor = TrueLineAccent.copy(alpha = 0.4f)
                        )
                    ) {
                        if (isProcessing) {
                            com.example.truelineapp.ui.TrueLineWaveformLoader(
                                size = 24.dp,
                                barColor = TrueLineDarkBg,
                                accentColor = TrueLinePrimary
                            )
                        } else {
                            Text("Recharge $selectedPrice", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            // --- BALANCE CARD ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = TrueLineDarkBg,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Available Coin Balance",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CoinLogo(size = 34.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = balance.toString(),
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val estMins = balance / 9
                    Surface(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "≈ $estMins minutes of call time",
                            fontSize = 12.sp,
                            color = TrueLineAccent,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tab Switcher
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                divider = {},
                indicator = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Top Up Coins",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) TrueLinePrimary else TrueLineTextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Coin Ledger",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) TrueLinePrimary else TrueLineTextSecondary
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // Top Up Pack Section
                Text(
                    text = "Select a coin pack",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrueLineDarkBg
                )
                Text(
                    text = "Standard rate: 9 coins / min. No expiry on coins.",
                    fontSize = 12.sp,
                    color = TrueLineTextSecondary,
                    modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModernRechargeCard(
                        coins = 130,
                        price = "₹49",
                        tag = null,
                        isSelected = selectedPackageAmount == 130,
                        onClick = { selectedPackageAmount = 130 },
                        modifier = Modifier.weight(1f)
                    )
                    ModernRechargeCard(
                        coins = 260,
                        price = "₹99",
                        tag = "Most Popular",
                        isSelected = selectedPackageAmount == 260,
                        onClick = { selectedPackageAmount = 260 },
                        modifier = Modifier.weight(1.05f)
                    )
                    ModernRechargeCard(
                        coins = 530,
                        price = "₹199",
                        tag = "Best Value",
                        isSelected = selectedPackageAmount == 530,
                        onClick = { selectedPackageAmount = 530 },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Trust Badges
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        WalletTrustRow(text = "100% Real verified human listeners, never AI")
                        WalletTrustRow(text = "No recurring charges or auto-debits, ever")
                        WalletTrustRow(text = "Unused coins never expire in your wallet")
                    }
                }
            } else {
                // Transaction History List
                if (isTransactionsLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        com.example.truelineapp.ui.TrueLineWaveformLoader(size = 38.dp)
                    }
                } else if (transactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.History, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No transactions yet", color = TrueLineTextSecondary, fontSize = 15.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(transactions) { tx ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(if (tx.type == "credit") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (tx.type == "credit") Icons.Filled.Add else Icons.Filled.Phone,
                                                contentDescription = null,
                                                tint = if (tx.type == "credit") Color(0xFF2E7D32) else Color(0xFFC62828),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(tx.description, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TrueLineDarkBg)
                                            Text(tx.created_at.take(10), fontSize = 11.sp, color = Color.Gray)
                                        }
                                    }
                                    Text(
                                        text = "${if (tx.type == "credit") "+" else "-"}${tx.amount.toInt()} coins",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (tx.type == "credit") Color(0xFF2E7D32) else TrueLineDarkBg
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernRechargeCard(
    coins: Int,
    price: String,
    tag: String?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (tag != null) 10.dp else 0.dp)
                .clickable { onClick() },
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) TrueLinePrimary else Color(0xFFE2E8F0)
            ),
            shadowElevation = if (isSelected) 4.dp else 0.dp
        ) {
            Column(
                modifier = Modifier.padding(vertical = 18.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoinLogo(size = 18.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = coins.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = TrueLineDarkBg
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = price,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TrueLinePrimary
                )
            }
        }

        if (tag != null) {
            Surface(
                color = if (tag == "Most Popular") TrueLineAccent else TrueLinePrimary,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Text(
                    text = tag,
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun WalletTrustRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("✓", color = TrueLinePrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = TrueLineDarkBg.copy(alpha = 0.85f), fontSize = 12.sp)
    }
}
