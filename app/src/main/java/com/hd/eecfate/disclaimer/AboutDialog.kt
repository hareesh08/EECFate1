package com.hd.eecfate.disclaimer

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.io.File

@Composable
fun AboutDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    context: Context
) {
    val showDisclaimerDialog = remember { mutableStateOf(false) }
    val appContext = LocalContext.current

    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App Title
                    Text(
                        text = "EECFate",
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // App Version
                    Text(
                        text = "Version 6.9.8",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Organization Info
                    Text(
                        text = "JLabs",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // App Purpose - Formatted Text with Bold and Regular Styles
                    Text(
                        text = buildAnnotatedString {
                            append("EECFate is an ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("unofficial web-based application")
                            }
                            append(" developed by JLabs for educational purposes. It provides easy access to the ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("SRM Easwari ERP Portal")
                            }
                            append(" data in a more user-friendly format.")
                        },
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Creator Info
                    Text(
                        text = "Created by Hareesh",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Clickable Email and Instagram Links
                    val email = "hareeshcode020@yahoo.com"
                    val instagramHandle = "j_.a_.r_.v_.i_.s"

                    Text(
                        text = email,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clickable {
                                val emailIntent = Intent(
                                    Intent.ACTION_SENDTO,
                                    Uri.parse("mailto:$email")
                                )
                                appContext.startActivity(emailIntent)
                            }
                    )

                    Text(
                        text = "Instagram: @$instagramHandle",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .clickable {
                                val instagramIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.instagram.com/j_.a_.r_.v_.i_.s/")
                                )
                                appContext.startActivity(instagramIntent)
                            }
                    )

                    // Disclaimer Button
                    OutlinedButton(
                        onClick = { showDisclaimerDialog.value = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Disclaimer")
                    }


                    // Share App Button
                    Button(
                        onClick = {
                            val packageName = context.packageName
                            try {
                                // Offline sharing - retrieve the base.apk file
                                val apkPath = context.packageManager.getApplicationInfo(
                                    packageName,
                                    0
                                ).sourceDir
                                val apkFile = File(apkPath)
                                val uri = Uri.fromFile(apkFile)

                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/vnd.android.package-archive"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "EECFate is an unofficial web-based application. It provides easy access to the SRM Easwari ERP Portal data in a more user-friendly format.\n\n" +
                                                "Download the app offline from this APK file or online via Google Drive: https://drive.google.com/drive/folders/11w9qHN_LgauXNpcOYlr-YM252HvyyN5E?usp=drive_link"
                                    )
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        "Share App Via"
                                    )
                                )
                            } catch (e: Exception) {
                                // Fallback: Share only the Google Drive link with the description
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "EECFate is an unofficial web-based application. It provides easy access to the SRM Easwari ERP Portal data in a more user-friendly format.\n\n" +
                                                "Download the app online via Google Drive: https://drive.google.com/drive/folders/11w9qHN_LgauXNpcOYlr-YM252HvyyN5E?usp=drive_link"
                                    )
                                }
                                context.startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        "Share App Via"
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("Share App")
                    }


                }
            }
        }
    }

    // Disclaimer Dialog
    if (showDisclaimerDialog.value) {
        DisclaimerDialog(
            context = context,
            onDisclaimerAccepted = {
                showDisclaimerDialog.value = false // Close Disclaimer
            }
        )
    }
}
