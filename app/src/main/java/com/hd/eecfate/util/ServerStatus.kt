package com.hd.eecfate.util

import androidx.compose.ui.graphics.Color
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

const val GOOD = "Good"
const val MEDIUM = "Medium"
const val WEAK = "Weak"

// Define colors for Android UI (Jetpack Compose)
val GREEN = Color(0xFF4CAF50)  // Green for Good
val YELLOW = Color(0xFFFFEB3B) // Yellow for Medium
val RED = Color(0xFFF44336)    // Red for Weak
val GRAY = Color(0xFF9E9E9E)   // Gray for "Loading..." status

fun checkServerLoad(url: String): Pair<String, Color> {
    var status = WEAK  // Default status
    var responseTime = 0L

    try {
        val startTime = System.currentTimeMillis()
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000  // Timeout after 5 seconds
        connection.readTimeout = 5000     // Timeout after 5 seconds
        connection.connect()

        responseTime = System.currentTimeMillis() - startTime
        status = determineStatus(responseTime)
        connection.disconnect()
    } catch (e: IOException) {
        // Handle network errors if connection fails
        status = WEAK
    }

    // Return the status message along with the corresponding color
    return when (status) {
        GOOD -> "Status:$GOOD" to GREEN  // Green for Good
        MEDIUM -> "Status:$MEDIUM" to YELLOW  // Yellow for Medium
        else -> "Status:$WEAK" to RED  // Red for Weak
    }
}

fun determineStatus(responseTime: Long): String {
    return when {
        responseTime < 500 -> GOOD    // Fast response
        responseTime < 1000 -> MEDIUM // Average response
        else -> WEAK                 // Slow response
    }
}
