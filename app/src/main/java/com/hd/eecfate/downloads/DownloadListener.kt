package com.hd.eecfate.downloads

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException

object DownloadListener {

    private const val TAG = "DownloadListener"
    private val activeDownloads = mutableMapOf<Long, String>() // downloadId to fileName mapping

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
        try {
            showCustomNameDialog(context) { customFileName ->
                if (!TextUtils.isEmpty(customFileName)) {
                    saveFileWithCustomName(context, url, customFileName, mimeType)
                } else {
                    Toast.makeText(context, "File name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling HTTP download", e)
            Toast.makeText(context, "Failed to start download: ${e.message}", Toast.LENGTH_LONG).show()
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
        try {
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

            // Ensure notification channel is created
            DownloadNotificationManager.createNotificationChannel(context)

            val request = DownloadManager.Request(Uri.parse(url)).apply {
                val cookie = CookieManager.getInstance().getCookie(url)
                if (!cookie.isNullOrEmpty()) {
                    addRequestHeader("Cookie", cookie)
                }
                addRequestHeader("User-Agent", "Mozilla/5.0 (Linux; Android) AppleWebKit/537.36")
                setTitle("Downloading $customFileName")
                setDescription("Downloading to EECFate folder")
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "EECFate/$customFileName"
                )
                allowScanningByMediaScanner()
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
                )
                setAllowedOverMetered(true)
                setAllowedOverRoaming(false)
            }

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)
            
            // Track active download
            activeDownloads[downloadId] = customFileName
            
            Log.d(TAG, "Download started: $customFileName (ID: $downloadId)")
            Toast.makeText(context, "Download started: $customFileName", Toast.LENGTH_SHORT).show()

            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent.action) {
                        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                        if (id == downloadId) {
                            handleDownloadComplete(context, downloadManager, id, customFileName)
                            activeDownloads.remove(id)
                            try {
                                context.unregisterReceiver(this)
                            } catch (e: Exception) {
                                Log.e(TAG, "Error unregistering receiver", e)
                            }
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
        } catch (e: Exception) {
            Log.e(TAG, "Error saving file", e)
            Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Handles download completion and shows appropriate feedback
     */
    private fun handleDownloadComplete(
        context: Context,
        downloadManager: DownloadManager,
        downloadId: Long,
        fileName: String
    ) {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        
        if (cursor.moveToFirst()) {
            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(statusIndex)
            
            when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    Log.d(TAG, "Download successful: $fileName")
                    Toast.makeText(context, "Download complete: $fileName", Toast.LENGTH_LONG).show()
                }
                DownloadManager.STATUS_FAILED -> {
                    val reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                    val reason = cursor.getInt(reasonIndex)
                    val errorMessage = getDownloadErrorMessage(reason)
                    Log.e(TAG, "Download failed: $fileName - $errorMessage")
                    Toast.makeText(context, "Download failed: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }
        }
        cursor.close()
    }

    /**
     * Gets human-readable error message for download failure
     */
    private fun getDownloadErrorMessage(reason: Int): String {
        return when (reason) {
            DownloadManager.ERROR_CANNOT_RESUME -> "Cannot resume download"
            DownloadManager.ERROR_DEVICE_NOT_FOUND -> "No storage device found"
            DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "File already exists"
            DownloadManager.ERROR_FILE_ERROR -> "Storage error occurred"
            DownloadManager.ERROR_HTTP_DATA_ERROR -> "HTTP data error"
            DownloadManager.ERROR_INSUFFICIENT_SPACE -> "Insufficient storage space"
            DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "Too many redirects"
            DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "Server error"
            DownloadManager.ERROR_UNKNOWN -> "Unknown error"
            else -> "Download failed"
        }
    }

    /**
     * Handles downloading files from blob URLs.
     *
     * @param webView The WebView instance.
     * @param blobUrl The blob URL to download.
     * @param context The context to access the file system.
     */
    private fun handleBlobUrl(webView: WebView, blobUrl: String, context: Context) {
        try {
            webView.addJavascriptInterface(BlobHandler(context), "Android")

            webView.evaluateJavascript(
                """
                (async () => {
                    try {
                        const response = await fetch('$blobUrl');
                        const blob = await response.blob();
                        const reader = new FileReader();
                        reader.onload = function() {
                            if (typeof Android !== 'undefined') {
                                Android.saveBlob(reader.result, blob.type);
                            }
                        };
                        reader.onerror = function() {
                            console.error('Failed to read blob');
                        };
                        reader.readAsDataURL(blob);
                    } catch (error) {
                        console.error('Blob download error:', error);
                    }
                })();
                """.trimIndent(),
                null
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error handling blob URL", e)
            Toast.makeText(context, "Failed to process download", Toast.LENGTH_SHORT).show()
        }
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
                if (dataUrl.isEmpty() || !dataUrl.contains(",")) {
                    Log.e(TAG, "Invalid data URL format")
                    (context as? Activity)?.runOnUiThread {
                        Toast.makeText(context, "Invalid file data", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                val base64Data = dataUrl.split(",")[1]
                val fileData = Base64.decode(base64Data, Base64.DEFAULT)

                if (fileData.isEmpty()) {
                    Log.e(TAG, "Empty file data")
                    (context as? Activity)?.runOnUiThread {
                        Toast.makeText(context, "File is empty", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                (context as? Activity)?.runOnUiThread {
                    showCustomNameDialog(context) { customFileName ->
                        if (!TextUtils.isEmpty(customFileName)) {
                            saveFileWithCustomName(context, customFileName, mimeType, fileData)
                        } else {
                            Toast.makeText(context, "File name cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving blob", e)
                (context as? Activity)?.runOnUiThread {
                    Toast.makeText(context, "Failed to save file: ${e.message}", Toast.LENGTH_LONG).show()
                }
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
            try {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, customFileName)
                    put(MediaStore.Downloads.MIME_TYPE, mimeType)
                    put(
                        MediaStore.Downloads.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS + "/EECFate"
                    )
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let {
                    try {
                        resolver.openOutputStream(it)?.use { outputStream ->
                            outputStream.write(fileData)
                            outputStream.flush()
                        }
                        
                        // Mark as complete
                        contentValues.clear()
                        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                        resolver.update(it, contentValues, null, null)
                        
                        Log.d(TAG, "Blob file saved successfully: $customFileName")
                        (context as? Activity)?.runOnUiThread {
                            Toast.makeText(context, "File saved: $customFileName", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: IOException) {
                        Log.e(TAG, "Error writing file", e)
                        resolver.delete(it, null, null)
                        (context as? Activity)?.runOnUiThread {
                            Toast.makeText(context, "Failed to write file: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } ?: run {
                    Log.e(TAG, "Failed to create file URI")
                    (context as? Activity)?.runOnUiThread {
                        Toast.makeText(context, "Failed to create file", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in saveFileWithCustomName", e)
                (context as? Activity)?.runOnUiThread {
                    Toast.makeText(context, "Save failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
