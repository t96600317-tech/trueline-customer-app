package com.example.truelineapp

import androidx.compose.runtime.Composable

@Composable
actual fun rememberGalleryLauncher(onImagePicked: (String?) -> Unit): () -> Unit {
    return { onImagePicked("ios_gallery_picked") }
}

@Composable
actual fun rememberCameraLauncher(onImageCaptured: (Boolean) -> Unit): () -> Unit {
    return { onImageCaptured(true) }
}
