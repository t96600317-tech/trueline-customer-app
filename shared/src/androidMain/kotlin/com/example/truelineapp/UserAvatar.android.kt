package com.example.truelineapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

fun decodeBitmapWithExif(filePath: String): Bitmap? {
    return try {
        val file = File(filePath)
        if (!file.exists() || file.length() == 0L) return null

        var degrees = 0f
        try {
            val exif = ExifInterface(file.absolutePath)
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

        var bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return null
        if (degrees != 0f) {
            val matrix = Matrix()
            matrix.postRotate(degrees)
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (rotated != bitmap) {
                bitmap.recycle()
                bitmap = rotated
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
actual fun UserAvatar(
    photoPath: String?,
    name: String,
    modifier: Modifier,
    size: Dp
) {
    val bitmap = remember(photoPath) {
        if (!photoPath.isNullOrBlank()) {
            decodeBitmapWithExif(photoPath)?.asImageBitmap()
        } else null
    }

    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.2f),
        border = BorderStroke(3.dp, Color.White),
        shadowElevation = 3.dp
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "User Avatar",
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            val initial = name.trim().take(1).uppercase().ifBlank { "U" }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF0D9488), Color(0xFF14B8A6))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    fontSize = (size.value * 0.42f).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
