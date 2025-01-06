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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hd.eecfate.R

@Composable
fun AppHeader() {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) } // Dropdown visibility state
    val items = listOf("HOME", "INTERNALS", "SEMESTER") // Menu items

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .wrapContentHeight()
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_app_icon),
                contentDescription = "App Icon",
                tint = Color.Unspecified,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = "EECFate",
            maxLines = 2,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        DeveloperLink()

        // Dropdown Menu Button
        Box(

            modifier = Modifier
                .wrapContentWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 28.dp),
            contentAlignment = Alignment.CenterEnd // Aligns the button content to the end
        ) {
            // Row for the Menu Icon and Text
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Menu, // Use Menu icon
                    contentDescription = "Menu",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp)) // Space between icon and text
                Text(
                    text = "Menu", // Visible "Menu" text
                    fontSize = 16.sp,
                    color = Color.Black, // Match your app theme
                    maxLines = 1
                )
            }

            // Dropdown Menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(
                    x = 50.dp,
                    y = 5.dp
                ), // Adjust the vertical offset for proper alignment
                modifier = Modifier
                    .background(
                        color = Color.White, // Change the background color to white
                        //shape = MaterialTheme.shapes.medium // Keep the rounded corners
                    )
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = false // Close menu on click
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

                                "HOME" -> context.startActivity(
                                    Intent(
                                        context,
                                        com.hd.eecfate.process.HomePage::class.java
                                    )
                                )
                            }
                        },
                        text = {
                            Text(
                                text = item,
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        },
                    )
                }
            }
        }
    }
}
