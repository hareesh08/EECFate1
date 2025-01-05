package com.hd.eecfate.fatereq

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun DeveloperLink() {
    val context = LocalContext.current
    TextButton(
        onClick = {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/j_.a_.r_.v_.i_.s/"))
            context.startActivity(intent)
        },
        colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
    ) {
        Text(
            text = "Crafted By JLabs",
            fontSize = 15.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.Bold
        )
    }
}
