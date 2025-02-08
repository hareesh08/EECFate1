package com.hd.eecfate.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.CookieManager
import androidx.activity.ComponentActivity
import com.hd.eecfate.MainActivity


class FixApp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clearAppDataAndRestart()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    // Function to clear data and restart app
    private fun clearAppDataAndRestart() {
        try {
            // Clear app data (cache, files, shared preferences)
            clearAppCacheAndFiles()
            restartApp()
        } catch (_: Exception) {
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
        val cookieManager = CookieManager.getInstance()
        if (cookieManager.acceptCookie()) {
            cookieManager.removeAllCookies {
            }
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
