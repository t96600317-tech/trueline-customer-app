package com.example.truelineapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.truelineapp.i18n.getAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNameBottomSheet(
    currentName: String,
    isFirstTime: Boolean,
    userBalance: Int,
    selectedLanguageCode: String = "en",
    onDismiss: () -> Unit,
    onAddCoins: () -> Unit,
    onSave: (newName: String, cost: Int) -> Unit
) {
    val strings = getAppStrings(selectedLanguageCode)
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
                Text(strings.editName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
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
                        Text(strings.enterNewName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.weight(1f))
                        if (isFirstTime) {
                            Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(6.dp)) {
                                Text(strings.freeBadge, color = TrueLineOnline, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
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
                    Text(strings.saveChangesFree, fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${strings.payAndSave.replace("%d", "$cost")}", fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
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
    selectedLanguageCode: String = "en",
    onDismiss: () -> Unit,
    onAddCoins: () -> Unit,
    onRemovePhoto: () -> Unit = {},
    onConfirm: (cost: Int) -> Unit
) {
    val strings = getAppStrings(selectedLanguageCode)
    val cost = 59

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null
    ) {
        Column(modifier = Modifier.padding(24.dp).navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(strings.updateProfilePhoto, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
            Spacer(modifier = Modifier.height(8.dp))
            Text(strings.changePhotoCostNotice, fontSize = 14.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Surface(
                color = TrueLineLightBg,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text(strings.uploadCost, fontSize = 15.sp, color = TrueLineDarkBg)
                    Spacer(modifier = Modifier.width(10.dp))
                    CoinLogo(size = 22.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("$cost ${strings.coins}", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = TrueLinePrimary)
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
                    Text(strings.choosePhotoCost.replace("%d", "$cost"), fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
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
                            strings.removeCurrentPhoto,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626),
                            fontSize = 15.sp
                        )
                    }
                }
            }
            
            TextButton(onClick = onDismiss, modifier = Modifier.padding(top = 8.dp)) {
                Text(strings.cancel, color = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoSourcePickerBottomSheet(
    hasExistingPhoto: Boolean = false,
    selectedLanguageCode: String = "en",
    onDismiss: () -> Unit,
    onChooseFromGallery: () -> Unit,
    onTakeSelfie: () -> Unit,
    onRemovePhoto: () -> Unit = {}
) {
    val strings = getAppStrings(selectedLanguageCode)

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
                Text(strings.selectPhotoSource, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                strings.chooseHowToUpdatePhoto,
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
                        Text(strings.chooseFromGallery, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TrueLineDarkBg)
                        Text(strings.chooseFromGallerySubtitle, fontSize = 12.sp, color = Color.Gray)
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
                        Text(strings.takeASelfie, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TrueLineDarkBg)
                        Text(strings.takeASelfieSubtitle, fontSize = 12.sp, color = Color.Gray)
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
                            Text(strings.removeCurrentPhoto, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFDC2626))
                            Text(strings.resetToDefaultAvatar, fontSize = 12.sp, color = Color.Gray)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = Color.Gray)
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
    selectedLanguageCode: String = "en",
    onDismiss: () -> Unit,
    onRetake: () -> Unit,
    onAddCoins: () -> Unit,
    onUpload: () -> Unit
) {
    val strings = getAppStrings(selectedLanguageCode)
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
                    if (isCamera) strings.previewSelfie else strings.previewPhoto,
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
                    Text(strings.uploadCost, fontSize = 15.sp, color = TrueLineDarkBg)
                    Spacer(modifier = Modifier.width(10.dp))
                    CoinLogo(size = 22.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("$cost ${strings.coins}", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = TrueLinePrimary)
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
                            if (isCamera) strings.retake else strings.change,
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
                            strings.upload,
                            fontWeight = FontWeight.Bold,
                            color = TrueLineDarkBg,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = Color.Gray)
            }
        }
    }
}

private fun encodeUriComponent(str: String): String {
    return str
        .replace("%", "%25")
        .replace(" ", "%20")
        .replace("\n", "%0A")
        .replace("\r", "")
        .replace("&", "%26")
        .replace("=", "%3D")
        .replace("?", "%3F")
        .replace("#", "%23")
        .replace("+", "%2B")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSupportBottomSheet(
    defaultName: String = "",
    defaultPhone: String = "",
    onDismiss: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    var name by remember { mutableStateOf(defaultName.ifBlank { "" }) }
    var phone by remember { mutableStateOf(defaultPhone.ifBlank { "" }) }
    var email by remember { mutableStateOf("") }
    var issueText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = CircleShape,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.HeadsetMic,
                                contentDescription = null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Customer Support",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrueLineDarkBg
                        )
                        Text(
                            text = "help@truelineapp.in · 24/7 Help",
                            fontSize = 12.5.sp,
                            color = Color.Gray
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isSubmitted) {
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFDCFCE7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Request Prepared",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF166534)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Your email app has been opened with your support request addressed to help@truelineapp.in. Please tap send in your email client.",
                            fontSize = 13.5.sp,
                            color = Color(0xFF1E293B),
                            lineHeight = 19.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = TrueLinePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Text(
                    text = "Please fill in the details below. Tapping submit will open your email app addressed to help@truelineapp.in.",
                    fontSize = 13.5.sp,
                    color = Color(0xFF475569),
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Real Name
                Text("1. Real Name", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        errorMessage = null
                    },
                    placeholder = { Text("Your full legal name", color = Color.Gray, fontSize = 14.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TrueLinePrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedTextColor = TrueLineDarkBg,
                        unfocusedTextColor = TrueLineDarkBg
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Mobile Number
                Text("2. Mobile Number", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { 
                        phone = it
                        errorMessage = null
                    },
                    placeholder = { Text("+91 98765 43210", color = Color.Gray, fontSize = 14.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TrueLinePrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedTextColor = TrueLineDarkBg,
                        unfocusedTextColor = TrueLineDarkBg
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Email ID
                Text("3. Email Address", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        errorMessage = null
                    },
                    placeholder = { Text("name@example.com", color = Color.Gray, fontSize = 14.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TrueLinePrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedTextColor = TrueLineDarkBg,
                        unfocusedTextColor = TrueLineDarkBg
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Issue Description Box
                Text("4. Describe Your Issue", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TrueLineDarkBg)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = issueText,
                    onValueChange = { 
                        issueText = it
                        errorMessage = null
                    },
                    placeholder = { Text("Write your question, payment issue, or feedback here...", color = Color.Gray, fontSize = 14.sp) },
                    minLines = 4,
                    maxLines = 7,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TrueLinePrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedTextColor = TrueLineDarkBg,
                        unfocusedTextColor = TrueLineDarkBg
                    )
                )

                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFDC2626),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (name.isBlank()) {
                            errorMessage = "Please enter your real name."
                            return@Button
                        }
                        if (phone.isBlank()) {
                            errorMessage = "Please enter your mobile number."
                            return@Button
                        }
                        if (email.isBlank() || !email.contains("@")) {
                            errorMessage = "Please enter a valid email address."
                            return@Button
                        }
                        if (issueText.isBlank()) {
                            errorMessage = "Please describe your issue."
                            return@Button
                        }

                        val subject = "Customer Support Request: ${name.trim()}"
                        val body = "Customer Support Ticket\n\n" +
                                "1. Real Name: ${name.trim()}\n" +
                                "2. Mobile Number: ${phone.trim()}\n" +
                                "3. Email Address: ${email.trim()}\n\n" +
                                "4. Issue Description:\n${issueText.trim()}\n\n" +
                                "---\nSent from TrueLine App"

                        val encodedSubject = encodeUriComponent(subject)
                        val encodedBody = encodeUriComponent(body)
                        val mailtoUrl = "mailto:help@truelineapp.in?subject=$encodedSubject&body=$encodedBody"

                        try {
                            uriHandler.openUri(mailtoUrl)
                            isSubmitted = true
                        } catch (e: Exception) {
                            errorMessage = "Could not open email app. Please write to help@truelineapp.in directly."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TrueLineAccent),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Submit Request",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrueLineDarkBg
                    )
                }
            }
        }
    }
}
