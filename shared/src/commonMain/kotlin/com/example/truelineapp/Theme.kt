package com.example.truelineapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Official TrueLine Brand Color Palette (HEX Codes)
 */
val TrueLinePrimary = Color(0xFF2D6A6B)   // Teal: App Bars, Primary Buttons, Headers, Listener Avatars
val TrueLineSecondary = Color(0xFF5FA8D3) // Sky: Sub-texts, secondary icons, dark-mode accents
val TrueLineAccent = Color(0xFFF2A65A)    // Amber: Call-to-action triggers ONLY (Connect, Rate, Gift)
val TrueLineDarkBg = Color(0xFF0F1B22)    // Dark Background: In-Call screens & Dark Theme
val TrueLineLightBg = Color(0xFFF4F8F9)   // Light Background: Main app screens
val TrueLineOnline = Color(0xFF3FBFAD)    // Online Green: Indicators, verified badges, success
val TrueLineTextSecondary = Color(0xFF64748B) // Muted Secondary Text

// UI Component Tokens (matching trueline_listener design system)
val PrimaryDim = Color(0xFF245658)
val AccentDisabled = Color(0xFFFADBB3)
val DarkSurface = Color(0xFF16232C)
val OnlineSuccess = Color(0xFF3FBFAD)
val Danger = Color(0xFFE65D5D)
val SurfaceWhite = Color(0xFFFFFFFF)
val SurfaceElevated = Color(0xFFEDF4F6)
val BorderSubtle = Color(0xFFE2EAEB)
val TextPrimary = Color(0xFF0F1B22)
val TextSecondary = Color(0xFF4E6167)
val TextMuted = Color(0xFF8A9BA0)
val TextMutedGrey = Color(0xFF5A6E72)
val TextResendMuted = Color(0xFF8A9BA0)

// Legacy Aliases for safety (internal use)
val Primary = TrueLinePrimary
val Secondary = TrueLineSecondary
val Accent = TrueLineAccent
val Dark = TrueLineDarkBg
val Light = TrueLineLightBg
val Online = TrueLineOnline

private val DarkColorScheme = darkColorScheme(
    primary = TrueLinePrimary,
    secondary = TrueLineSecondary,
    tertiary = TrueLineAccent,
    background = TrueLineDarkBg,
    surface = Color(0xFF16232C),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TrueLineLightBg,
    onSurface = TrueLineLightBg,
)

private val LightColorScheme = lightColorScheme(
    primary = TrueLinePrimary,
    secondary = TrueLineSecondary,
    tertiary = TrueLineAccent,
    background = TrueLineLightBg,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TrueLineDarkBg,
    onSurface = TrueLineDarkBg,
)

@Composable
fun TrueLineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
