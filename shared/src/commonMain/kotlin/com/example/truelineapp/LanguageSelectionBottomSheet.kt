package com.example.truelineapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Language(val code: String, val name: String, val nativeName: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionBottomSheet(
    selectedLanguageCode: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    val languages = listOf(
        Language("en", "English", "English"),
        Language("hi", "Hindi", "हिन्दी"),
        Language("bn", "Bengali", "বাংলা"),
        Language("ta", "Tamil", "தமிழ்"),
        Language("te", "Telugu", "తెలుగు"),
        Language("mr", "Marathi", "मराठी")
    )

    val strings = com.example.truelineapp.i18n.getAppStrings(selectedLanguageCode)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = strings.languagePreference,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TrueLineDarkBg
            )
            Spacer(modifier = Modifier.height(16.dp))

            languages.forEach { language ->
                LanguageItem(
                    language = language,
                    isSelected = language.code == selectedLanguageCode,
                    onClick = {
                        onLanguageSelected(language.code)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
fun LanguageItem(
    language: Language,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = language.name,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TrueLinePrimary else TrueLineDarkBg
            )
            Text(
                text = language.nativeName,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                tint = TrueLinePrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
