package com.hd.eecfate.downloads.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hd.eecfate.downloads.DownloadProgress
import com.hd.eecfate.downloads.DownloadStatus

/**
 * Dialog showing download progress
 */
@Composable
fun DownloadProgressDialog(
    progress: DownloadProgress,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { 
            if (progress.status == DownloadStatus.SUCCESSFUL || 
                progress.status == DownloadStatus.FAILED) {
                onDismiss()
            }
        },
        title = {
            Text(
                text = when (progress.status) {
                    DownloadStatus.SUCCESSFUL -> "Download Complete"
                    DownloadStatus.FAILED -> "Download Failed"
                    else -> "Downloading"
                }
            )
        },
        text = {
            Column {
                Text(
                    text = progress.fileName,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                when (progress.status) {
                    DownloadStatus.RUNNING, DownloadStatus.PENDING -> {
                        LinearProgressIndicator(
                            progress = progress.progressPercentage / 100f,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${progress.progressPercentage}%",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    DownloadStatus.FAILED -> {
                        Text(
                            text = progress.reason ?: "Unknown error",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    DownloadStatus.SUCCESSFUL -> {
                        Text(
                            text = "File saved successfully",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    else -> {
                        Text(
                            text = "Processing...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (progress.status == DownloadStatus.SUCCESSFUL || 
                progress.status == DownloadStatus.FAILED) {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            }
        }
    )
}
