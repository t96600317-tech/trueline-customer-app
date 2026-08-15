package com.example.truelineapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNameBottomSheet(
    currentName: String,
    isFirstTime: Boolean,
    userBalance: Int,
    onDismiss: () -> Unit,
    onAddCoins: () -> Unit,
    onSave: (newName: String, cost: Int) -> Unit
) {
    val cost = if (isFirstTime) 0 else 20
    var nameState by remember { mutableStateOf(currentName) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null
    ) {
        Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Edit Name", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = TrueLineLightBg,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Enter New Name", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.weight(1f))
                        if (isFirstTime) {
                            Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(6.dp)) {
                                Text("FREE", color = TrueLineOnline, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CoinLogo(size = 14.dp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("$cost", color = TrueLineDarkBg, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = nameState,
                        onValueChange = { nameState = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TrueLinePrimary,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = TrueLineDarkBg,
                            unfocusedTextColor = TrueLineDarkBg
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (userBalance >= cost) onSave(nameState, cost) else onAddCoins()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TrueLinePrimary)
            ) {
                if (cost == 0) {
                    Text("Save Changes (Free)", fontWeight = FontWeight.Bold)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Pay ", fontWeight = FontWeight.Bold)
                        CoinLogo(size = 16.dp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$cost & Save", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoConfirmationBottomSheet(
    userBalance: Int,
    onDismiss: () -> Unit,
    onAddCoins: () -> Unit,
    onConfirm: (cost: Int) -> Unit
) {
    val cost = 59

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null
    ) {
        Column(modifier = Modifier.padding(24.dp).navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Update Profile Photo", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Changing your profile picture costs coins.", fontSize = 14.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Surface(
                color = TrueLineLightBg,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text("Total Cost:", fontSize = 16.sp, color = TrueLineDarkBg)
                    Spacer(modifier = Modifier.width(12.dp))
                    CoinLogo(size = 24.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$cost Coins", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TrueLinePrimary)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (userBalance >= cost) onConfirm(cost) else onAddCoins()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TrueLinePrimary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Pay ", fontWeight = FontWeight.Bold)
                    CoinLogo(size = 16.dp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$cost & Choose Photo", fontWeight = FontWeight.Bold)
                }
            }
            
            TextButton(onClick = onDismiss, modifier = Modifier.padding(top = 8.dp)) {
                Text("Cancel", color = Color.Gray)
            }
        }
    }
}
