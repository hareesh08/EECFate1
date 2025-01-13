package com.hd.eecfate.downloads.support

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun getFilesInDirectory(context: Context): List<File> {
    val downloadsFolder = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "EECFate"
    )
    if (!downloadsFolder.exists()) {
        return emptyList()
    }
    return downloadsFolder.listFiles()?.filter { it.extension.equals("pdf", ignoreCase = true) }
        ?: emptyList()
}

fun getReadableDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@SuppressLint("QueryPermissionsNeeded")
fun openFile(context: Context, file: File) {
    // Get the MIME type based on the file extension
    val extension = file.extension.lowercase(Locale.ROOT)
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "application/*"

    // Get the URI for the file using FileProvider
    val uri: Uri = FileProvider.getUriForFile(
        context,
        context.applicationContext.packageName + ".provider",
        file
    )

    // Create an Intent to view the file
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/*")
        setDataAndType(uri, "application/pdf")  // Use the determined MIME type
        flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    // Log MIME type for debugging (optional)
    //Log.d("OpenFile", "MIME type: $mimeType")

    // Check if there is an app available to handle the file
    if (intent.resolveActivity(context.packageManager) != null) {
        // Start the activity to open the file
        ContextCompat.startActivity(context, intent, null)
    } else {
        // Show a toast if no suitable app is available
        Toast.makeText(context, "No app available to open the file", Toast.LENGTH_SHORT).show()
    }
}
