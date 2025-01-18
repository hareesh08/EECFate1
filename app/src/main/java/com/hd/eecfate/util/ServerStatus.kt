package com.hd.eecfate.util

import androidx.compose.ui.graphics.Color
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


// This is your checkServerLoad function
fun checkServerLoad(url: String): Pair<String, Color> {
    var status = "Weak"  // Default status
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
        status = "Weak"
    }

    return when (status) {
        "Good" -> "Status: Good" to Color(0xFF4CAF50)  // Green for Good
        "Medium" -> "Status: Medium" to Color(0xFFFFEB3B)  // Yellow for Medium
        else -> "Status: Weak" to Color(0xFFF44336)  // Red for Weak
    }
}

fun determineStatus(responseTime: Long): String {
    return when {
        responseTime < 500 -> "Good"    // Fast response
        responseTime < 1000 -> "Medium" // Average response
        else -> "Weak"                 // Slow response
    }
}
