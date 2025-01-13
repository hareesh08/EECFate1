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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hd.eecfate.fatereq.AppHeader

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
    var showError by remember { mutableStateOf(false) }

    // Get screen width and height to adjust responsiveness
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Dynamically adjust text size and padding based on screen width
    val isSmallScreen = screenWidth < 550.dp // Smaller screen size threshold

    // Adjust font size and paddings based on screen size
    val fontSizeTitle = if (isSmallScreen) 14.sp else 18.sp
    val fontSizeButton = if (isSmallScreen) 12.sp else 14.sp
    val fontSizeResult = if (isSmallScreen) 14.sp else 16.sp
    val paddingVertical = if (isSmallScreen) 4.dp else 8.dp
    val paddingHorizontal = if (isSmallScreen) 8.dp else 12.dp

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
                .padding(horizontal = paddingHorizontal)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "CGPA Calculator",
                fontSize = fontSizeTitle,
                color = Color.Black,
                modifier = Modifier.padding(bottom = paddingVertical)
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
                                semesters = semesters.toMutableList().apply { removeAt(index) }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(paddingVertical))

            Button(
                onClick = { semesters = semesters + Semester() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Semester", fontSize = fontSizeButton, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(paddingVertical))

            Button(
                onClick = {
                    if (semesters.any { it.credits <= 0 || it.gpa <= 0 }) {
                        showError = true
                    } else {
                        cgpa = calculateCGPA(semesters)
                        showResult = true
                        showError = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculate CGPA", fontSize = fontSizeButton, color = Color.Black)
            }

            if (showError) {
                Text(
                    text = "Please enter valid GPA and credits for all semesters.",
                    color = Color.Red,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (showResult) {
                Spacer(modifier = Modifier.height(paddingVertical))
                Text(
                    text = "Your CGPA is: ${"%.2f".format(cgpa)}",
                    fontSize = fontSizeResult,
                    color = Color.Black,
                    modifier = Modifier.padding(top = paddingVertical)
                )

                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (cgpa / 10).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                )

                Spacer(modifier = Modifier.height(paddingVertical))

                Button(
                    onClick = {
                        semesters = List(1) { Semester() }
                        cgpa = 0.0
                        showResult = false
                        showError = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset", fontSize = fontSizeButton, color = Color.Black)
                }
            }
        }
    }
}
