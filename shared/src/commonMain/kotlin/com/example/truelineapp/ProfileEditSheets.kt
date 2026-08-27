package com.example.truelineapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
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
                colors = ButtonDefaults.buttonColors(containerColor = TrueLineAccent)
            ) {
                if (cost == 0) {
                    Text("Save Changes (Free)", fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Pay ", fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
                        CoinLogo(size = 16.dp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$cost & Save", fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoConfirmationBottomSheet(
    hasExistingPhoto: Boolean = false,
    userBalance: Int,
    onDismiss: () -> Unit,
    onAddCoins: () -> Unit,
    onRemovePhoto: () -> Unit = {},
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
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Surface(
                color = TrueLineLightBg,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text("Upload Cost:", fontSize = 15.sp, color = TrueLineDarkBg)
                    Spacer(modifier = Modifier.width(10.dp))
                    CoinLogo(size = 22.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("$cost Coins", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = TrueLinePrimary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (userBalance >= cost) onConfirm(cost) else onAddCoins()
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TrueLineAccent)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Choose Photo (Cost: ", fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
                    CoinLogo(size = 16.dp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$cost)", fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
                }
            }

            if (hasExistingPhoto) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onRemovePhoto,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFFEF2F2))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Remove Photo",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Remove Current Photo",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626),
                            fontSize = 15.sp
                        )
                    }
                }
            }
            
            TextButton(onClick = onDismiss, modifier = Modifier.padding(top = 8.dp)) {
                Text("Cancel", color = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoSourcePickerBottomSheet(
    hasExistingPhoto: Boolean = false,
    onDismiss: () -> Unit,
    onChooseFromGallery: () -> Unit,
    onTakeSelfie: () -> Unit,
    onRemovePhoto: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Select Photo Source", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Choose how you want to update your profile picture.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Option 1: Choose Photo from Gallery
            Surface(
                onClick = onChooseFromGallery,
                shape = RoundedCornerShape(16.dp),
                color = TrueLineLightBg,
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = TrueLinePrimary.copy(alpha = 0.1f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = TrueLinePrimary)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Choose Photo from Gallery", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TrueLineDarkBg)
                        Text("Select an existing photo from library", fontSize = 12.sp, color = Color.Gray)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Option 2: Take a Selfie
            Surface(
                onClick = onTakeSelfie,
                shape = RoundedCornerShape(16.dp),
                color = TrueLineLightBg,
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = TrueLineAccent.copy(alpha = 0.15f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = TrueLineAccent)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Take a Selfie", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TrueLineDarkBg)
                        Text("Capture a new selfie using camera", fontSize = 12.sp, color = Color.Gray)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                }
            }

            if (hasExistingPhoto) {
                Spacer(modifier = Modifier.height(12.dp))

                // Option 3: Remove Current Photo
                Surface(
                    onClick = onRemovePhoto,
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFDC2626).copy(alpha = 0.12f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color(0xFFDC2626))
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Remove Current Photo", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFDC2626))
                            Text("Reset to default avatar", fontSize = 12.sp, color = Color.Gray)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPreviewBottomSheet(
    photoPath: String,
    userBalance: Int,
    isCamera: Boolean = true,
    onDismiss: () -> Unit,
    onRetake: () -> Unit,
    onAddCoins: () -> Unit,
    onUpload: () -> Unit
) {
    val cost = 59

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (isCamera) "Preview Selfie" else "Preview Photo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrueLineDarkBg
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Photo Preview Circle
            UserAvatar(
                photoPath = photoPath,
                name = "User",
                size = 140.dp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Cost banner
            Surface(
                color = TrueLineLightBg,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Upload Cost:", fontSize = 15.sp, color = TrueLineDarkBg)
                    Spacer(modifier = Modifier.width(10.dp))
                    CoinLogo(size = 22.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("$cost Coins", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = TrueLinePrimary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons: Retake and Upload
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Retake Button
                OutlinedButton(
                    onClick = onRetake,
                    modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.5.dp, TrueLinePrimary),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Retake",
                            tint = TrueLinePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (isCamera) "Retake" else "Change",
                            fontWeight = FontWeight.Bold,
                            color = TrueLinePrimary,
                            fontSize = 15.sp
                        )
                    }
                }

                // Upload Button
                Button(
                    onClick = {
                        if (userBalance >= cost) onUpload() else onAddCoins()
                    },
                    modifier = Modifier.weight(1.3f).height(54.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TrueLineAccent)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Upload",
                            tint = TrueLineDarkBg,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Upload",
                            fontWeight = FontWeight.Bold,
                            color = TrueLineDarkBg,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    }
}
