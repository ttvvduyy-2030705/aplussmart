package com.aplus.locknative.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AplusBlack = Color(0xFF070707)
val AplusGraphite = Color(0xFF111216)
val AplusCard = Color(0xFF17191F)
val AplusCardSoft = Color(0xFF20232B)
val AplusRed = Color(0xFFE11D2E)
val AplusRedDark = Color(0xFF8F0F1B)
val AplusText = Color(0xFFF7F7F7)
val AplusMuted = Color(0xFFB7B7BE)
val AplusLine = Color(0xFF2B2E38)
val AplusGreen = Color(0xFF42D392)
val AplusAmber = Color(0xFFFFC857)

private val AplusColors = darkColorScheme(
    primary = AplusRed,
    secondary = AplusRedDark,
    background = AplusBlack,
    surface = AplusCard,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = AplusText,
    onSurface = AplusText
)

@Composable
fun AplusLockTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AplusColors, typography = MaterialTheme.typography, content = content)
}
