package com.hd.eecfate.downloads.support

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.hd.eecfate.fatereq.AppHeader
import java.io.File

@Composable
fun ViewDownloadsScreen(context: Context) {
    if (isAndroid11OrAbove()) {
        if (!hasManageExternalStoragePermission(context)) {
            requestManageExternalStoragePermission(context)
        } else {
            val files = getFilesInDirectory(context)
            DisplayFiles(files, context)
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
            val files = getFilesInDirectory(context)
            DisplayFiles(files, context)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayFiles(files: List<File>, context: Context) {
    // Declare the mutable state variable to hold the list of files
    val fileList = remember { mutableStateOf(files) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Transparent),
                title = { AppHeader() }
            )
        }
    ) { paddingValues ->
        if (fileList.value.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No files found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(fileList.value.size) { index ->
                    val file = fileList.value[index]
                    FileItemView(
                        file = file,
                        onClick = { openFile(context, file) },
                        onDelete = { deleteFile(context, file, fileList) },
                        onShare = { shareFile(context, file) }
                    )
                }
            }
        }
    }
}


@Composable
fun FileItemView(file: File, onClick: () -> Unit, onDelete: () -> Unit, onShare: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Size: ${file.length() / 1024} KB",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Last Modified: ${getReadableDate(file.lastModified())}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            // Delete and Share Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

fun deleteFile(
    context: Context,
    file: File,
    fileList: MutableState<List<File>>
) {
    if (file.exists()) {
        val deleted = file.delete()
        if (deleted) {
            // Remove the file from the list after deletion
            val updatedList = fileList.value.filter { it != file }
            // Update the state with the new list
            fileList.value = updatedList
            Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show()
        }
    }
}


fun shareFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        context.applicationContext.packageName + ".provider",
        file
    )

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "application/pdf"  // Or use "application/*" to be more general
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(shareIntent, "Share PDF")
    ContextCompat.startActivity(context, chooser, null)
}



