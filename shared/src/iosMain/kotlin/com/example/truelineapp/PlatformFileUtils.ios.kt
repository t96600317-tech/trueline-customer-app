package com.example.truelineapp

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import kotlin.time.Clock

actual fun currentPlatformTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()

@OptIn(ExperimentalForeignApi::class)
actual fun deleteLocalFile(path: String) {
    NSFileManager.defaultManager.removeItemAtPath(path, error = null)
}
