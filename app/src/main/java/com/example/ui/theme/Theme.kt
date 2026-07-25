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

private val DarkColorScheme =
  darkColorScheme(
      primary = EmeraldLight,
      onPrimary = Color(0xFF141E0D),
      primaryContainer = Color(0xFF3A4D01),
      onPrimaryContainer = EmeraldLight,
      secondary = CoralBurned,
      onSecondary = Color.White,
      tertiary = MacroProteinColor,
      background = DarkBackground,
      surface = DarkSurface,
      surfaceVariant = DarkCard,
      onBackground = Color(0xFFE6E3D8),
      onSurface = Color(0xFFE6E3D8),
      onSurfaceVariant = Color(0xFFB3B1A2),
      outline = Color(0xFF484A3F)
  )

private val LightColorScheme =
  lightColorScheme(
      primary = EmeraldPrimary,
      onPrimary = Color.White,
      primaryContainer = EmeraldLight,
      onPrimaryContainer = Color(0xFF141E0D),
      secondary = CoralBurned,
      onSecondary = Color.White,
      secondaryContainer = CoralBurnedLight,
      onSecondaryContainer = Color(0xFF411C00),
      tertiary = MacroProteinColor,
      background = LightBackground,
      surface = LightSurface,
      surfaceVariant = LightCard,
      onBackground = Color(0xFF1B1C17),
      onSurface = Color(0xFF1B1C17),
      onSurfaceVariant = Color(0xFF797966),
      outline = Color(0xFFEBEBE0)
  )

@Composable
fun NutriTrackTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
