package com.example.truelineapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class LoginStep {
    ENTER_PHONE,
    ENTER_OTP
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    isLoading: Boolean = false,
    isSuccess: Boolean = false,
    errorMessage: String? = null,
    onSendOtp: (String) -> Unit,
    onVerifyOtp: (String, String) -> Unit,
    onLoginSuccess: () -> Unit, 
    onBack: () -> Unit
) {
    var step by remember { mutableStateOf(LoginStep.ENTER_PHONE) }
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            onLoginSuccess()
        }
    }

    Scaffold(
        containerColor = TrueLineLightBg, // Brand Light Bg
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        if (step == LoginStep.ENTER_OTP) {
                            step = LoginStep.ENTER_PHONE
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TrueLinePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Brand Logo or Icon
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFEBF2F3)), // Soft light gray/teal
                contentAlignment = Alignment.Center
            ) {
                Text(if (step == LoginStep.ENTER_PHONE) "📱" else "📩", fontSize = 44.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (step == LoginStep.ENTER_PHONE) "Verify your number" else "Verify OTP",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TrueLineDarkBg, // Deep dark teal
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (step == LoginStep.ENTER_PHONE) 
                    "We'll send a 6-digit OTP to verify your account." 
                    else "OTP sent to +91 $phoneNumber",
                fontSize = 16.sp,
                color = TrueLinePrimary.copy(alpha = 0.8f), // Deep dark teal subtext
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            if (step == LoginStep.ENTER_PHONE) {
                PhoneInputField(
                    value = phoneNumber,
                    onValueChange = { if (it.length <= 10) phoneNumber = it }
                )
            } else {
                OTPInputField(
                    value = otpCode,
                    onValueChange = { if (it.length <= 6) otpCode = it }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (step == LoginStep.ENTER_PHONE) {
                        if (phoneNumber.length < 10) {
                            // Local validation before API call
                        } else {
                            onSendOtp("+91$phoneNumber")
                            step = LoginStep.ENTER_OTP
                        }
                    } else {
                        if (otpCode.length == 6) {
                            onVerifyOtp("+91$phoneNumber", otpCode)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TrueLineAccent,
                    disabledContainerColor = TrueLineAccent
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
                    Text(
                        text = if (step == LoginStep.ENTER_PHONE) "Send OTP" else "Verify & Continue",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrueLineDarkBg 
                    )
                }
            }

            if (step == LoginStep.ENTER_OTP) {
                Spacer(modifier = Modifier.height(24.dp))
                
                TextButton(
                    onClick = { onSendOtp("+91$phoneNumber") },
                    enabled = !isLoading
                ) {
                    Text(
                        "Resend OTP", 
                        color = TrueLinePrimary, 
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                "By continuing, you agree to our Terms & Privacy Policy",
                fontSize = 12.sp,
                color = Color.Gray.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
fun PhoneInputField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp), // Updated to 12dp
        leadingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp, end = 8.dp)
            ) {
                Text(
                    "🇮🇳", 
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "+91", 
                    fontWeight = FontWeight.Bold, 
                    color = TrueLineDarkBg,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.LightGray))
            }
        },
        placeholder = { Text("Mobile Number", color = Color.Gray.copy(alpha = 0.5f)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 18.sp, 
            fontWeight = FontWeight.Bold,
            color = TrueLineDarkBg
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TrueLinePrimary,
            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.3f), // Subtle border
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = TrueLinePrimary,
            focusedTextColor = TrueLineDarkBg,
            unfocusedTextColor = TrueLineDarkBg
        )
    )
}

@Composable
fun OTPInputField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp), // Updated to 12dp
        placeholder = { 
            Text(
                "Enter 6-digit OTP", 
                textAlign = TextAlign.Center, 
                modifier = Modifier.fillMaxWidth(),
                color = Color.Gray.copy(alpha = 0.5f)
            ) 
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center, 
            fontSize = 22.sp, 
            fontWeight = FontWeight.Bold, 
            letterSpacing = 8.sp,
            color = TrueLinePrimary
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TrueLinePrimary,
            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.3f),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = TrueLinePrimary,
            focusedTextColor = TrueLinePrimary,
            unfocusedTextColor = TrueLinePrimary
        )
    )
}
