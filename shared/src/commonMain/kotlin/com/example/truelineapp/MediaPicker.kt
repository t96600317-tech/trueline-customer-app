package com.example.truelineapp

import androidx.compose.runtime.Composable

@Composable
expect fun rememberGalleryLauncher(onImagePicked: (String?) -> Unit): () -> Unit

@Composable
expect fun rememberCameraLauncher(onImageCaptured: (String?) -> Unit): () -> Unit
