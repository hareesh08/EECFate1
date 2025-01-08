package com.hd.eecfate.process.gpacalc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CourseRow(
    course: Course,
    onSubjectChange: (String) -> Unit,
    onCreditsChange: (Int) -> Unit,
    onGradeChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    var subject by remember { mutableStateOf(course.subject) }
    var credits by remember { mutableStateOf(course.credits.toString()) }
    var grade by remember { mutableStateOf(course.grade) }
    var isGradeExpanded by remember { mutableStateOf(false) }
    val grades = listOf("O", "A+", "A", "B+", "B", "C", "W", "F", "Ab", "I", "*")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Subject TextField
        OutlinedTextField(
            value = subject,
            onValueChange = {
                subject = it
                onSubjectChange(it)
            },
            label = { Text("Subject", color = Color.Black) },
            modifier = Modifier.weight(2f),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Credits TextField
        OutlinedTextField(
            value = credits,
            onValueChange = {
                credits = it
                onCreditsChange(it.toIntOrNull() ?: 0)
            },
            label = { Text("Credits", color = Color.Black) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Grade Dropdown Menu
        Box(modifier = Modifier.weight(1f)) {
            Column {
                Text(
                    text = "Grade: $grade",
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 0.dp)
                )

                OutlinedButton(
                    onClick = { isGradeExpanded = true },
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Select Grade", fontSize = 10.sp, color = Color.Black)
                }
            }

            GradeDropdownMenu(
                expanded = isGradeExpanded,
                onDismissRequest = { isGradeExpanded = false },
                grades = grades,
                onGradeSelected = { selectedGrade ->
                    grade = selectedGrade
                    onGradeChange(selectedGrade)
                }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Delete Button
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Course",
                tint = Color.Black
            )
        }
    }
}