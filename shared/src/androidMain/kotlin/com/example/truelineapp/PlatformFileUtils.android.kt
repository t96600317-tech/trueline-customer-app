package com.example.truelineapp

import java.io.File

actual fun currentPlatformTimeMillis(): Long = System.currentTimeMillis()

actual fun deleteLocalFile(path: String) {
    runCatching { File(path).delete() }
}
