package com.hd.eecfate.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

// Primary color palette
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Semantic color tokens for light theme
val SuccessLight = Color(0xFF4CAF50)
val WarningLight = Color(0xFFFF9800)
val InfoLight = Color(0xFF2196F3)

// Semantic color tokens for dark theme
val SuccessDark = Color(0xFF81C784)
val WarningDark = Color(0xFFFFB74D)
val InfoDark = Color(0xFF64B5F6)

/**
 * Extension properties for semantic colors on ColorScheme.
 * These provide consistent success, warning, and info colors throughout the app.
 */
val ColorScheme.success: Color
    get() = if (this.primary == Purple40) SuccessLight else SuccessDark

val ColorScheme.warning: Color
    get() = if (this.primary == Purple40) WarningLight else WarningDark

val ColorScheme.info: Color
    get() = if (this.primary == Purple40) InfoLight else InfoDark


