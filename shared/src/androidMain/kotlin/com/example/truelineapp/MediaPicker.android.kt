package com.example.truelineapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun normalizeAndSaveImage(context: Context, inputStream: InputStream): String? {
    return try {
        val bytes = inputStream.readBytes()
        if (bytes.isEmpty()) return null

        val tempExifFile = File(context.cacheDir, "temp_gallery_${System.currentTimeMillis()}.jpg")
        tempExifFile.writeBytes(bytes)

        var degrees = 0f
        try {
            val exif = ExifInterface(tempExifFile.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            degrees = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            tempExifFile.delete()
        }

        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
        if (degrees != 0f) {
            val matrix = Matrix()
            matrix.postRotate(degrees)
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (rotated != bitmap) {
                bitmap.recycle()
                bitmap = rotated
            }
        }

        // Clean up previous user avatar files
        context.filesDir.listFiles()?.forEach { file ->
            if (file.name.startsWith("user_avatar_") && file.name.endsWith(".jpg")) {
                file.delete()
            }
        }

        val destFile = File(context.filesDir, "user_avatar_${System.currentTimeMillis()}.jpg")
        FileOutputStream(destFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }
        destFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun normalizeCameraFile(context: Context, capturedFile: File): String? {
    return try {
        if (!capturedFile.exists() || capturedFile.length() == 0L) return null

        var degrees = 0f
        try {
            val exif = ExifInterface(capturedFile.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            degrees = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var bitmap = BitmapFactory.decodeFile(capturedFile.absolutePath) ?: return null
        if (degrees != 0f) {
            val matrix = Matrix()
            matrix.postRotate(degrees)
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (rotated != bitmap) {
                bitmap.recycle()
                bitmap = rotated
            }
        }

        context.filesDir.listFiles()?.forEach { file ->
            if (file.name.startsWith("user_avatar_") && file.name.endsWith(".jpg")) {
                file.delete()
            }
        }

        val destFile = File(context.filesDir, "user_avatar_${System.currentTimeMillis()}.jpg")
        FileOutputStream(destFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }
        capturedFile.delete()
        destFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
actual fun rememberGalleryLauncher(onImagePicked: (String?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val savedPath = normalizeAndSaveImage(context, inputStream)
                    onImagePicked(savedPath)
                } else {
                    onImagePicked(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onImagePicked(null)
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
    val tempCameraFile = remember { mutableStateOf<File?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val file = tempCameraFile.value
            if (file != null) {
                val savedPath = normalizeCameraFile(context, file)
                onImageCaptured(savedPath)
            } else {
                onImageCaptured(null)
            }
        } else {
            tempCameraFile.value?.delete()
            onImageCaptured(null)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val tempFile = File(context.cacheDir, "camera_capture_${System.currentTimeMillis()}.jpg")
                tempCameraFile.value = tempFile
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                e.printStackTrace()
                onImageCaptured(null)
            }
        }
    }

    return {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            try {
                val tempFile = File(context.cacheDir, "camera_capture_${System.currentTimeMillis()}.jpg")
                tempCameraFile.value = tempFile
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                e.printStackTrace()
                onImageCaptured(null)
            }
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}
