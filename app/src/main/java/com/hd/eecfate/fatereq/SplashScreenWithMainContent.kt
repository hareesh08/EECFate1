package com.hd.eecfate.fatereq

import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hd.eecfate.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreenWithMainContent() {
    var showSplash by remember { mutableStateOf(true) }
    val scale = remember { androidx.compose.animation.core.Animatable(0.5f) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Toast.makeText(context, "Developed By Hareesh", Toast.LENGTH_SHORT).show()
        scale.animateTo(1f, animationSpec = tween(durationMillis = 1000))
        delay(2000)
        showSplash = false
    }

    if (showSplash) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF6A11CB), Color(0xFF2575FC)))),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_refresh),
                    contentDescription = "App Launcher Icon",
                    modifier = Modifier
                        .size(200.dp)
                        .scale(scale.value)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(25.dp))
                Text(
                    text = "Made With Hate",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    } else {
        MainScreen("https://srmgroup.dhi-edu.com/srmgroup_srmeec/#/student/dashboard")
    }
}
