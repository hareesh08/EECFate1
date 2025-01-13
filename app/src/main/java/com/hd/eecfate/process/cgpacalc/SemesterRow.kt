package com.hd.eecfate.process.cgpacalc

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SemesterRow(
    semester: Semester,
    onSemChange: (String) -> Unit,
    onGpaChange: (Double) -> Unit,
    onCreditsChange: (Int) -> Unit,
    onDelete: () -> Unit
) {
    var sem by remember { mutableStateOf(semester.sem) }
    var gpa by remember { mutableStateOf(semester.gpa.toString()) }
    var credits by remember { mutableStateOf(semester.credits.toString()) }

    // Get screen width to adjust for responsiveness
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Dynamically adjust font size and spacing based on screen width
    val isSmallScreen = screenWidth < 650.dp
    val fontSizeLabel = if (isSmallScreen) 10.sp else 12.sp
    val paddingHorizontal = if (isSmallScreen) 4.dp else 8.dp
    val paddingVertical = if (isSmallScreen) 2.dp else 4.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = paddingVertical)
    ) {
        // Semester TextField
        OutlinedTextField(
            value = sem,
            onValueChange = {
                sem = it
                onSemChange(it)
            },
            label = { Text("Semester", color = Color.Black, fontSize = fontSizeLabel) },
            modifier = Modifier.weight(2f),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.width(paddingHorizontal))

        // GPA TextField
        OutlinedTextField(
            value = gpa,
            onValueChange = {
                gpa = it
                onGpaChange(it.toDoubleOrNull() ?: 0.0)
            },
            label = { Text("GPA", color = Color.Black, fontSize = fontSizeLabel) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.width(paddingHorizontal))

        // Credits TextField
        OutlinedTextField(
            value = credits,
            onValueChange = {
                credits = it
                onCreditsChange(it.toIntOrNull() ?: 0)
            },
            label = { Text("Total Credits", color = Color.Black, fontSize = fontSizeLabel) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.width(paddingHorizontal))

        // Delete Button
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Semester",
                tint = Color.Black
            )
        }
    }
}
