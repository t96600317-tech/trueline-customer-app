package com.example.truelineapp.call

/**
 * Keeps the server/native Zego diagnostic intact for the development call
 * error UI. In particular, Zego's numeric reason code must not be truncated.
 */
fun callConnectionDiagnosticForDisplay(message: String): String = message
