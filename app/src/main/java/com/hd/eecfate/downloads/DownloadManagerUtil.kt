package com.hd.eecfate.downloads

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import java.io.File

/**
 * Utility class for managing downloads
 */
object DownloadManagerUtil {

    /**
     * Gets all downloads from DownloadManager
     */
    fun getAllDownloads(context: Context): List<DownloadInfo> {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        val cursor = downloadManager.query(query)
        
        val downloads = mutableListOf<DownloadInfo>()
        
        cursor?.use {
            while (it.moveToNext()) {
                downloads.add(extractDownloadInfo(it))
            }
        }
        
        return downloads
    }

    /**
     * Gets active (pending/running) downloads
     */
    fun getActiveDownloads(context: Context): List<DownloadInfo> {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterByStatus(
            DownloadManager.STATUS_PENDING or DownloadManager.STATUS_RUNNING
        )
        val cursor = downloadManager.query(query)
        
        val downloads = mutableListOf<DownloadInfo>()
        
        cursor?.use {
            while (it.moveToNext()) {
                downloads.add(extractDownloadInfo(it))
            }
        }
        
        return downloads
    }

    /**
     * Cancels a download
     */
    fun cancelDownload(context: Context, downloadId: Long): Boolean {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return downloadManager.remove(downloadId) > 0
    }

    /**
     * Retries a failed download by removing and re-enqueueing
     */
    fun retryDownload(context: Context, downloadId: Long): Long? {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        
        var newDownloadId: Long? = null
        
        cursor?.use {
            if (it.moveToFirst()) {
                val uriIndex = it.getColumnIndex(DownloadManager.COLUMN_URI)
                val titleIndex = it.getColumnIndex(DownloadManager.COLUMN_TITLE)
                val descriptionIndex = it.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION)
                
                val uri = it.getString(uriIndex)
                val title = it.getString(titleIndex)
                val description = it.getString(descriptionIndex)
                
                // Remove old download
                downloadManager.remove(downloadId)
                
                // Create new download request
                val request = DownloadManager.Request(Uri.parse(uri)).apply {
                    setTitle(title)
                    setDescription(description)
                    setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "EECFate/${title.substringAfter("Downloading ")}"
                    )
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    allowScanningByMediaScanner()
                }
                
                newDownloadId = downloadManager.enqueue(request)
            }
        }
        
        return newDownloadId
    }

    /**
     * Gets download file path
     */
    fun getDownloadFilePath(context: Context, downloadId: Long): String? {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        
        var filePath: String? = null
        
        cursor?.use {
            if (it.moveToFirst()) {
                val localUriIndex = it.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                val localUri = it.getString(localUriIndex)
                filePath = localUri?.let { uri -> Uri.parse(uri).path }
            }
        }
        
        return filePath
    }

    /**
     * Extracts download information from cursor
     */
    private fun extractDownloadInfo(cursor: Cursor): DownloadInfo {
        val idIndex = cursor.getColumnIndex(DownloadManager.COLUMN_ID)
        val titleIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)
        val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        val bytesDownloadedIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
        val totalBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
        val reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
        
        return DownloadInfo(
            id = cursor.getLong(idIndex),
            title = cursor.getString(titleIndex) ?: "Unknown",
            status = cursor.getInt(statusIndex),
            bytesDownloaded = cursor.getLong(bytesDownloadedIndex),
            totalBytes = cursor.getLong(totalBytesIndex),
            reason = cursor.getInt(reasonIndex)
        )
    }
}

/**
 * Data class representing download information
 */
data class DownloadInfo(
    val id: Long,
    val title: String,
    val status: Int,
    val bytesDownloaded: Long,
    val totalBytes: Long,
    val reason: Int
) {
    val progressPercentage: Int
        get() = if (totalBytes > 0) ((bytesDownloaded * 100) / totalBytes).toInt() else 0
    
    val isActive: Boolean
        get() = status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_RUNNING
    
    val isComplete: Boolean
        get() = status == DownloadManager.STATUS_SUCCESSFUL
    
    val isFailed: Boolean
        get() = status == DownloadManager.STATUS_FAILED
}
