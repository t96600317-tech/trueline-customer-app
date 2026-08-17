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

data class CoinPackage(val id: String, val coins: Int, val price: Int, val isPopular: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCoinsBottomSheet(
    listenerName: String,
    currentBalance: Int,
    onDismiss: () -> Unit,
    onAddCoins: (CoinPackage) -> Unit
) {
    val packages = listOf(
        CoinPackage("pack_49", 130, 49),
        CoinPackage("pack_99", 260, 99, isPopular = true),
        CoinPackage("pack_199", 530, 199)
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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onAddCoins(selectedPackage) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TrueLinePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Pay ₹${selectedPackage.price} for ${selectedPackage.coins} Coins",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PackageCard(
    pkg: CoinPackage,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) TrueLinePrimary.copy(alpha = 0.08f) else Color.White,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) TrueLinePrimary else Color.LightGray.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp)
        ) {
            if (pkg.isPopular) {
                Surface(
                    color = TrueLineSecondary,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = "POPULAR",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                CoinLogo(size = 14.dp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${pkg.coins}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TrueLineDarkBg
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "₹${pkg.price}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TrueLinePrimary
            )
        }
    }
}
