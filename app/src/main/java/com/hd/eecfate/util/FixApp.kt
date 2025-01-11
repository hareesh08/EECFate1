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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hd.eecfate.MainActivity

class FixApp : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppScreen(onClearDataAndRestart = { clearAppDataAndRestart() })
        }
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
            Toast.makeText(this, "Error clearing app data: ${e.message}", Toast.LENGTH_SHORT).show()
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

        // Clear database (replace "your_database_name" with the actual database name)
        try {
            val dbPath = getDatabasePath("your_database_name")
            if (dbPath.exists()) {
                dbPath.delete()
            }
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center, // Centers content vertically
        horizontalAlignment = Alignment.CenterHorizontally // Centers content horizontally
    ) {
        // Your content goes here, for example:
        Text(text = "Welcome to the App!")

        Spacer(modifier = Modifier.height(16.dp))

        // Button to clear app data and restart
        Button(onClick = onClearDataAndRestart) {
            Text(text = "Clear Data & Restart App")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppScreen() {
    AppScreen(onClearDataAndRestart = {})
}