package com.example.truelineapp

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter

actual fun getCurrentTimeFormatted(): String {
    val dateFormatter = NSDateFormatter()
    dateFormatter.dateFormat = "hh:mm a"
    return dateFormatter.stringFromDate(NSDate()).uppercase()
}

actual fun formatTimestamp(isoString: String): String {
    return isoString // For now, just return as is on iOS
}
