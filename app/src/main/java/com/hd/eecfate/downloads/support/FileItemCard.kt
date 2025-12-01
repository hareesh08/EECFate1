package com.hd.eecfate.downloads.support

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hd.eecfate.ui.theme.LocalDimensions
import java.io.File

/**
 * Material 3 Card component for displaying a file item with metadata and actions.
 * Follows responsive design principles using LocalDimensions.
 *
 * @param file The file to display
 * @param onOpen Callback invoked when the card is clicked to open the file
 * @param onShare Callback invoked when the share button is clicked
 * @param onDelete Callback invoked when the delete button is clicked
 * @param modifier Optional modifier for the card
 */
@Composable
fun FileItemCard(
    file: File,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensions.paddingMedium,
                vertical = dimensions.paddingSmall
            )
            .clickable { onOpen() }
            .semantics {
                contentDescription = "File: ${file.name}"
            },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensions.cardElevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // File icon
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = "PDF file icon",
                modifier = Modifier
                    .size(dimensions.iconSizeLarge)
                    .semantics {
                        contentDescription = "PDF file icon"
                    },
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(dimensions.spacingMedium))

            // File metadata
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.semantics {
                        contentDescription = "File name: ${file.name}"
                    }
                )

                Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                Text(
                    text = formatFileSize(file.length()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.semantics {
                        contentDescription = "File size: ${formatFileSize(file.length())}"
                    }
                )

                Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                Text(
                    text = "Last Modified: ${getReadableDate(file.lastModified())}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.semantics {
                        contentDescription = "Last modified: ${getReadableDate(file.lastModified())}"
                    }
                )
            }

            Spacer(modifier = Modifier.width(dimensions.spacingSmall))

            // Action buttons
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(dimensions.minTouchTarget)
                        .semantics {
                            contentDescription = "Share file ${file.name}"
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(dimensions.minTouchTarget)
                        .semantics {
                            contentDescription = "Delete file ${file.name}"
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * Formats file size in bytes to a human-readable string (KB or MB).
 *
 * @param bytes File size in bytes
 * @return Formatted string with appropriate unit
 */
private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> String.format("%.2f KB", bytes / 1024.0)
        else -> String.format("%.2f MB", bytes / (1024.0 * 1024.0))
    }
}
