package com.hd.eecfate.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hd.eecfate.ui.theme.LocalDimensions

/**
 * Reusable empty state component for displaying when lists or content areas are empty.
 * Follows Material Design 3 guidelines with proper accessibility support.
 *
 * @param message The message to display to the user
 * @param icon Optional icon to display above the message (default: folder icon)
 * @param description Optional additional description text
 * @param modifier Optional modifier for the component
 */
@Composable
fun EmptyState(
    message: String,
    icon: ImageVector = Icons.Outlined.FolderOpen,
    description: String? = null,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensions.paddingExtraLarge)
            .semantics(mergeDescendants = true) {
                contentDescription = buildString {
                    append("Empty state: $message")
                    if (description != null) {
                        append(". $description")
                    }
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon with minimum touch target size consideration
        Icon(
            imageVector = icon,
            contentDescription = "Empty state icon",
            modifier = Modifier
                .size(dimensions.iconSizeLarge * 2) // 64dp on compact, larger on tablets
                .semantics {
                    contentDescription = "Empty state icon"
                },
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(dimensions.spacingLarge))

        // Primary message
        Text(
            text = message,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics {
                contentDescription = message
            }
        )

        // Optional description
        if (description != null) {
            Spacer(modifier = Modifier.height(dimensions.spacingSmall))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics {
                    contentDescription = description
                }
            )
        }
    }
}
