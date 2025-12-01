package com.hd.eecfate.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/**
 * Reusable confirmation dialog component following Material Design 3 guidelines.
 * Displays a dialog with a title, message, and confirm/dismiss actions.
 *
 * @param title The dialog title text
 * @param message The dialog message/body text
 * @param confirmButtonText Text for the confirm button (default: "Confirm")
 * @param dismissButtonText Text for the dismiss button (default: "Cancel")
 * @param onConfirm Callback invoked when the confirm button is clicked
 * @param onDismiss Callback invoked when the dismiss button is clicked or dialog is dismissed
 * @param isDestructive Whether this is a destructive action (uses error color for confirm button)
 * @param modifier Optional modifier for the dialog
 */
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmButtonText: String = "Confirm",
    dismissButtonText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDestructive: Boolean = false,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.semantics {
                    contentDescription = "Dialog title: $title"
                }
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.semantics {
                    contentDescription = "Dialog message: $message"
                }
            )
        },
        confirmButton = {
            if (isDestructive) {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.semantics {
                        contentDescription = "$confirmButtonText button"
                    }
                ) {
                    Text(confirmButtonText)
                }
            } else {
                TextButton(
                    onClick = onConfirm,
                    modifier = Modifier.semantics {
                        contentDescription = "$confirmButtonText button"
                    }
                ) {
                    Text(confirmButtonText)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.semantics {
                    contentDescription = "$dismissButtonText button"
                }
            ) {
                Text(dismissButtonText)
            }
        },
        modifier = modifier
    )
}
