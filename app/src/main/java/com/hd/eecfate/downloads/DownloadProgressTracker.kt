package com.hd.eecfate.downloads

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Data class representing download progress information
 */
data class DownloadProgress(
    val downloadId: Long,
    val fileName: String,
    val bytesDownloaded: Long,
    val totalBytes: Long,
    val status: DownloadStatus,
    val reason: String? = null
) {
    val progressPercentage: Int
        get() = if (totalBytes > 0) ((bytesDownloaded * 100) / totalBytes).toInt() else 0
}

/**
 * Enum representing download status
 */
enum class DownloadStatus {
    PENDING,
    RUNNING,
    PAUSED,
    SUCCESSFUL,
    FAILED,
    UNKNOWN
}

/**
 * Tracks download progress for a given download ID
 */
object DownloadProgressTracker {

    /**
     * Creates a Flow that emits download progress updates
     * @param context Android context
     * @param downloadId The download ID to track
     * @param fileName The name of the file being downloaded
     * @return Flow emitting DownloadProgress updates
     */
    fun trackDownload(
        context: Context,
        downloadId: Long,
        fileName: String
    ): Flow<DownloadProgress> = flow {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        
        var isComplete = false
        while (!isComplete) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor: Cursor? = downloadManager.query(query)
            
            cursor?.use {
                if (it.moveToFirst()) {
                    val progress = extractProgressFromCursor(it, downloadId, fileName)
                    emit(progress)
                    
                    isComplete = progress.status == DownloadStatus.SUCCESSFUL ||
                                progress.status == DownloadStatus.FAILED
                }
            }
            
            if (!isComplete) {
                delay(500) // Update every 500ms
            }
        }
    }

    /**
     * Extracts download progress information from cursor
     */
    private fun extractProgressFromCursor(
        cursor: Cursor,
        downloadId: Long,
        fileName: String
    ): DownloadProgress {
        val bytesDownloaded = cursor.getLong(
            cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
        )
        val totalBytes = cursor.getLong(
            cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
        )
        val statusCode = cursor.getInt(
            cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
        )
        val reasonCode = cursor.getInt(
            cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON)
        )
        
        val status = mapStatusCode(statusCode)
        val reason = if (status == DownloadStatus.FAILED) {
            getFailureReason(reasonCode)
        } else null
        
        return DownloadProgress(
            downloadId = downloadId,
            fileName = fileName,
            bytesDownloaded = bytesDownloaded,
            totalBytes = totalBytes,
            status = status,
            reason = reason
        )
    }

    /**
     * Maps DownloadManager status code to DownloadStatus enum
     */
    private fun mapStatusCode(statusCode: Int): DownloadStatus {
        return when (statusCode) {
            DownloadManager.STATUS_PENDING -> DownloadStatus.PENDING
            DownloadManager.STATUS_RUNNING -> DownloadStatus.RUNNING
            DownloadManager.STATUS_PAUSED -> DownloadStatus.PAUSED
            DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.SUCCESSFUL
            DownloadManager.STATUS_FAILED -> DownloadStatus.FAILED
            else -> DownloadStatus.UNKNOWN
        }
    }

    /**
     * Gets human-readable failure reason
     */
    private fun getFailureReason(reasonCode: Int): String {
        return when (reasonCode) {
            DownloadManager.ERROR_CANNOT_RESUME -> "Cannot resume download"
            DownloadManager.ERROR_DEVICE_NOT_FOUND -> "No external storage device found"
            DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "File already exists"
            DownloadManager.ERROR_FILE_ERROR -> "Storage error"
            DownloadManager.ERROR_HTTP_DATA_ERROR -> "HTTP data error"
            DownloadManager.ERROR_INSUFFICIENT_SPACE -> "Insufficient storage space"
            DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "Too many redirects"
            DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "Unhandled HTTP error"
            DownloadManager.ERROR_UNKNOWN -> "Unknown error"
            else -> "Download failed"
        }
    }
}
