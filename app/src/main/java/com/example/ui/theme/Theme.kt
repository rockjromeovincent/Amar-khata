package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.models.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimaryDark,
    onPrimary = Color(0xFF00391E),
    primaryContainer = EmeraldContainerDark,
    onPrimaryContainer = Color(0xFF86F8B6),
    secondary = MintSecondaryDark,
    onSecondary = Color(0xFF003822),
    secondaryContainer = MintContainerDark,
    onSecondaryContainer = Color(0xFF90F9CB),
    tertiary = GoldTertiaryDark,
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = GoldContainerDark,
    onTertiaryContainer = Color(0xFFFFDE9F),
    background = BackgroundDark,
    onBackground = Color(0xFFE2E9E3),
    surface = SurfaceDark,
    onSurface = Color(0xFFE2E9E3),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC0CEC5),
    outline = OutlineDark,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainerLight,
    onPrimaryContainer = Color(0xFF00210E),
    secondary = MintSecondary,
    onSecondary = Color.White,
    secondaryContainer = MintContainerLight,
    onSecondaryContainer = Color(0xFF002113),
    tertiary = GoldTertiary,
    onTertiary = Color.White,
    tertiaryContainer = GoldContainerLight,
    onTertiaryContainer = Color(0xFF2B1700),
    background = BackgroundLight,
    onBackground = Color(0xFF191D1A),
    surface = SurfaceLight,
    onSurface = Color(0xFF191D1A),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF404943),
    outline = OutlineLight,
    error = CashOutRed,
    onError = Color.White
)

@Composable
fun AmarKhataTheme(
    appThemeMode: AppThemeMode = AppThemeMode.SYSTEM,
    dynamicColor: Boolean = false, // Preserve authentic fintech branding by default
    content: @Composable () -> Unit
) {
    val isDark = when (appThemeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

