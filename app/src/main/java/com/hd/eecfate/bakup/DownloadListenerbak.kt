package com.hd.eecfate.bakup

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.EditText
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
        webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            if (url.startsWith("blob:")) {
                // Handle blob URLs manually
                handleBlobUrl(webView, url, context)
            } else {
                // Handle HTTP/HTTPS URLs using DownloadManager
                handleHttpUrl(url, userAgent, contentDisposition, mimeType, context)
            }
        }
    }

    /**
     * Handles downloading files from HTTP/HTTPS URLs.
     *
     * @param url The URL of the file to download.
     * @param userAgent The user agent string.
     * @param contentDisposition The content disposition header.
     * @param mimeType The MIME type of the file.
     * @param context The context to access the DownloadManager.
     */
    private fun handleHttpUrl(
        url: String,
        userAgent: String,
        contentDisposition: String,
        mimeType: String,
        context: Context
    ) {
        // Ask the user for a custom file name
        showCustomNameDialog(context) { customFileName ->
            if (!TextUtils.isEmpty(customFileName)) {
                // Save the file with the custom name
                saveFileWithCustomName(context, url, customFileName, mimeType)
            }
        }
    }

    /**
     * Ask the user for a custom file name.
     *
     * @param context The context for the dialog.
     * @param callback The callback to pass the custom file name.
     */
    private fun showCustomNameDialog(context: Context, callback: (String) -> Unit) {
        // Create a dialog to ask the user for a file name
        val editText = EditText(context)
        val dialog = AlertDialog.Builder(context)
            .setTitle("Enter File Name")
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
        // Check if permission to write to external storage is granted
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

        // Perform the file download using the custom name
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            // Set headers
            val cookie = CookieManager.getInstance().getCookie(url)
            addRequestHeader("Cookie", cookie)
            addRequestHeader("User-Agent", "Android")

            // Set title and description
            setTitle("Downloading $customFileName")
            setDescription("File is being downloaded")

            // Set destination directory (EECFate folder inside Downloads)
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "EECFate/$customFileName"
            )

            // Allow scanning by media scanner
            allowScanningByMediaScanner()

            // Show a notification when the download is complete
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }

        // Enqueue the download
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Log.d("DownloadListener", "Download started with custom name: $customFileName")
    }

    /**
     * Handles downloading files from blob URLs.
     *
     * @param webView The WebView instance.
     * @param blobUrl The blob URL to download.
     * @param context The context to access the file system.
     */
    private fun handleBlobUrl(webView: WebView, blobUrl: String, context: Context) {
        // Add a JavaScript interface to handle the blob data
        webView.addJavascriptInterface(BlobHandler(context), "Android")

        // Inject JavaScript to fetch the blob data
        webView.evaluateJavascript(
            """
            (async () => {
                const response = await fetch('$blobUrl');
                const blob = await response.blob();
                const reader = new FileReader();
                reader.onload = function() {
                    // Ensure the Android object is available
                    if (typeof Android !== 'undefined') {
                        // Call Android method to save blob data
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

        @SuppressLint("NewApi")
        @JavascriptInterface
        fun saveBlob(dataUrl: String, mimeType: String) {
            try {
                // Extract the base64 data from the data URL
                val base64Data = dataUrl.split(",")[1]
                val fileData = Base64.decode(base64Data, Base64.DEFAULT)

                // Ask the user for a custom file name
                showCustomNameDialog(context) { customFileName ->
                    if (!TextUtils.isEmpty(customFileName)) {
                        // Save the file with the custom name
                        saveFileWithCustomName(context, customFileName, mimeType, fileData)
                    }
                }
            } catch (e: Exception) {
                Log.e("SaveBlob", "Failed to save blob: ${e.message}")
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
            // Get the content resolver and content values
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, customFileName)
                put(MediaStore.Downloads.MIME_TYPE, mimeType)
                put(
                    MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + "/EECFate"
                )
            }

            // Insert into the MediaStore
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            if (uri != null) {
                try {
                    // Open an output stream to write the file
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(fileData)
                        Log.d("FileSaveHelper", "File saved successfully: $customFileName")
                    }
                } catch (e: IOException) {
                    Log.e("FileSaveHelper", "Error saving file: ${e.message}")
                }
            } else {
                Log.e("FileSaveHelper", "Failed to insert into MediaStore")
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
                .setTitle("Enter FileName To Download ")
                .setView(editText)
                .setPositiveButton("Save") { _, _ ->
                    val customFileName = editText.text.toString().trim()
                    callback(customFileName)
                }
                .setNegativeButton("Cancel", null)
                .create()

            dialog.show()
            //Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
        }
    }
}
