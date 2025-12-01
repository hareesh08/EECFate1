package com.hd.eecfate.process.cgpacalc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hd.eecfate.fatereq.AppHeader
import com.hd.eecfate.ui.theme.LocalDimensions
import com.hd.eecfate.util.InputValidator
import com.hd.eecfate.util.ValidationResult

@Composable
fun CgpaCalculatorScreen() {
    CgpaApp()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CgpaApp() {
    var semesters by remember { mutableStateOf(List(1) { Semester() }) }
    var cgpa by remember { mutableStateOf(0.0) }
    var showResult by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Use LocalDimensions for responsive spacing
    val dimensions = LocalDimensions.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Transparent),
                title = { AppHeader() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = dimensions.paddingMedium)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "CGPA Calculator",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = dimensions.paddingMedium)
            )

            Box(
                modifier = Modifier
                    .heightIn(max = 520.dp)
                    .fillMaxWidth()
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(semesters) { index, semester ->
                        SemesterRow(
                            semester = semester,
                            onSemChange = { sem ->
                                semesters = semesters.toMutableList().apply {
                                    this[index] = this[index].copy(sem = sem)
                                }
                            },
                            onGpaChange = { gpa ->
                                semesters = semesters.toMutableList().apply {
                                    this[index] = this[index].copy(gpa = gpa)
                                }
                            },
                            onCreditsChange = { credits ->
                                semesters = semesters.toMutableList().apply {
                                    this[index] = this[index].copy(credits = credits)
                                }
                            },
                            onDelete = {
                                // Ensure minimum 1 semester
                                if (semesters.size > 1) {
                                    semesters = semesters.toMutableList().apply { removeAt(index) }
                                }
                            },
                            canDelete = semesters.size > 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensions.spacingMedium))

            Button(
                onClick = { semesters = semesters + Semester() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Add Semester",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spacingMedium))

            Button(
                onClick = {
                    // Validate all semesters before calculation
                    val errors = mutableListOf<String>()
                    semesters.forEachIndexed { index, semester ->
                        val validation = InputValidator.validateSemester(semester)
                        if (validation is ValidationResult.Invalid) {
                            errors.add("Semester ${index + 1}: ${validation.message}")
                        }
                    }

                    if (errors.isNotEmpty()) {
                        validationError = errors.joinToString("\n")
                        showResult = false
                    } else {
                        cgpa = calculateCGPA(semesters)
                        showResult = true
                        validationError = null
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Calculate CGPA",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Display validation error if present
            validationError?.let { error ->
                Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = dimensions.paddingSmall)
                )
            }

            if (showResult) {
                Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                Text(
                    text = "Your CGPA is: ${"%.2f".format(cgpa)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = dimensions.paddingMedium)
                )

                Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                LinearProgressIndicator(
                    progress = { (cgpa / 10).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                )

                Spacer(modifier = Modifier.height(dimensions.spacingMedium))

                Button(
                    onClick = {
                        semesters = List(1) { Semester() }
                        cgpa = 0.0
                        showResult = false
                        validationError = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Reset",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
