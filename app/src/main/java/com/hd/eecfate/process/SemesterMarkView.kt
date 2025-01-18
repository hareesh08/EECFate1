package com.hd.eecfate.process

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsControllerCompat
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

    private fun enableEdgeToEdge() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
            windowInsetsController.isAppearanceLightStatusBars = true
            windowInsetsController.isAppearanceLightNavigationBars = true
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
        } else {
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
                        WebView.setWebContentsDebuggingEnabled(true)

                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true

                        webViewClient = object : WebViewClient() {
                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                val url = request?.url?.toString()
                                val headers = request?.requestHeaders

                                if (!url.isNullOrEmpty() && url.contains("dhiapiserver/api/university-exam/score")) {
                                    makeHttpRequest(url, headers, context) { responseBody ->
                                        Handler(Looper.getMainLooper()).post {
                                            dialogMessage = parseResponse(responseBody)
                                            showDialog = true
                                        }
                                    }
                                }

                                return super.shouldInterceptRequest(view, request)
                            }
                        }
                        Toast.makeText(context, "Downloads Not Available", Toast.LENGTH_SHORT)
                            .show()
                        Toast.makeText(context, "Try In HomePage", Toast.LENGTH_SHORT).show()
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState())
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
    val client = OkHttpClient()
    val requestBuilder = Request.Builder().url(url)

    headers?.forEach { (key, value) ->
        requestBuilder.addHeader(key, value)
    }

    val request = requestBuilder.build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Handler(Looper.getMainLooper()).post {
                callback("Error fetching data: ${e.message}")
            }
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string() ?: "No response body"
            callback(responseBody)
        }
    })
}

// A generic function to parse any JSON response dynamically
private fun parseResponse(responseBody: String): String {
    return try {
        val gson = Gson()
        val type = object : TypeToken<Map<String, Any>>() {}.type
        val parsedResponse: Map<String, Any> = gson.fromJson(responseBody, type)
        Log.d("Parsed Response", parsedResponse.toString())

        formatResponse(parsedResponse)
    } catch (e: Exception) {
        "Error parsing response: ${e.message}"
    }
}

// Function to format the parsed JSON into a human-readable string
private fun formatResponse(parsedResponse: Map<String, Any>): String {
    val formattedResponse = StringBuilder()

    val studentName = parsedResponse["studentName"] as? String ?: "N/A"
    val usn = parsedResponse["usn"] as? String ?: "N/A"
    val summary = parsedResponse["summary"] as? Map<*, *> ?: emptyMap<Any, Any>()
    val percentage = summary["percentage"] as? String ?: "N/A"
    val resultFullForm = summary["resultFullForm"] as? String ?: "N/A"

    formattedResponse.append("Name: $studentName\n")
    formattedResponse.append("Reg NO: $usn\n")
    formattedResponse.append("GPA: $percentage\n")
    formattedResponse.append("Result: $resultFullForm\n")

    val ueScoreGraph = parsedResponse["ueScoreGraph"] as? List<Map<String, Any>>
    ueScoreGraph?.forEach { course ->
        val courseCodeRaw = course["courseCode"] as? String ?: ""
        val courseCode = courseCodeRaw.trim()

        val courseName = course["courseName"] as? String ?: "N/A"
        val iaScore = course["iaScore"] as? String ?: "N/A"
        val maxIaScore = course["maxIaScore"]?.toString()?.toIntOrNull() ?: 40
        val ueScore = course["ueScore"] as? String ?: "N/A"
        val maxUeScore = course["maxUeScore"]?.toString()?.toIntOrNull() ?: 60
        val totalScore = course["totalScore"] as? String ?: "N/A"
        val result = course["result"] as? String ?: "N/A"

        val isLabCourse = courseCode.endsWith("L", ignoreCase = true)

        formattedResponse.append("\nCourse Code: $courseCode\n")
        formattedResponse.append("Course Name: $courseName\n")

        if (isLabCourse) {
            formattedResponse.append("Internal: $iaScore/$maxUeScore\n")
            formattedResponse.append("External: $ueScore/$maxIaScore\n")
        } else {
            formattedResponse.append("Internal: $iaScore/$maxIaScore\n")
            formattedResponse.append("External: $ueScore/$maxUeScore\n")
        }

        formattedResponse.append("Marks: $totalScore\n")
        formattedResponse.append("Grade: $result\n")
    }

    return formattedResponse.toString()
}

