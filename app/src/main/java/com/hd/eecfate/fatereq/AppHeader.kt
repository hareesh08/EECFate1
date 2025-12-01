package com.hd.eecfate.fatereq

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hd.eecfate.R
import com.hd.eecfate.disclaimer.AboutDialog

@Composable
fun AppHeader() {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val items = listOf(
        "HOME",
        "INTERNALS",
        "SEMESTER",
        "GPA CALCULATOR",
        "DOWNLOADS",
        "REFRESH"
    )
    var showAboutDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // Dynamically adjust UI elements based on screen width and height
    val iconSize = when {
        screenWidth < 360.dp -> 32.dp
        screenWidth < 480.dp -> 38.dp
        screenWidth < 600.dp -> 42.dp
        else -> 48.dp
    }
    
    val titleFontSize = when {
        screenWidth < 360.dp -> 14.sp
        screenWidth < 480.dp -> 16.sp
        screenWidth < 600.dp -> 18.sp
        else -> 20.sp
    }
    
    val fontSize = when {
        screenWidth < 360.dp -> 11.sp
        screenWidth < 480.dp -> 13.sp
        screenWidth < 600.dp -> 14.sp
        else -> 16.sp
    }
    
    val menuIconSize = when {
        screenWidth < 360.dp -> 18.dp
        screenWidth < 480.dp -> 20.dp
        else -> 24.dp
    }
    
    val horizontalPadding = when {
        screenWidth < 360.dp -> 4.dp
        screenWidth < 480.dp -> 6.dp
        screenWidth < 600.dp -> 8.dp
        else -> 12.dp
    }
    
    val spacerWidth = when {
        screenWidth < 360.dp -> 4.dp
        screenWidth < 480.dp -> 6.dp
        else -> 8.dp
    }
    
    val dropdownItemPadding = when {
        screenWidth < 360.dp -> 8.dp
        screenWidth < 480.dp -> 12.dp
        else -> 16.dp
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween, // Ensure sections are spaced well
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 8.dp)
            .wrapContentHeight()
    ) {
        // Left Section: Contains the app icon and title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.wrapContentWidth() // Ensures left section doesn't take unnecessary space
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable { showAboutDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_app_icon),
                    contentDescription = "App Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(iconSize)
                )
            }

            Spacer(modifier = Modifier.width(spacerWidth))

            // Title Section: EECFate text
            Text(
                text = "EECFate",
                maxLines = 1,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        // Center Section: Contains the DeveloperLink component and occupies more space
        DeveloperLink() // The center section takes up the available space

        // Right Section: Contains a menu button
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = horizontalPadding),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.size(menuIconSize),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(spacerWidth))
                Text(
                    text = "Menu",
                    fontSize = fontSize,
                    color = Color.Black,
                    maxLines = 1
                )
            }

            // Dropdown Menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = 10.dp, y = 5.dp),
                modifier = Modifier.background(Color.White)
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            when (item) {
                                "SEMESTER" -> context.startActivity(
                                    Intent(
                                        context,
                                        com.hd.eecfate.process.SemesterMarkView::class.java
                                    )
                                )

                                "INTERNALS" -> context.startActivity(
                                    Intent(
                                        context,
                                        com.hd.eecfate.process.InternalMarksActivity::class.java
                                    )
                                )

                                "GPA CALCULATOR" -> context.startActivity(
                                    Intent(
                                        context,
                                        com.hd.eecfate.process.FullCalculator::class.java
                                    )
                                )

                                "DOWNLOADS" -> context.startActivity(
                                    Intent(
                                        context,
                                        com.hd.eecfate.downloads.ViewDownloadsActivity::class.java
                                    )
                                )

                                "REFRESH" -> context.startActivity(
                                    Intent(
                                        context,
                                        com.hd.eecfate.util.FixApp::class.java
                                    )
                                )

                                "HOME" -> context.startActivity(
                                    Intent(
                                        context,
                                        com.hd.eecfate.process.HomePage::class.java
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        text = {
                            Text(
                                text = item,
                                fontSize = fontSize,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = dropdownItemPadding, vertical = 8.dp)
                            )
                        }
                    )
                }
            }
        }
    }

    // Show the AboutDialog when showAboutDialog is true
    if (showAboutDialog) {
        AboutDialog(
            context = context,
            isVisible = showAboutDialog,
            onDismiss = { showAboutDialog = false }
        )
    }
}



