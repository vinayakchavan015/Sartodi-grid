package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = GridBluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = GridBlueDark,
    secondary = GridCyanAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = Color(0xFF0E7490),
    tertiary = EnergizedGreen,
    onTertiary = Color.White,
    tertiaryContainer = EnergizedGreenSoft,
    onTertiaryContainer = Color(0xFF065F46),
    error = HighVoltageRed,
    onError = Color.White,
    errorContainer = HighVoltageRedSoft,
    onErrorContainer = Color(0xFF991B1B),
    background = SlateBgLight,
    onBackground = SlateTextMainLight,
    surface = SlateSurfaceLight,
    onSurface = SlateTextMainLight,
    surfaceVariant = SlateSurfaceVariantLight,
    onSurfaceVariant = SlateTextMutedLight,
    outline = SlateOutlineLight,
    outlineVariant = Color(0xFFE2E8F0)
)

private val DarkColors = darkColorScheme(
    primary = GridCyanLight,
    onPrimary = Color(0xFF090E1A),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFF38BDF8),
    onSecondary = Color(0xFF090E1A),
    secondaryContainer = Color(0xFF164E63),
    onSecondaryContainer = Color(0xFFCFFAFE),
    tertiary = EnergizedGreenDark,
    onTertiary = Color(0xFF064E3B),
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFFA7F3D0),
    error = HighVoltageRedDark,
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
    background = SlateBgDark,
    onBackground = SlateTextMainDark,
    surface = SlateSurfaceDark,
    onSurface = SlateTextMainDark,
    surfaceVariant = SlateSurfaceVariantDark,
    onSurfaceVariant = SlateTextMutedDark,
    outline = SlateOutlineDark,
    outlineVariant = Color(0xFF273549)
)

val SubstationShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun SartodiSubstationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = SubstationShapes,
        content = content
    )
}
