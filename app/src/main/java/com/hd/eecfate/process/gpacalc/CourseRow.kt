package com.hd.eecfate.process.gpacalc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hd.eecfate.ui.theme.LocalDimensions

@Composable
fun CourseRow(
    course: Course,
    onSubjectChange: (String) -> Unit,
    onCreditsChange: (Int) -> Unit,
    onGradeChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    var subject by remember { mutableStateOf(course.subject) }
    var credits by remember { mutableStateOf(if (course.credits > 0) course.credits.toString() else "") }
    var grade by remember { mutableStateOf(course.grade) }
    var isGradeExpanded by remember { mutableStateOf(false) }
    val grades = listOf("O", "A+", "A", "B+", "B", "C", "W", "F", "Ab", "I", "*")

    // Use LocalDimensions for responsive spacing
    val dimensions = LocalDimensions.current
    
    // Get screen width for responsive layout
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Adjust field weights based on screen size
    val isSmallScreen = screenWidth < 600.dp
    val subjectWeight = if (isSmallScreen) 1.5f else 2f
    val creditsWeight = if (isSmallScreen) 0.8f else 1f
    val gradeWeight = if (isSmallScreen) 1f else 1.2f
    
    // Sync internal state with course prop when it changes
    LaunchedEffect(course) {
        subject = course.subject
        credits = if (course.credits > 0) course.credits.toString() else ""
        grade = course.grade
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.paddingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Subject TextField
        OutlinedTextField(
            value = subject,
            onValueChange = {
                subject = it
                onSubjectChange(it)
            },
            label = { 
                Text(
                    "Subject",
                    style = MaterialTheme.typography.bodySmall
                ) 
            },
            modifier = Modifier.weight(subjectWeight),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(dimensions.spacingSmall))

        // Credits TextField
        OutlinedTextField(
            value = credits,
            onValueChange = {
                credits = it
                onCreditsChange(it.toIntOrNull() ?: 0)
            },
            label = { 
                Text(
                    "Credits",
                    style = MaterialTheme.typography.bodySmall
                ) 
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(creditsWeight),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(dimensions.spacingSmall))

        // Grade Dropdown Menu
        Box(modifier = Modifier.weight(gradeWeight)) {
            Column {
                Text(
                    text = "Grade: $grade",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = dimensions.paddingSmall)
                )

                OutlinedButton(
                    onClick = { isGradeExpanded = true },
                    modifier = Modifier
                        .height(dimensions.minTouchTarget)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        "Select",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            // Grade Dropdown Menu
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

        Spacer(modifier = Modifier.width(dimensions.spacingSmall))

        // Delete Button with minimum touch target
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(dimensions.minTouchTarget)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Course",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(dimensions.iconSizeMedium)
            )
        }
    }
}

