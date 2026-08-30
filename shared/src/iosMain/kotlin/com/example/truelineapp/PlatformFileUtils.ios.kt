package com.example.truelineapp

import platform.Foundation.NSDate
import platform.Foundation.NSFileManager

actual fun currentPlatformTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1_000).toLong()

actual fun deleteLocalFile(path: String) {
    NSFileManager.defaultManager.removeItemAtPath(path, error = null)
}
