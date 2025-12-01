package com.hd.eecfate.downloads.support

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hd.eecfate.fatereq.AppHeader
import com.hd.eecfate.ui.components.ConfirmationDialog
import com.hd.eecfate.ui.components.EmptyState
import com.hd.eecfate.ui.theme.LocalDimensions
import java.io.File

@Composable
fun ViewDownloadsScreen(context: Context) {
    var refreshTrigger by remember { mutableStateOf(0) }
    
    if (isAndroid11OrAbove()) {
        if (!hasManageExternalStoragePermission(context)) {
            requestManageExternalStoragePermission(context)
        } else {
            val files = remember(refreshTrigger) { getFilesInDirectory(context) }
            DisplayFiles(files, context) { refreshTrigger++ }
        }
    } else {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1
            )
        } else {
            val files = remember(refreshTrigger) { getFilesInDirectory(context) }
            DisplayFiles(files, context) { refreshTrigger++ }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayFiles(files: List<File>, context: Context, onRefresh: () -> Unit = {}) {
    val dimensions = LocalDimensions.current
    
    // State management for file list
    var fileList by remember { mutableStateOf(files) }
    
    // State management for delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var fileToDelete by remember { mutableStateOf<File?>(null) }
    
    // State for error messages
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Transparent),
                title = { AppHeader() },
                modifier = Modifier.height(52.dp)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            // Warning text about file downloads
            Text(
                text = "Please Download Files From Home Page. Don't use any other",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 8.sp),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 0.dp,
                        bottom = dimensions.paddingSmall
                    )
            )

            // Display empty state or file list
            if (fileList.isEmpty()) {
                EmptyState(
                    message = "No files found",
                    description = "Download files from the home page to see them here"
                )
            } else {
                // LazyColumn for displaying files with responsive spacing
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        vertical = dimensions.paddingSmall
                    )
                ) {
                    items(fileList.size) { index ->
                        val file = fileList[index]
                        FileItemCard(
                            file = file,
                            onOpen = { 
                                when (val result = FileOperationsManager.openFile(context, file)) {
                                    is Result.Error -> {
                                        errorMessage = result.message
                                        Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                    }
                                    is Result.Success -> { /* File opened successfully */ }
                                }
                            },
                            onShare = { 
                                when (val result = FileOperationsManager.shareFile(context, file)) {
                                    is Result.Error -> {
                                        errorMessage = result.message
                                        Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                    }
                                    is Result.Success -> { /* File shared successfully */ }
                                }
                            },
                            onDelete = {
                                fileToDelete = file
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }

        // Delete confirmation dialog
        if (showDeleteDialog && fileToDelete != null) {
            ConfirmationDialog(
                title = "Delete File",
                message = "Are you sure you want to delete '${fileToDelete?.name}'? This action cannot be undone.",
                confirmButtonText = "Delete",
                dismissButtonText = "Cancel",
                onConfirm = {
                    fileToDelete?.let { file ->
                        when (val result = FileOperationsManager.deleteFile(file)) {
                            is Result.Success -> {
                                // Update file list after successful deletion
                                fileList = fileList.filter { it != file }
                                Toast.makeText(context, "File deleted successfully", Toast.LENGTH_SHORT).show()
                                onRefresh()
                            }
                            is Result.Error -> {
                                errorMessage = result.message
                                Toast.makeText(context, "Failed to delete: ${result.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    showDeleteDialog = false
                    fileToDelete = null
                },
                onDismiss = {
                    showDeleteDialog = false
                    fileToDelete = null
                },
                isDestructive = true
            )
        }
    }
}




