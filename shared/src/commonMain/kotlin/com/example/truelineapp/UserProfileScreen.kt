package com.example.truelineapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserProfileScreen(
    userName: String,
    userPhotoPath: String?,
    walletBalance: Int,
    isFirstTimeNameChange: Boolean,
    selectedLanguageCode: String,
    onLogout: () -> Unit,
    onAddCoins: () -> Unit,
    onUpdateName: (newName: String, cost: Int) -> Unit,
    onUpdatePhoto: (photoPath: String, cost: Int) -> Unit,
    onRemovePhoto: () -> Unit,
    onLanguageClick: () -> Unit,
    onPrivacySecurityClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showNameEdit by remember { mutableStateOf(false) }
    var showPhotoConfirm by remember { mutableStateOf(false) }
    var showPhotoSourcePicker by remember { mutableStateOf(false) }
    var pendingPhotoPath by remember { mutableStateOf<String?>(null) }
    var isCameraPreview by remember { mutableStateOf(true) }
    var showPhotoPreview by remember { mutableStateOf(false) }
    val hasExistingPhoto = !userPhotoPath.isNullOrBlank()

    val launchGallery = rememberGalleryLauncher { path ->
        if (!path.isNullOrBlank()) {
            pendingPhotoPath = path
            isCameraPreview = false
            showPhotoPreview = true
        }
    }

    val launchCamera = rememberCameraLauncher { path ->
        if (!path.isNullOrBlank()) {
            pendingPhotoPath = path
            isCameraPreview = true
            showPhotoPreview = true
        }
    }

    val strings = com.example.truelineapp.i18n.getAppStrings(selectedLanguageCode)

    if (showNameEdit) {
        EditNameBottomSheet(
            currentName = userName,
            isFirstTime = isFirstTimeNameChange,
            userBalance = walletBalance,
            selectedLanguageCode = selectedLanguageCode,
            onDismiss = { showNameEdit = false },
            onAddCoins = {
                showNameEdit = false
                onAddCoins()
            },
            onSave = { newName, cost ->
                onUpdateName(newName, cost)
                showNameEdit = false
            }
        )
    }

    if (showPhotoConfirm) {
        PhotoConfirmationBottomSheet(
            hasExistingPhoto = hasExistingPhoto,
            userBalance = walletBalance,
            selectedLanguageCode = selectedLanguageCode,
            onDismiss = { showPhotoConfirm = false },
            onAddCoins = {
                showPhotoConfirm = false
                onAddCoins()
            },
            onRemovePhoto = {
                showPhotoConfirm = false
                onRemovePhoto()
            },
            onConfirm = { cost ->
                showPhotoConfirm = false
                showPhotoSourcePicker = true
            }
        )
    }

    if (showPhotoSourcePicker) {
        PhotoSourcePickerBottomSheet(
            hasExistingPhoto = hasExistingPhoto,
            selectedLanguageCode = selectedLanguageCode,
            onDismiss = { showPhotoSourcePicker = false },
            onChooseFromGallery = {
                showPhotoSourcePicker = false
                launchGallery()
            },
            onTakeSelfie = {
                showPhotoSourcePicker = false
                launchCamera()
            },
            onRemovePhoto = {
                showPhotoSourcePicker = false
                onRemovePhoto()
            }
        )
    }

    if (showPhotoPreview && pendingPhotoPath != null) {
        PhotoPreviewBottomSheet(
            photoPath = pendingPhotoPath!!,
            userBalance = walletBalance,
            isCamera = isCameraPreview,
            selectedLanguageCode = selectedLanguageCode,
            onDismiss = {
                showPhotoPreview = false
                pendingPhotoPath = null
            },
            onRetake = {
                showPhotoPreview = false
                if (isCameraPreview) {
                    launchCamera()
                } else {
                    launchGallery()
                }
            },
            onAddCoins = {
                showPhotoPreview = false
                onAddCoins()
            },
            onUpload = {
                val path = pendingPhotoPath
                showPhotoPreview = false
                pendingPhotoPath = null
                if (!path.isNullOrBlank()) {
                    onUpdatePhoto(path, 59)
                }
            }
        )
    }

    val languageMap = mapOf(
        "en" to "English",
        "hi" to "Hindi",
        "bn" to "Bengali",
        "ta" to "Tamil",
        "te" to "Telugu",
        "mr" to "Marathi"
    )
    val displayLanguage = languageMap[selectedLanguageCode] ?: "English"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TrueLineLightBg)
            .verticalScroll(scrollState)
    ) {
        // --- HEADER SECTION ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(TrueLinePrimary, Color(0xFF1E4D4E))
                    )
                )
                .padding(top = 48.dp, bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Avatar with Camera Icon
                Box(contentAlignment = Alignment.BottomEnd) {
                    UserAvatar(
                        photoPath = userPhotoPath,
                        name = userName,
                        size = 100.dp
                    )
                    Surface(
                        modifier = Modifier.size(34.dp),
                        shape = CircleShape,
                        color = TrueLineAccent,
                        border = BorderStroke(2.dp, Color.White),
                        shadowElevation = 4.dp,
                        onClick = { showPhotoConfirm = true }
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Edit Photo",
                            tint = Color.White,
                            modifier = Modifier.padding(7.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name & Verified Badge & Edit Name Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showNameEdit = true }
                ) {
                    Text(
                        userName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = TrueLineOnline,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Name",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    "+91 98765 43210",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // --- SETTINGS GROUPS ---
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsGroup(title = strings.accountSettings) {
                SettingsItem(
                    icon = Icons.Default.Shield,
                    title = strings.privacyAndSecurity,
                    colorTint = Color(0xFFE8F5E9),
                    onClick = onPrivacySecurityClick
                )
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = strings.languagePreference,
                    subtitle = displayLanguage,
                    colorTint = Color(0xFFE3F2FD),
                    onClick = onLanguageClick
                )
            }

            SettingsGroup(title = strings.supportAndInformation) {
                SettingsItem(
                    icon = Icons.Default.HeadsetMic,
                    title = strings.customerSupport,
                    subtitle = strings.helpCenter24x7,
                    colorTint = Color(0xFFFFF3E0),
                    onClick = { /* TODO: Open support */ }
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogout() },
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFEBEE)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFD32F2F))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        strings.logout,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F),
                        fontSize = 16.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 0.5.dp,
            border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.2f))
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    colorTint: Color,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = colorTint
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = TrueLinePrimary, modifier = Modifier.size(20.dp))
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TrueLineDarkBg)
            if (subtitle != null) {
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
        }
        
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
    }
}
