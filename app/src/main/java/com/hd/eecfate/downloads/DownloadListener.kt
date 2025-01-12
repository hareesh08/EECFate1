package com.hd.eecfate.downloads

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException

object DownloadListener {

    /**
     * Sets up a download listener for the given WebView.
     *
     * @param webView The WebView to set the download listener for.
     * @param context The context to access the DownloadManager.
     */
    fun setDownloadListener(webView: WebView, context: Context) {
        webView.setDownloadListener { url, _, _, mimeType, _ ->
            if (url.startsWith("blob:")) {
                handleBlobUrl(webView, url, context)
            } else {
                handleHttpUrl(url, mimeType, context)
            }
        }
    }

    /**
     * Handles downloading files from HTTP/HTTPS URLs.
     *
     * @param url The URL of the file to download.
     * @param mimeType The MIME type of the file.
     * @param context The context to access the DownloadManager.
     */
    private fun handleHttpUrl(
        url: String,
        mimeType: String,
        context: Context
    ) {
        showCustomNameDialog(context) { customFileName ->
            if (!TextUtils.isEmpty(customFileName)) {
                saveFileWithCustomName(context, url, customFileName, mimeType)
            }
        }
    }

    /**
     * Shows a dialog to ask the user for a custom file name.
     *
     * @param context The context for the dialog.
     * @param callback The callback to pass the custom file name.
     */
    private fun showCustomNameDialog(context: Context, callback: (String) -> Unit) {
        val editText = EditText(context)
        val dialog = AlertDialog.Builder(context)
            .setTitle("Enter File Name  ")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val customFileName = editText.text.toString().trim()
                callback(customFileName)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    /**
     * Saves the file with the custom file name to the Downloads folder.
     *
     * @param context The context to access file system.
     * @param url The URL of the file to download.
     * @param customFileName The custom file name.
     * @param mimeType The MIME type of the file.
     */
    private fun saveFileWithCustomName(
        context: Context,
        url: String,
        customFileName: String,
        mimeType: String
    ) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
            return
        }

        val request = DownloadManager.Request(Uri.parse(url)).apply {
            val cookie = CookieManager.getInstance().getCookie(url)
            addRequestHeader("Cookie", cookie)
            addRequestHeader("User-Agent", "Android")
            setTitle("Downloading $customFileName")
            setDescription("File is being downloaded")
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "EECFate/$customFileName"
            )
            allowScanningByMediaScanner()
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context, intent: android.content.Intent) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent.action) {
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (id == downloadId) {
                        Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
                        context.unregisterReceiver(this)
                    }
                }
            }
        }

        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    /**
     * Handles downloading files from blob URLs.
     *
     * @param webView The WebView instance.
     * @param blobUrl The blob URL to download.
     * @param context The context to access the file system.
     */
    private fun handleBlobUrl(webView: WebView, blobUrl: String, context: Context) {
        webView.addJavascriptInterface(BlobHandler(context), "Android")

        webView.evaluateJavascript(
            """
            (async () => {
                const response = await fetch('$blobUrl');
                const blob = await response.blob();
                const reader = new FileReader();
                reader.onload = function() {
                    if (typeof Android !== 'undefined') {
                        Android.saveBlob(reader.result, blob.type);
                    }
                };
                reader.readAsDataURL(blob);
            })();
            """.trimIndent(),
            null
        )
    }

    /**
     * JavaScript interface to handle communication between JavaScript and the app.
     */
    class BlobHandler(private val context: Context) {

        @RequiresApi(Build.VERSION_CODES.Q)
        @SuppressLint("NewApi")
        @JavascriptInterface
        fun saveBlob(dataUrl: String, mimeType: String) {
            try {
                val base64Data = dataUrl.split(",")[1]
                val fileData = Base64.decode(base64Data, Base64.DEFAULT)

                showCustomNameDialog(context) { customFileName ->
                    if (!TextUtils.isEmpty(customFileName)) {
                        saveFileWithCustomName(context, customFileName, mimeType, fileData)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Saves the file with the custom file name to the Downloads folder.
         *
         * @param context The context to access file system.
         * @param customFileName The custom file name.
         * @param mimeType The MIME type of the file.
         * @param fileData The file data.
         */
        @RequiresApi(Build.VERSION_CODES.Q)
        private fun saveFileWithCustomName(
            context: Context,
            customFileName: String,
            mimeType: String,
            fileData: ByteArray
        ) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, customFileName)
                put(MediaStore.Downloads.MIME_TYPE, mimeType)
                put(
                    MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + "/EECFate"
                )
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                try {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(fileData)
                    }
                    Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
