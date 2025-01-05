package com.hd.eecfate

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hd.eecfate.ui.theme.EECFateTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

// Main Activity
class ThirdActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EECFateTheme {
                ThirdActivityScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdActivityScreen() {
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
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradientBackground)
                    .padding(bottom = 4.dp),
                color = Color.Transparent
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Circular App Icon
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_app_icon),
                                contentDescription = "App Icon",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // App Title with Modern Typography
                        Text(
                            text = "EEC Fate",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontFamily = FontFamily.SansSerif
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Developer Link with Subtle Hover Effect
                    TextButton(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.instagram.com/j_.a_.r_.v_.i_.s/")
                            )
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White.copy(alpha = 0.8f)
                        )
                    ) {
                        // Stylish text change to "Crafted By"
                        Text(
                            text = "Crafted By JLabs",
                            fontSize = 15.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
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
                                        dialogMessage = parseResponse(responseBody)
                                        showDialog = true
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
                            .height(400.dp) // Set a fixed height if needed
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
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                // Get the response body
                val responseBody = response.body?.string() ?: "No response body"

                // Parse the response and display it in the dialog
                callback(responseBody)
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

    // Handle top-level fields
    formattedResponse.append("Name: ${parsedResponse["studentName"]}\n")
    formattedResponse.append("Reg NO: ${parsedResponse["usn"]}\n")
    formattedResponse.append("GPA: ${parsedResponse["summary"]?.let { (it as Map<*, *>)["percentage"] }}\n")
    formattedResponse.append("Result: ${parsedResponse["summary"]?.let { (it as Map<*, *>)["resultFullForm"] }}\n")

    // Handle courses
    val ueScoreGraph = parsedResponse["ueScoreGraph"] as? List<Map<String, Any>>
    ueScoreGraph?.forEach { course ->
        formattedResponse.append("\nCourse Code: ${course["courseCode"]}\n")
        formattedResponse.append("Course Name: ${course["courseName"]}\n")
        formattedResponse.append("Internal: ${course["iaScore"]}/40\n")
        formattedResponse.append("External: ${course["ueScore"]}/60\n")
        formattedResponse.append("Marks: ${course["totalScore"]}\n")
        formattedResponse.append("Grade: ${course["result"]}\n")
    }

    return formattedResponse.toString()
}
