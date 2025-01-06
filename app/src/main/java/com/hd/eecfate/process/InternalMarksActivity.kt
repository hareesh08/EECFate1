package com.hd.eecfate.process

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import com.hd.eecfate.fatereq.AppHeader
import com.hd.eecfate.fatereq.WebViewScreen
import com.hd.eecfate.ui.theme.EECFateTheme

class InternalMarksActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display (for modern full-screen experiences)
        enableEdgeToEdge()

        // Set the content of the activity
        setContent {
            EECFateTheme {
                InternalMarksActivityScreen() // Calling the composable inside this activity
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternalMarksActivityScreen() {
    val context = LocalContext.current
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF6A11CB),
            Color(0xFF2575FC)
        )
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Transparent),
                title = { AppHeader() }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            WebViewScreen(
                url = "https://srmgroup.dhi-edu.com/srmgroup_srmeec/#/student/scores/overall",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
