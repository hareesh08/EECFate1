package com.hd.eecfate.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Data class representing responsive dimensions based on device screen size.
 * Values scale according to WindowSizeClass (Compact, Medium, Expanded).
 */
data class Dimensions(
    val paddingSmall: Dp,
    val paddingMedium: Dp,
    val paddingLarge: Dp,
    val paddingExtraLarge: Dp,
    val spacingSmall: Dp,
    val spacingMedium: Dp,
    val spacingLarge: Dp,
    val iconSizeSmall: Dp,
    val iconSizeMedium: Dp,
    val iconSizeLarge: Dp,
    val minTouchTarget: Dp,
    val cardElevation: Dp,
    val cornerRadius: Dp
)

/**
 * Compact dimensions for phones (width < 600dp)
 */
private val CompactDimensions = Dimensions(
    paddingSmall = 4.dp,
    paddingMedium = 8.dp,
    paddingLarge = 16.dp,
    paddingExtraLarge = 24.dp,
    spacingSmall = 4.dp,
    spacingMedium = 8.dp,
    spacingLarge = 16.dp,
    iconSizeSmall = 20.dp,
    iconSizeMedium = 24.dp,
    iconSizeLarge = 32.dp,
    minTouchTarget = 48.dp,
    cardElevation = 2.dp,
    cornerRadius = 12.dp
)

/**
 * Medium dimensions for tablets (600dp ≤ width < 840dp)
 */
private val MediumDimensions = Dimensions(
    paddingSmall = 6.dp,
    paddingMedium = 12.dp,
    paddingLarge = 20.dp,
    paddingExtraLarge = 32.dp,
    spacingSmall = 6.dp,
    spacingMedium = 12.dp,
    spacingLarge = 24.dp,
    iconSizeSmall = 22.dp,
    iconSizeMedium = 28.dp,
    iconSizeLarge = 40.dp,
    minTouchTarget = 48.dp,
    cardElevation = 3.dp,
    cornerRadius = 14.dp
)

/**
 * Expanded dimensions for large tablets (width ≥ 840dp)
 */
private val ExpandedDimensions = Dimensions(
    paddingSmall = 8.dp,
    paddingMedium = 16.dp,
    paddingLarge = 24.dp,
    paddingExtraLarge = 40.dp,
    spacingSmall = 8.dp,
    spacingMedium = 16.dp,
    spacingLarge = 32.dp,
    iconSizeSmall = 24.dp,
    iconSizeMedium = 32.dp,
    iconSizeLarge = 48.dp,
    minTouchTarget = 48.dp,
    cardElevation = 4.dp,
    cornerRadius = 16.dp
)

/**
 * Enum representing window size classes based on screen width.
 */
enum class WindowSizeClass {
    Compact,    // width < 600dp
    Medium,     // 600dp ≤ width < 840dp
    Expanded    // width ≥ 840dp
}

/**
 * Determines the appropriate WindowSizeClass based on screen width in dp.
 * 
 * @param widthDp Screen width in density-independent pixels
 * @return WindowSizeClass (Compact, Medium, or Expanded)
 */
fun getWindowSizeClass(widthDp: Int): WindowSizeClass {
    return when {
        widthDp < 600 -> WindowSizeClass.Compact
        widthDp < 840 -> WindowSizeClass.Medium
        else -> WindowSizeClass.Expanded
    }
}

/**
 * Returns appropriate Dimensions based on WindowSizeClass.
 */
fun getDimensionsForSizeClass(sizeClass: WindowSizeClass): Dimensions {
    return when (sizeClass) {
        WindowSizeClass.Compact -> CompactDimensions
        WindowSizeClass.Medium -> MediumDimensions
        WindowSizeClass.Expanded -> ExpandedDimensions
    }
}

/**
 * Composable function that calculates and returns appropriate dimensions
 * based on the current device screen size.
 */
@Composable
fun rememberDimensions(): Dimensions {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val sizeClass = getWindowSizeClass(screenWidthDp)
    return getDimensionsForSizeClass(sizeClass)
}

/**
 * CompositionLocal for accessing dimensions throughout the app.
 * Provides device-aware dimensions based on WindowSizeClass.
 */
val LocalDimensions = compositionLocalOf { CompactDimensions }

/**
 * Extension property for easy access to current dimensions.
 * Usage: LocalDimensions.current
 */
object LocalDimensionsProvider {
    val current: Dimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalDimensions.current
}
