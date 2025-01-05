package com.hd.eecfate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hd.eecfate.fatereq.SplashScreenWithMainContent
import com.hd.eecfate.ui.theme.EECFateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EECFateTheme {
                SplashScreenWithMainContent()
            }
        }
    }
}
