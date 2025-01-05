package com.hd.eecfate.process

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hd.eecfate.fatereq.AppHeader
import com.hd.eecfate.ui.theme.EECFateTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

// Main Activity
class SemesterMarkView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EECFateTheme {
                SemesterMarkViewScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemesterMarkViewScreen() {
    val context = LocalContext.current
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF6A11CB),
            Color(0xFF2575FC)
        )
    )

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("No response") }

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
            // WebView content
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        // Enable debugging to inspect outgoing requests
                        WebView.setWebContentsDebuggingEnabled(true)

                        // Enable JavaScript and DOM Storage
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true

                        // Set WebViewClient to capture outgoing requests
                        webViewClient = object : WebViewClient() {
                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                // Ensure that the request object is not null
                                val url = request?.url?.toString()

                                // Capture headers
                                val headers = request?.requestHeaders

                                // Log the URL and headers to console (Logcat)
                                if (!url.isNullOrEmpty()) {
                                    Log.d("WebViewRequest", "Captured Dynamic Request URL: $url")
                                }
                                if (!headers.isNullOrEmpty()) {
                                    Log.d("WebViewRequest", "Captured Request Headers: $headers")
                                }

                                // Check if it's a dynamic request (example: containing "dhiapiserver")
                                if (!url.isNullOrEmpty() && url.contains("dhiapiserver/api/university-exam/score")) {
                                    Log.d(
                                        "WebViewRequest",
                                        "Captured Dynamic URL with Headers: $url\nHeaders: $headers"
                                    )
                                    // Make HTTP request with captured URL and headers
                                    makeHttpRequest(url, headers, context) { responseBody ->
                                        // Ensure that UI updates are performed on the main thread
                                        Handler(Looper.getMainLooper()).post {
                                            dialogMessage = parseResponse(responseBody)
                                            showDialog = true
                                        }
                                    }
                                }

                                // Allow the request to load normally
                                return super.shouldInterceptRequest(view, request)
                            }
                        }

                        // Load the target URL
                        loadUrl("https://srmgroup.dhi-edu.com/srmgroup_srmeec/#/student/scores/universityexam")
                    }
                }
            )
        }

        // Show dialog if needed
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("University Marks") },
                text = {
                    // Scrollable content inside the dialog
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp) // Adjust height as needed
                            .verticalScroll(rememberScrollState()) // Ensure it scrolls
                    ) {
                        Text(
                            text = dialogMessage,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

// Function to make an HTTP request and return the response via callback
private fun makeHttpRequest(
    url: String,
    headers: Map<String, String>?,
    context: Context,
    callback: (String) -> Unit
) {
    // Create OkHttpClient
    val client = OkHttpClient()

    // Build the request
    val requestBuilder = Request.Builder().url(url)

    // Add headers if available
    headers?.forEach { (key, value) ->
        requestBuilder.addHeader(key, value)
    }

    val request = requestBuilder.build()

    // Make asynchronous request
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            // Handle failure (e.g., network error)
            Log.e("HTTP Request", "Failed to fetch data: ${e.message}")
            e.printStackTrace()
            // Optionally, update the UI to show an error message
            Handler(Looper.getMainLooper()).post {
                callback("Error fetching data: ${e.message}")
            }
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                // Get the response body
                val responseBody = response.body?.string() ?: "No response body"

                // Log the response for debugging
                Log.d("HTTP Response", "Response Body: $responseBody")

                // Parse the response and display it in the dialog
                callback(responseBody)
            } else {
                Log.e("HTTP Response", "Unsuccessful response: ${response.code}")
                Handler(Looper.getMainLooper()).post {
                    callback("Error: Unsuccessful response with code ${response.code}")
                }
            }
        }
    })
}

// A generic function to parse any JSON response dynamically
private fun parseResponse(responseBody: String): String {
    // Use Gson or any other JSON parsing library to parse the response
    return try {
        val gson = Gson()
        val type = object : TypeToken<Map<String, Any>>() {}.type
        val parsedResponse: Map<String, Any> = gson.fromJson(responseBody, type)

        // Format the parsed response into a readable string
        formatResponse(parsedResponse)
    } catch (e: Exception) {
        "Error parsing response: ${e.message}"
    }
}

// Function to format the parsed JSON into a human-readable string
private fun formatResponse(parsedResponse: Map<String, Any>): String {
    val formattedResponse = StringBuilder()

    // Extract top-level fields with safe casting and default values
    val studentName = parsedResponse["studentName"] as? String ?: "N/A"
    val usn = parsedResponse["usn"] as? String ?: "N/A"
    val summary = parsedResponse["summary"] as? Map<*, *> ?: emptyMap<Any, Any>()
    val percentage = summary["percentage"] as? String ?: "N/A"
    val resultFullForm = summary["resultFullForm"] as? String ?: "N/A"

    // Append top-level information
    formattedResponse.append("Name: $studentName\n")
    formattedResponse.append("Reg NO: $usn\n")
    formattedResponse.append("GPA: $percentage\n")
    formattedResponse.append("Result: $resultFullForm\n")

    // Handle courses
    val ueScoreGraph = parsedResponse["ueScoreGraph"] as? List<Map<String, Any>>
    ueScoreGraph?.forEach { course ->
        // Safely extract course details with default values
        val courseCodeRaw = course["courseCode"] as? String ?: ""
        val courseCode = courseCodeRaw.trim()

        val courseName = course["courseName"] as? String ?: "N/A"
        val iaScore = course["iaScore"] as? String ?: "N/A"
        val maxIaScore = course["maxIaScore"]?.toString()?.toIntOrNull() ?: 40
        val ueScore = course["ueScore"] as? String ?: "N/A"
        val maxUeScore = course["maxUeScore"]?.toString()?.toIntOrNull() ?: 60
        val totalScore = course["totalScore"] as? String ?: "N/A"
        val result = course["result"] as? String ?: "N/A"

        // Determine if the course is a lab course
        val isLabCourse = courseCode.endsWith("L", ignoreCase = true)

        // Append course information
        formattedResponse.append("\nCourse Code: $courseCode\n")
        formattedResponse.append("Course Name: $courseName\n")

        if (isLabCourse) {
            // For lab courses, display scores with their respective maximums
            formattedResponse.append("Internal: $iaScore/$maxUeScore\n")
            formattedResponse.append("External: $ueScore/$maxIaScore\n")
        } else {
            // For regular courses, display scores with their respective maximums
            formattedResponse.append("Internal: $iaScore/$maxIaScore\n")
            formattedResponse.append("External: $ueScore/$maxUeScore\n")
        }

        formattedResponse.append("Marks: $totalScore\n")
        formattedResponse.append("Grade: $result\n")
    }

    return formattedResponse.toString()
}
