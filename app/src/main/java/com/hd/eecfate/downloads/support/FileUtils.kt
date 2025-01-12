package com.hd.eecfate.downloads.support

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Environment
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
    val uri = FileProvider.getUriForFile(
        context,
        context.applicationContext.packageName + ".provider",  // Ensure this matches your app's package name
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/*")  // Use more general MIME type
        flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    // Set the package explicitly to Google Drive
    intent.setPackage("com.google.android.apps.docs")  // Google Drive package name

    // Check if Google Drive is installed and start activity if it's available
    if (intent.resolveActivity(context.packageManager) != null) {
        ContextCompat.startActivity(context, intent, null)
    } else {
        Toast.makeText(context, "Google Drive is not installed", Toast.LENGTH_SHORT).show()
    }
}


