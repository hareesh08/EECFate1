package com.hd.eecfate

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowInsetsControllerCompat
import com.hd.eecfate.fatereq.SplashScreenWithMainContent
import com.hd.eecfate.ui.theme.EECFateTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge with transparent system bars
        enableEdgeToEdge()

        setContent {
            EECFateTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    SplashScreenWithMainContent()
                }
            }
        }
    }

    private fun enableEdgeToEdge() {
        // For Android 11 (API 30) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
            windowInsetsController.isAppearanceLightStatusBars = true // Light status bar icons
            windowInsetsController.isAppearanceLightNavigationBars =
                true // Light navigation bar icons
            window.statusBarColor = Color.Transparent.toArgb() // Transparent status bar
            window.navigationBarColor = Color.Transparent.toArgb() // Transparent navigation bar
        } else {
            // For versions below Android 11
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    )
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
        }
    }
}
