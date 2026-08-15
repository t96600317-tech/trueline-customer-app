package com.example.truelineapp

import java.text.SimpleDateFormat
import java.util.*

actual fun getCurrentTimeFormatted(): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date()).uppercase()
}

actual fun formatTimestamp(isoString: String): String {
    return try {
        // If it's already in hh:mm a format (from our fallback or optimistic update), return it
        if (isoString.contains("AM") || isoString.contains("PM")) return isoString
        
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(isoString)
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        outputFormat.format(date!!).uppercase()
    } catch (e: Exception) {
        isoString // Fallback to original string if parsing fails
    }
}
