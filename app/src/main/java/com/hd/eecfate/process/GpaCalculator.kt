package com.hd.eecfate.process

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import com.hd.eecfate.fatereq.AppHeader
import com.hd.eecfate.ui.theme.EECFateTheme

class GpaCalculator : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EECFateTheme {
                GPAApp()
            }
        }
    }

    private fun enableEdgeToEdge() {
        // For Android 11 (API 30) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
            windowInsetsController.isAppearanceLightStatusBars = true // Light status bar icons
            windowInsetsController.isAppearanceLightNavigationBars =
                true // Light navigation bar icons
            window.statusBarColor = Color.Transparent.toArgb() // Transparent status bar
            window.navigationBarColor = Color.Transparent.toArgb() // Transparent navigation bar
        } else {
            // For versions below Android 11
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    )
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
        }
    }
}


@Composable
fun AppHeader() {
    Text(
        text = "GPA Calculator",
        fontSize = 20.sp,
        color = Color.Black,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun GradeDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    grades: List<String>,
    onGradeSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White) // Set background color for the dropdown
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier
                .background(Color.White) // Set background color for the dropdown menu
        ) {
            grades.forEach { grade ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = grade,
                            fontSize = 12.sp,
                            color = Color.Black // Set text color to black for visibility
                        )
                    },
                    onClick = {
                        onGradeSelected(grade)
                        onDismissRequest()
                    },
                    modifier = Modifier.background(Color.White) // Set background color for each item
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GPAApp() {
    var courses by remember { mutableStateOf(List(3) { Course() }) } // Start with fewer courses
    var gpa by remember { mutableStateOf(0.0) }
    var showResult by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

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
                .padding(horizontal = 12.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Make the entire content scrollable
        ) {
            Text(
                text = "GPA Calculator",
                fontSize = 18.sp,
                color = Color.Black, // Set text color to black
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // LazyColumn with fixed height
            Box(
                modifier = Modifier
                    .heightIn(max = 520.dp) // Limit the height of the LazyColumn
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(courses) { index, course ->
                        CourseRow(
                            course = course,
                            onSubjectChange = { subject ->
                                courses = courses.toMutableList().apply {
                                    this[index] = this[index].copy(subject = subject)
                                }
                            },
                            onCreditsChange = { credits ->
                                courses = courses.toMutableList().apply {
                                    this[index] = this[index].copy(credits = credits)
                                }
                            },
                            onGradeChange = { grade ->
                                courses = courses.toMutableList().apply {
                                    this[index] = this[index].copy(grade = grade)
                                }
                            },
                            onDelete = {
                                courses = courses.toMutableList().apply { removeAt(index) }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add Course Button
            Button(
                onClick = { courses = courses + Course() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Add Course",
                    fontSize = 12.sp,
                    color = Color.Black
                ) // Reduced font size and set text color to black
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calculate GPA Button
            Button(
                onClick = {
                    if (courses.any { it.credits <= 0 }) {
                        showError = true
                    } else {
                        gpa = calculateGPA(courses)
                        showResult = true
                        showError = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Calculate GPA",
                    fontSize = 12.sp,
                    color = Color.Black
                ) // Reduced font size and set text color to black
            }

            // Error Message
            if (showError) {
                Text(
                    text = "Please enter valid credits for all courses.",
                    color = Color.Red, // Error message in red
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // GPA Result
            if (showResult) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your GPA is: ${"%.2f".format(gpa)}",
                    fontSize = 16.sp,
                    color = Color.Black, // Set text color to black
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (gpa / 10).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Reset Button
                Button(
                    onClick = {
                        courses = List(3) { Course() } // Reset to fewer courses
                        gpa = 0.0
                        showResult = false
                        showError = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Reset",
                        fontSize = 12.sp,
                        color = Color.Black
                    ) // Reduced font size and set text color to black
                }
            }
        }
    }
}

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
    var isGradeExpanded by remember { mutableStateOf(false) } // For dropdown menu
    val grades =
        listOf("O", "A+", "A", "B+", "B", "C", "W", "F", "Ab", "I", "*") // Predefined grades

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp) // Reduce vertical padding
    ) {
        // Subject TextField
        OutlinedTextField(
            value = subject,
            onValueChange = {
                subject = it
                onSubjectChange(it)
            },
            label = { Text("Subject", color = Color.Black) }, // Set text color to black
            modifier = Modifier.weight(2f),
            singleLine = true, // Ensure text fits in one line
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
            label = { Text("Credits", color = Color.Black) }, // Set text color to black
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            singleLine = true, // Ensure text fits in one line
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Grade Dropdown Menu
        Box(modifier = Modifier.weight(1f)) {
            Column {
                // Display the selected grade on top of the dropdown button
                Text(
                    text = "Grade: $grade",
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 0.dp)
                )

                // Rectangle-shaped dropdown button
                OutlinedButton(
                    onClick = { isGradeExpanded = true },
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.small, // Use a small rectangle shape
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White, // Background color
                        contentColor = Color.Black // Text color
                    )
                ) {
                    Text(
                        "Select Grade",
                        fontSize = 10.sp,
                        color = Color.Black
                    ) // Set text color to black
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
                tint = Color.Black // Set icon color to black
            )
        }
    }
}

data class Course(
    val subject: String = "",
    val credits: Int = 0,
    val grade: String = "O"
)

fun calculateGPA(courses: List<Course>): Double {
    val gradeToPoints = mapOf(
        "O" to 10.0,
        "A+" to 9.0,
        "A" to 8.0,
        "B+" to 7.0,
        "B" to 6.0,
        "C" to 5.5,
        "W" to 0.0,
        "F" to 0.0,
        "Ab" to 0.0,
        "I" to 0.0,
        "*" to 0.0
    )

    val totalCredits = courses.sumOf { it.credits }
    if (totalCredits == 0) return 0.0

    val totalPoints = courses.sumOf { course ->
        gradeToPoints[course.grade]!! * course.credits
    }

    return totalPoints / totalCredits
}