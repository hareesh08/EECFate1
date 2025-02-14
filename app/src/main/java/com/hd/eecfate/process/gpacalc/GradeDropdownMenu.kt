package com.hd.eecfate.process.gpacalc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GradeDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    grades: List<String>,
    onGradeSelected: (String) -> Unit
) {
    // Get screen width
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Dynamically adjust font size and padding based on screen width
    val isSmallScreen = screenWidth < 360.dp // Consider screens less than 360dp as small screens
    val fontSizeGrade = if (isSmallScreen) 10.sp else 12.sp
    val dropdownPadding = if (isSmallScreen) 4.dp else 8.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier
                .background(Color.White)
                .padding(horizontal = dropdownPadding) // Adjust padding for small screens
        ) {
            grades.forEach { grade ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = grade,
                            fontSize = fontSizeGrade,
                            color = Color.Black
                        )
                    },
                    onClick = {
                        onGradeSelected(grade)
                        onDismissRequest()
                    },
                    modifier = Modifier.background(Color.White)
                )
            }
        }
    }
}
