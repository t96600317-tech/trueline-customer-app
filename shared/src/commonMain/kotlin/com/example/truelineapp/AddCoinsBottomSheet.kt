package com.example.truelineapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CoinPackage(val coins: Int, val price: Int, val isPopular: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCoinsBottomSheet(
    listenerName: String,
    currentBalance: Int,
    onDismiss: () -> Unit,
    onAddCoins: (Int) -> Unit
) {
    val packages = listOf(
        CoinPackage(120, 49),
        CoinPackage(260, 99, isPopular = true),
        CoinPackage(530, 199)
    )
    
    var selectedPackage by remember { mutableStateOf(packages[1]) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Insufficient Balance",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TrueLineDarkBg
            )
            Text(
                text = "Add coins to connect with $listenerName",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                color = TrueLineLightBg,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Your Balance:", fontSize = 16.sp, color = TrueLineDarkBg)
                    Spacer(modifier = Modifier.width(12.dp))
                    CoinLogo(size = 20.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$currentBalance Coins", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TrueLinePrimary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(140.dp)
            ) {
                items(packages) { pkg ->
                    PackageCard(
                        pkg = pkg,
                        isSelected = selectedPackage == pkg,
                        onClick = { selectedPackage = pkg }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            var isLoading by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()

            Button(
                onClick = { 
                    scope.launch {
                        isLoading = true
                        delay(1500) // Buffer to show animation
                        onAddCoins(selectedPackage.coins)
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .navigationBarsPadding(), 
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
                    Text("Add Coins & Proceed", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PackageCard(pkg: CoinPackage, isSelected: Boolean, onClick: () -> Unit) {
    Box {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (pkg.isPopular) 10.dp else 0.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) Color.White else TrueLineLightBg,
            border = if (isSelected) BorderStroke(1.dp, TrueLineAccent) else BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoinLogo(size = 14.dp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = pkg.coins.toString(), 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp, 
                        color = TrueLineDarkBg
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹${pkg.price}", 
                    fontSize = 14.sp, 
                    color = TrueLineOnline,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        if (pkg.isPopular) {
            Surface(
                color = TrueLineAccent,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Text(
                    "Best value",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
