package com.hd.eecfate.disclaimer

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ShowDisclaimerIfNeeded(
    context: Context,
    onDisclaimerAccepted: @Composable () -> Unit,
    onDisclaimerDeclined: () -> Unit
) {
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val showDisclaimer =
        remember { mutableStateOf(!sharedPreferences.getBoolean("DISCLAIMER_ACCEPTED", false)) }

    // Get screen configuration for responsive design
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isSmallScreen = screenWidth < 360.dp || screenHeight < 640.dp
    val isMediumScreen = screenWidth < 600.dp

    // Responsive values
    val horizontalPadding = when {
        isSmallScreen -> 12.dp
        isMediumScreen -> 16.dp
        else -> 24.dp
    }
    val verticalPadding = when {
        isSmallScreen -> 8.dp
        isMediumScreen -> 12.dp
        else -> 16.dp
    }
    val titleFontSize = when {
        isSmallScreen -> 20.sp
        isMediumScreen -> 24.sp
        else -> 28.sp
    }
    val titleBottomPadding = when {
        isSmallScreen -> 12.dp
        isMediumScreen -> 16.dp
        else -> 24.dp
    }
    val buttonHeight = when {
        isSmallScreen -> 44.dp
        isMediumScreen -> 48.dp
        else -> 56.dp
    }
    val buttonFontSize = when {
        isSmallScreen -> 14.sp
        isMediumScreen -> 16.sp
        else -> 18.sp
    }
    val buttonSpacing = when {
        isSmallScreen -> 8.dp
        else -> 16.dp
    }

    if (showDisclaimer.value) {
        Dialog(
            onDismissRequest = { /* Prevent dismiss without action */ },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = horizontalPadding, vertical = verticalPadding)
                ) {
                    // Header
                    Text(
                        text = "Disclaimer & Privacy Policy",
                        style = TextStyle(
                            fontSize = titleFontSize,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            lineHeight = titleFontSize * 1.3f
                        ),
                        modifier = Modifier.padding(bottom = titleBottomPadding)
                    )

                    // Scrollable content
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = if (isSmallScreen) 8.dp else 16.dp)
                        ) {
                            // Disclaimer Section
                            BoldSectionTitle(text = "Disclaimer", isSmallScreen = isSmallScreen, isMediumScreen = isMediumScreen)
                            FormattedContentText(text = DISCLAIMER_TEXT, isSmallScreen = isSmallScreen, isMediumScreen = isMediumScreen)
                            Spacer(modifier = Modifier.height(if (isSmallScreen) 16.dp else if (isMediumScreen) 24.dp else 32.dp))

                            // Privacy Policy Section
                            BoldSectionTitle(text = "Privacy Policy", isSmallScreen = isSmallScreen, isMediumScreen = isMediumScreen)
                            FormattedContentText(text = PRIVACY_POLICY_TEXT, isSmallScreen = isSmallScreen, isMediumScreen = isMediumScreen)
                        }
                    }

                    // Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = if (isSmallScreen) 8.dp else 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onDisclaimerDeclined()
                                showDisclaimer.value = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(buttonHeight)
                        ) {
                            Text(
                                "Decline",
                                style = TextStyle(fontSize = buttonFontSize)
                            )
                        }
                        Button(
                            onClick = {
                                sharedPreferences.edit().putBoolean("DISCLAIMER_ACCEPTED", true)
                                    .apply()
                                showDisclaimer.value = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(buttonHeight)
                        ) {
                            Text(
                                "Accept",
                                style = TextStyle(fontSize = buttonFontSize)
                            )
                        }
                    }
                }
            }
        }
    }

    if (!showDisclaimer.value) {
        onDisclaimerAccepted()
    }
}

@Composable
private fun BoldSectionTitle(text: String, isSmallScreen: Boolean, isMediumScreen: Boolean) {
    val fontSize = when {
        isSmallScreen -> 18.sp
        isMediumScreen -> 20.sp
        else -> 24.sp
    }
    val bottomPadding = when {
        isSmallScreen -> 8.dp
        isMediumScreen -> 12.dp
        else -> 16.dp
    }
    
    Text(
        text = text,
        style = TextStyle(
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = fontSize * 1.3f
        ),
        modifier = Modifier.padding(bottom = bottomPadding)
    )
}

