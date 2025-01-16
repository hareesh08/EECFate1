package com.hd.eecfate.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hd.eecfate.MainActivity

class FixApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppScreen(onClearDataAndRestart = { clearAppDataAndRestart() })
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    // Function to clear data and restart app
    fun clearAppDataAndRestart() {
        try {
            // Clear app data (cache, files, shared preferences)
            clearAppCacheAndFiles()
            // Show Toast to notify the user
            Toast.makeText(this, "App data cleared", Toast.LENGTH_SHORT).show()
            // Restart the app
            restartApp()
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Error clearing app data. Try manually in settings: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Function to clear app cache and files
    private fun clearAppCacheAndFiles() {
        // Clear cache directory
        try {
            applicationContext.cacheDir.deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Clear internal files directory
        try {
            applicationContext.filesDir.deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Clear shared preferences
        try {
            val sharedPrefs = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            sharedPrefs.edit().clear().apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Function to restart the app
    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finishAffinity() // Close the current activity and all related activities
    }
}

@Composable
fun AppScreen(onClearDataAndRestart: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val padding = if (screenWidthDp > 600) 32.dp else 16.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .safeDrawingPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "App Won't Work Properly",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Just Click Here",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onClearDataAndRestart,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Clear Data & Restart App")
        }
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Try one or two times if not working, try manually in settings",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppScreen() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AppScreen(onClearDataAndRestart = {})
        }
    }
}