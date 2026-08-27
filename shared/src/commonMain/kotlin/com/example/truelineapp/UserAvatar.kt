package com.example.truelineapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
expect fun UserAvatar(
    photoPath: String?,
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
)