@Composable
private fun FormattedContentText(text: String, isSmallScreen: Boolean, isMediumScreen: Boolean) {
    val boldFontSize = when {
        isSmallScreen -> 14.sp
        isMediumScreen -> 16.sp
        else -> 18.sp
    }
    val normalFontSize = when {
        isSmallScreen -> 12.sp
        isMediumScreen -> 14.sp
        else -> 16.sp
    }
    val paragraphBottomPadding = when {
        isSmallScreen -> 8.dp
        isMediumScreen -> 12.dp
        else -> 16.dp
    }
    val lineBottomPadding = when {
        isSmallScreen -> 6.dp
        else -> 8.dp
    }
    
    val paragraphs = text.split("\n\n")
    Column {
        paragraphs.forEach { paragraph ->
            if (paragraph.isNotBlank()) {
                val lines = paragraph.split("\n")
                Column(modifier = Modifier.padding(bottom = paragraphBottomPadding)) {
                    lines.forEach { line ->
                        if (line.startsWith("**")) {
                            Text(
                                text = line.removeSurrounding("**"),
                                style = TextStyle(
                                    fontSize = boldFontSize,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = boldFontSize * 1.4f,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(bottom = lineBottomPadding)
                            )
                        } else {
                            Text(
                                text = line,
                                style = TextStyle(
                                    fontSize = normalFontSize,
                                    lineHeight = normalFontSize * 1.5f,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f)
                                ),
                                modifier = Modifier.padding(bottom = if (isSmallScreen) 2.dp else 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private const val DISCLAIMER_TEXT = """
**General Information**
This is an unofficial web-based application developed by JLabs for educational, testing, and learning purposes. It is a converted version of the SRM Easwari ERP Portal and is neither affiliated with, nor endorsed by, SRM Easwari in any capacity. The app is provided "as-is" with no guarantees or warranties.

**Semester Marks**
• The app allows users to view semester marks; however, the accuracy, completeness, and timeliness of these marks are solely dependent on the data provided by the SRM Easwari ERP Portal.
• The app creator assumes no responsibility for discrepancies, errors, or omissions in the displayed marks or any other data, and makes no guarantees regarding the accuracy or reliability of such data.
• Users must independently verify all information, including marks, with the official SRM Easwari ERP Portal. The app creator is not responsible for any changes to or removal of data from the portal.

**Responsibility for Claims**
• The app creator is not responsible for any claims, disputes, or actions that arise from the use of the app, including claims related to data accuracy, errors, or omissions.
• Any claims related to the accuracy, correctness, or modification of data displayed in the app must be addressed directly with SRM Easwari. The app creator shall not be held liable for any academic, financial, legal, or reputational damages resulting from the use or misuse of the app.
• Users agree to indemnify, defend, and hold harmless the app creator, developers, and any associated parties from any claims, damages, liabilities, or legal costs arising from their use of the app or reliance on its data.

**Data Accuracy and Integrity**
• The app relies entirely on data sourced from the SRM Easwari ERP Portal, which may be subject to change without notice. The app makes no representations regarding the completeness or correctness of the data.
• Users acknowledge that the app creator cannot control or verify the data retrieved from the SRM Easwari ERP Portal and, as such, cannot guarantee its accuracy.
• The app does not provide real-time data and is not responsible for any delays, discrepancies, or errors in the data provided.

**App Usage and Limitations**
• By using the app, users agree to assume all risks associated with the use of the app. The app creator will not be liable for any direct or indirect damages, including but not limited to, data loss, system errors, device malfunction, or academic consequences.
• The app may be subject to interruptions, bugs, or other technical issues. The app creator does not guarantee uninterrupted or error-free service and is not responsible for any impact caused by such interruptions.
• The app creator reserves the right to discontinue or modify the app, its functionality, or features at any time, without prior notice or liability.

**Changes to the Disclaimer**
• The app creator reserves the right to update, modify, or alter this disclaimer at any time without prior notice. By continuing to use the app after such changes, users accept the updated terms. It is the user's responsibility to review the disclaimer periodically.
"""


private const val PRIVACY_POLICY_TEXT = """
**Data Collection**
• The app does not collect, store, or track any personal data. All data, such as semester marks, is fetched directly from the SRM Easwari ERP Portal.
• No personally identifiable information (PII) is gathered, processed, or stored by the app creator. The app does not request or require any personal information from users.

**User Consent**
• By accessing and using this app, users acknowledge and consent to the app being an unofficial educational tool that fetches data from the SRM Easwari ERP Portal.
• Users further agree that any issues or concerns related to the accuracy or correctness of data must be addressed to the SRM Easwari authorities. The app creator is not responsible for data errors, inaccuracies, or updates.

**Data Security**
• As no personal data is stored by the app, there are no specific security measures for protecting data within the app. However, users should exercise caution and avoid entering any personal or sensitive information.
• The app creator is not responsible for any data breaches, loss, or unauthorized access that may occur during the use of the app.

**Third-Party Advertisements and Data Sharing**
• The app does not display third-party advertisements, nor does it track, share, or sell user data to third parties for commercial purposes.
• The app does not use analytics tools or share any data with third-party service providers.

**Legal Disclaimer**
• The app is provided "as-is," and the app creator disclaims all warranties, express or implied, regarding the functionality, reliability, accuracy, or performance of the app. 
• The app creator is not liable for any damages, losses, or legal consequences (including academic, financial, or reputational damage) arising from the use or misuse of the app, including any disputes over the data or functionality provided.
• Users agree to hold the app creator harmless and waive any claims against the app creator, its developers, and affiliates for any consequences resulting from the app’s use.

**Changes to the Privacy Policy**
• The app creator reserves the right to modify or update this Privacy Policy at any time. Users will be deemed to have accepted the changes by continuing to use the app after the update.
• Users are encouraged to review this Privacy Policy periodically to stay informed of any changes. The most recent version will always be available within the app.

**Contact Information**
• For further inquiries or concerns, please contact us at: hareeshcode020@yahoo.com
"""
