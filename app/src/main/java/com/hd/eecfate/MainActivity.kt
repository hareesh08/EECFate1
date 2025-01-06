package com.hd.eecfate

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hd.eecfate.fatereq.SplashScreenWithMainContent
import com.hd.eecfate.ui.theme.EECFateTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EECFateTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    SplashScreenWithMainContent()
                }
            }
        }
    }
}
