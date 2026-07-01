package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = SuperdryOrange,
    secondary = ElegantWhite,
    tertiary = SuperdryDarkOrange,
    background = CharcoalDark,
    surface = CharcoalCard,
    onPrimary = ElegantWhite,
    onSecondary = CharcoalDark,
    onBackground = WarmOffWhite,
    onSurface = WarmOffWhite
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SuperdryOrange,
    secondary = CharcoalDark,
    tertiary = SuperdryDarkOrange,
    background = WarmOffWhite,
    surface = ElegantWhite,
    onPrimary = ElegantWhite,
    onSecondary = ElegantWhite,
    onBackground = CharcoalDark,
    onSurface = CharcoalDark
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme by default for elite artistic canvas look
  dynamicColor: Boolean = false, // Disable to preserve custom brand identity colors
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
