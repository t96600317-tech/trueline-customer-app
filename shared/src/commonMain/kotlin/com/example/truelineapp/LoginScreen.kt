package com.example.truelineapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.truelineapp.ui.TrueLineWaveformLoader

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
    otpCountdown: Int = 30,
    canResendOtp: Boolean = false,
    onSendOtp: (phone: String, onSuccess: () -> Unit) -> Unit,
    onVerifyOtp: (phone: String, otp: String) -> Unit,
    onResendOtp: () -> Unit = {},
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var step by remember { mutableStateOf(LoginStep.ENTER_PHONE) }
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val otpFocusRequester = remember { FocusRequester() }

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

    LaunchedEffect(step) {
        if (step == LoginStep.ENTER_OTP) {
            otpFocusRequester.requestFocus()
        }
    }

    Scaffold(
        containerColor = TrueLineLightBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        if (step == LoginStep.ENTER_OTP) {
                            step = LoginStep.ENTER_PHONE
                            otpCode = ""
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TrueLineDarkBg
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Brand Icon Box
                Surface(
                    modifier = Modifier.size(76.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = TrueLineAccent.copy(alpha = 0.18f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (step == LoginStep.ENTER_PHONE) Icons.Filled.Phone else Icons.Filled.Security,
                            contentDescription = null,
                            tint = TrueLineDarkBg,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (step == LoginStep.ENTER_PHONE) "Enter your mobile number" else "Verify OTP Code",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrueLineDarkBg,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (step == LoginStep.ENTER_PHONE)
                        "We will send a 6-digit verification code to connect safely."
                    else
                        "Enter the 6-digit OTP code sent to +91 $phoneNumber",
                    fontSize = 14.sp,
                    color = TrueLineTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (step == LoginStep.ENTER_PHONE) {
                    // Mobile Number Input Box
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Country Code Pill
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = TrueLinePrimary.copy(alpha = 0.08f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🇮🇳", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("+91", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TrueLineDarkBg)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            TextField(
                                value = phoneNumber,
                                onValueChange = { input ->
                                    val digitsOnly = input.filter { it.isDigit() }
                                    if (digitsOnly.length <= 10) phoneNumber = digitsOnly
                                },
                                placeholder = { Text("98765 43210", color = Color(0xFFA0AEC0)) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        if (phoneNumber.length == 10) {
                                            onSendOtp("+91$phoneNumber") {
                                                step = LoginStep.ENTER_OTP
                                            }
                                        }
                                    }
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = TrueLinePrimary
                                ),
                                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TrueLineDarkBg),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    // Segmented 6-Box OTP Display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { otpFocusRequester.requestFocus() },
                        contentAlignment = Alignment.Center
                    ) {
                        BasicTextField(
                            value = otpCode,
                            onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }.take(6)
                                otpCode = digits
                                if (digits.length == 6) {
                                    focusManager.clearFocus()
                                    onVerifyOtp("+91$phoneNumber", digits)
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    if (otpCode.length == 6) {
                                        onVerifyOtp("+91$phoneNumber", otpCode)
                                    }
                                }
                            ),
                            modifier = Modifier
                                .focusRequester(otpFocusRequester)
                                .size(1.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 0 until 6) {
                                val char = otpCode.getOrNull(i)?.toString() ?: ""
                                val isFocused = otpCode.length == i

                                Surface(
                                    modifier = Modifier
                                        .width(48.dp)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White,
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isFocused) 2.dp else 1.dp,
                                        color = if (isFocused) TrueLineAccent else if (char.isNotEmpty()) TrueLineAccent.copy(alpha = 0.7f) else Color(0xFFE2E8F0)
                                    ),
                                    shadowElevation = if (isFocused) 3.dp else 0.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = char,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TrueLineDarkBg
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!canResendOtp) {
                            Text(
                                text = "Resend OTP in ${otpCountdown}s",
                                fontSize = 14.sp,
                                color = TrueLineTextSecondary
                            )
                        } else {
                            TextButton(onClick = onResendOtp) {
                                Text(
                                    text = "Resend OTP",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TrueLinePrimary
                                )
                            }
                        }
                    }
                }
            }

            // Bottom CTA Button (Yellow Accent Color!)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (step == LoginStep.ENTER_PHONE) {
                            if (phoneNumber.length == 10) {
                                onSendOtp("+91$phoneNumber") {
                                    step = LoginStep.ENTER_OTP
                                }
                            }
                        } else {
                            if (otpCode.length == 6) {
                                onVerifyOtp("+91$phoneNumber", otpCode)
                            }
                        }
                    },
                    enabled = !isLoading && (
                        (step == LoginStep.ENTER_PHONE && phoneNumber.length == 10) ||
                        (step == LoginStep.ENTER_OTP && otpCode.length == 6)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TrueLineAccent,
                        disabledContainerColor = TrueLineAccent.copy(alpha = 0.4f)
                    )
                ) {
                    if (isLoading) {
                        TrueLineWaveformLoader(
                            size = 24.dp,
                            barColor = TrueLineDarkBg,
                            accentColor = TrueLinePrimary
                        )
                    } else {
                        Text(
                            text = if (step == LoginStep.ENTER_PHONE) "Get OTP" else "Verify & Continue",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrueLineDarkBg
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "By continuing, you agree to TrueLine's Terms of Service & Privacy Policy",
                    fontSize = 11.sp,
                    color = TrueLineTextSecondary.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
