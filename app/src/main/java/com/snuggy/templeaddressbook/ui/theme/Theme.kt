package com.snuggy.templeaddressbook.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = OrangePrimary,
    secondary = OrangeSecondary,
    background = AppBg,
    surface = CardWhite,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onBackground = androidx.compose.ui.graphics.Color(0xFF1C1B1A),
    onSurface = androidx.compose.ui.graphics.Color(0xFF1C1B1A),
    outline = CardBorder,
    onSurfaceVariant = MutedText
)

@Composable
fun TempleAddressBookTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}
