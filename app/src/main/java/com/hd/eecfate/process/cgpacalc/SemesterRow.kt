package com.hd.eecfate.process.cgpacalc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hd.eecfate.ui.theme.LocalDimensions
import com.hd.eecfate.util.InputValidator
import com.hd.eecfate.util.ValidationResult

@Composable
fun SemesterRow(
    semester: Semester,
    onSemChange: (String) -> Unit,
    onGpaChange: (Double) -> Unit,
    onCreditsChange: (Int) -> Unit,
    onDelete: () -> Unit,
    canDelete: Boolean = true
) {
    var sem by remember { mutableStateOf(semester.sem) }
    var gpa by remember { mutableStateOf(if (semester.gpa == 0.0) "" else semester.gpa.toString()) }
    var credits by remember { mutableStateOf(if (semester.credits == 0) "" else semester.credits.toString()) }
    
    // Validation states
    var gpaError by remember { mutableStateOf<String?>(null) }
    var creditsError by remember { mutableStateOf<String?>(null) }

    // Use LocalDimensions for responsive spacing
    val dimensions = LocalDimensions.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.paddingSmall)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Semester TextField
            OutlinedTextField(
                value = sem,
                onValueChange = {
                    sem = it
                    onSemChange(it)
                },
                label = { Text("Semester") },
                modifier = Modifier.weight(2f),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors()
            )

            Spacer(modifier = Modifier.width(dimensions.spacingSmall))

            // GPA TextField with validation
            OutlinedTextField(
                value = gpa,
                onValueChange = {
                    gpa = it
                    val gpaValue = it.toDoubleOrNull() ?: 0.0
                    onGpaChange(gpaValue)
                    
                    // Validate GPA
                    if (it.isNotEmpty()) {
                        val validation = InputValidator.validateGpa(gpaValue)
                        gpaError = if (validation is ValidationResult.Invalid) {
                            validation.message
                        } else {
                            null
                        }
                    } else {
                        gpaError = null
                    }
                },
                label = { Text("GPA") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
                singleLine = true,
                isError = gpaError != null,
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors()
            )

            Spacer(modifier = Modifier.width(dimensions.spacingSmall))

            // Credits TextField with validation
            OutlinedTextField(
                value = credits,
                onValueChange = {
                    credits = it
                    val creditsValue = it.toIntOrNull() ?: 0
                    onCreditsChange(creditsValue)
                    
                    // Validate credits
                    if (it.isNotEmpty()) {
                        val validation = InputValidator.validateCredits(creditsValue)
                        creditsError = if (validation is ValidationResult.Invalid) {
                            validation.message
                        } else {
                            null
                        }
                    } else {
                        creditsError = null
                    }
                },
                label = { Text("Credits") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true,
                isError = creditsError != null,
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors()
            )

            Spacer(modifier = Modifier.width(dimensions.spacingSmall))

            // Delete Button with minimum touch target
            IconButton(
                onClick = onDelete,
                enabled = canDelete,
                modifier = Modifier.size(dimensions.minTouchTarget)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Semester",
                    tint = if (canDelete) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }

        // Display inline validation errors
        if (gpaError != null || creditsError != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = dimensions.paddingSmall, top = dimensions.paddingSmall)
            ) {
                // Spacer for semester field
                Spacer(modifier = Modifier.weight(2f))
                Spacer(modifier = Modifier.width(dimensions.spacingSmall))
                
                // GPA error
                if (gpaError != null) {
                    Text(
                        text = gpaError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                
                Spacer(modifier = Modifier.width(dimensions.spacingSmall))
                
                // Credits error
                if (creditsError != null) {
                    Text(
                        text = creditsError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                
                // Spacer for delete button
                Spacer(modifier = Modifier.width(dimensions.spacingSmall))
                Spacer(modifier = Modifier.size(dimensions.minTouchTarget))
            }
        }
    }
}
