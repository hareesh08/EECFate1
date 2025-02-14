package com.hd.eecfate.fatereq

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.hd.eecfate.downloads.DownloadListener

@Composable
fun WebViewScreen(url: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // WebView for loading the URL
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                // Add the JavaScript interface before loading the page
                addJavascriptInterface(DownloadListener.BlobHandler(context), "Android")

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url.toString()
                        // Open all links in the WebView itself
                        return false
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        // Ensure the page is fully loaded before interacting with JavaScript
                        Log.d("WebViewScreen", "Page finished loading: $url")
                    }
                }

                settings.javaScriptEnabled = true
                WebView.setWebContentsDebuggingEnabled(true)
                settings.domStorageEnabled = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.javaScriptCanOpenWindowsAutomatically = true

                // Set up the download listener
                DownloadListener.setDownloadListener(this, context)

                // Load the URL
                loadUrl(url)
            }
        }
    )
}