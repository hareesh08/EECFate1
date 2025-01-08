package com.hd.eecfate.process.gpacalc

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hd.eecfate.fatereq.AppHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GPAApp() {
    var courses by remember { mutableStateOf(List(3) { Course() }) }
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
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "GPA Calculator",
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Box(
                modifier = Modifier
                    .heightIn(max = 520.dp)
                    .fillMaxWidth()
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
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

            Button(
                onClick = { courses = courses + Course() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Course", fontSize = 12.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                Text("Calculate GPA", fontSize = 12.sp, color = Color.Black)
            }

            if (showError) {
                Text(
                    text = "Please enter valid credits for all courses.",
                    color = Color.Red,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (showResult) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your GPA is: ${"%.2f".format(gpa)}",
                    fontSize = 16.sp,
                    color = Color.Black,
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

                Button(
                    onClick = {
                        courses = List(3) { Course() }
                        gpa = 0.0
                        showResult = false
                        showError = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset", fontSize = 12.sp, color = Color.Black)
                }
            }
        }
    }
}