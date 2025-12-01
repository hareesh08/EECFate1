package com.hd.eecfate.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material Design 3 shape definitions for the EECFate application.
 * Provides consistent corner radius values across different component sizes.
 */
val Shapes = Shapes(
    // Extra small components (e.g., chips, small buttons)
    extraSmall = RoundedCornerShape(4.dp),
    
    // Small components (e.g., buttons, text fields)
    small = RoundedCornerShape(8.dp),
    
    // Medium components (e.g., cards, dialogs)
    medium = RoundedCornerShape(12.dp),
    
    // Large components (e.g., bottom sheets, large cards)
    large = RoundedCornerShape(16.dp),
    
    // Extra large components (e.g., full-screen dialogs)
    extraLarge = RoundedCornerShape(28.dp)
)
