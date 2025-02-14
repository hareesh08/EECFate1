package com.hd.eecfate.process

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import com.hd.eecfate.fatereq.AppHeader
import com.hd.eecfate.ui.theme.EECFateTheme

class FullCalculator : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EECFateTheme {
                HomeScreen()
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
fun HomeScreen() {
    val context = LocalContext.current
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Transparent),
                title = { AppHeader() },
                // Set statusBarPadding so the content doesn't overlap
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { paddingValues ->
        // The paddingValues will now ensure that the content respects the TopAppBar
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Apply padding here
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // GPA Calculator Button
                BigIconButton(
                    icon = Icons.Default.Calculate,
                    text = "GPA Calculator",
                    onClick = {
                        // Launch GPA Calculator Activity
                        context.startActivity(
                            Intent(
                                context,
                                GpaCalculator::class.java
                            )
                        )
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // CGPA Calculator Button
                BigIconButton(
                    icon = Icons.Default.Calculate,
                    text = "CGPA Calculator",
                    onClick = {
                        // Launch CGPA Calculator Activity
                        context.startActivity(
                            Intent(
                                context,
                                CgpaCalculator::class.java
                            )
                        )
                    }
                )
            }
        }
    }
}


@Composable
fun BigIconButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

// GPA Calculator Activity
class GC : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EECFateTheme {
                GpaCalculatorScreen()
            }
        }
    }
}

@Composable
fun GpaCalculatorScreen() {
    // Your existing GPA calculator UI
    Text(
        text = "GPA Calculator Screen",
        fontSize = 24.sp,
        color = Color.Black,
        modifier = Modifier.padding(16.dp)
    )
}

// CGPA Calculator Activity
class CGC : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EECFateTheme {
                CgpaCalculatorScreen()
            }
        }
    }
}

@Composable
fun CgpaCalculatorScreen() {
    // Your existing CGPA calculator UI
    Text(
        text = "CGPA Calculator Screen",
        fontSize = 24.sp,
        color = Color.Black,
        modifier = Modifier.padding(16.dp)
    )
}