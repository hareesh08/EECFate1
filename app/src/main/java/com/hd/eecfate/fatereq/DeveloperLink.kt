package com.hd.eecfate.fatereq

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.hd.eecfate.util.checkServerLoad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DeveloperLink() {
    val context = LocalContext.current
    val dhiUrl = "https://srmgroup.dhi-edu.com/srmgroup_srmeec/"

    // State to hold server status and color
    val serverStatus = remember { mutableStateOf("Loading...") }  // Default status
    val serverStatusColor =
        remember { mutableStateOf(Color.Gray) }  // Default color for "Loading..."

    // Use LaunchedEffect to run the server check asynchronously
    LaunchedEffect(dhiUrl) {
        // Run the server status check in a background thread
        launch {
            // Perform the server check asynchronously
            val (status, color) = withContext(Dispatchers.IO) {
                checkServerLoad(dhiUrl) // This returns a Pair<String, Color>
            }

            // Update the server status and color once the check completes
            serverStatus.value = "       " + status
            serverStatusColor.value = color
        }
    }

    // Column to arrange the texts vertically
    TextButton(
        onClick = {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/j_.a_.r_.v_.i_.s/"))
            context.startActivity(intent)
        },
        colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f)),
        modifier = Modifier.background(Color.Transparent) // Set transparent background
    ) {
        Column {
            Text(
                text = "Crafted By JLabs",
                fontSize = 15.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold
            )

            // Display the server status below with dynamic color
            Text(
                text = serverStatus.value,  // Display the dynamically fetched server status
                fontSize = 12.sp,      // Smaller font size for ping-like status
                color = serverStatusColor.value,  // Apply dynamic color
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}
