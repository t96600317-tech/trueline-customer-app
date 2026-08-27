package com.example.truelineapp

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream

@Composable
actual fun rememberGalleryLauncher(onImagePicked: (String?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val destFile = File(context.filesDir, "user_profile_avatar.jpg")
                val outputStream = FileOutputStream(destFile)
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                onImagePicked(destFile.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
                onImagePicked(uri.toString())
            }
        } else {
            onImagePicked(null)
        }
    }
    return { launcher.launch("image/*") }
}

@Composable
actual fun rememberCameraLauncher(onImageCaptured: (String?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            try {
                val destFile = File(context.filesDir, "user_profile_avatar.jpg")
                val outputStream = FileOutputStream(destFile)
                outputStream.use { output ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
                }
                onImageCaptured(destFile.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
                onImageCaptured(null)
            }
        } else {
            onImageCaptured(null)
        }
    }
    return { launcher.launch(null) }
}
