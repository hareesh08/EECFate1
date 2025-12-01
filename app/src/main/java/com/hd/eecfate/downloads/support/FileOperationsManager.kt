package com.hd.eecfate.downloads.support

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Result type for file operations with explicit error handling
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Throwable? = null) : Result<Nothing>()
}

/**
 * Manager for file operations with Result-based error handling
 * Handles PDF file discovery, opening, sharing, and deletion
 */
object FileOperationsManager {
    
    private const val DOWNLOADS_FOLDER_NAME = "EECFate"
    private const val PDF_EXTENSION = "pdf"
    private const val PDF_MIME_TYPE = "application/pdf"
    private const val GENERIC_MIME_TYPE = "application/*"
    
    /**
     * Discovers all PDF files in the downloads directory
     * @param context Android context
     * @return Result containing list of PDF files or error
     */
    fun getDownloadedFiles(context: Context): Result<List<File>> {
        return try {
            val downloadsFolder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                DOWNLOADS_FOLDER_NAME
            )
            
            if (!downloadsFolder.exists()) {
                Result.Success(emptyList())
            } else {
                val pdfFiles = downloadsFolder.listFiles()?.filter { 
                    it.extension.equals(PDF_EXTENSION, ignoreCase = true) 
                } ?: emptyList()
                
                Result.Success(pdfFiles)
            }
        } catch (e: Exception) {
            Result.Error("Failed to retrieve downloaded files", e)
        }
    }
    
    /**
     * Formats file size from bytes to human-readable format (KB/MB)
     * @param bytes File size in bytes
     * @return Formatted string (e.g., "1.50 MB", "500 KB")
     */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format(Locale.US, "%.2f KB", bytes / 1024.0)
            else -> String.format(Locale.US, "%.2f MB", bytes / (1024.0 * 1024.0))
        }
    }
    
    /**
     * Formats timestamp to readable date string
     * @param timestamp Unix timestamp in milliseconds
     * @return Formatted date string (e.g., "29 Nov 2025 14:30:00")
     */
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Opens a file using the appropriate application
     * @param context Android context
     * @param file File to open
     * @return Result indicating success or error
     */
    @SuppressLint("QueryPermissionsNeeded")
    fun openFile(context: Context, file: File): Result<Unit> {
        return try {
            if (!file.exists()) {
                return Result.Error("File not found: ${file.name}")
            }
            
            val mimeType = getMimeType(file)
            val uri = generateFileProviderUri(context, file)
                ?: return Result.Error("Failed to generate URI for file")
            
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            
            if (intent.resolveActivity(context.packageManager) != null) {
                ContextCompat.startActivity(context, intent, null)
                Result.Success(Unit)
            } else {
                Result.Error("No application available to open $mimeType files")
            }
        } catch (e: Exception) {
            Result.Error("Failed to open file: ${e.message}", e)
        }
    }
    
    /**
     * Shares a file using Android share sheet
     * @param context Android context
     * @param file File to share
     * @return Result indicating success or error
     */
    @SuppressLint("QueryPermissionsNeeded")
    fun shareFile(context: Context, file: File): Result<Unit> {
        return try {
            if (!file.exists()) {
                return Result.Error("File not found: ${file.name}")
            }
            
            val uri = generateFileProviderUri(context, file)
                ?: return Result.Error("Failed to generate URI for file sharing")
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = PDF_MIME_TYPE
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            // Check if any app can handle PDF sharing
            val packageManager = context.packageManager
            if (intent.resolveActivity(packageManager) == null) {
                return Result.Error("No application available to share PDF files")
            }
            
            val chooserIntent = Intent.createChooser(intent, "Share ${file.name}")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            context.startActivity(chooserIntent)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to share file: ${e.message}", e)
        }
    }
    
    /**
     * Deletes a file from the file system
     * @param file File to delete
     * @return Result indicating success or error
     */
    fun deleteFile(file: File): Result<Unit> {
        return try {
            if (!file.exists()) {
                return Result.Error("File not found: ${file.name}")
            }
            
            val deleted = file.delete()
            if (deleted) {
                Result.Success(Unit)
            } else {
                Result.Error("Failed to delete file: ${file.name}")
            }
        } catch (e: Exception) {
            Result.Error("Failed to delete file: ${e.message}", e)
        }
    }
    
    /**
     * Generates a FileProvider URI for secure file sharing
     * @param context Android context
     * @param file File to generate URI for
     * @return URI or null if generation fails
     */
    private fun generateFileProviderUri(context: Context, file: File): Uri? {
        return try {
            FileProvider.getUriForFile(
                context,
                "${context.applicationContext.packageName}.provider",
                file
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Detects MIME type for a file
     * @param file File to detect MIME type for
     * @return MIME type string
     */
    private fun getMimeType(file: File): String {
        val extension = file.extension.lowercase(Locale.ROOT)
        return if (extension == PDF_EXTENSION) {
            PDF_MIME_TYPE
        } else {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: GENERIC_MIME_TYPE
        }
    }
}
