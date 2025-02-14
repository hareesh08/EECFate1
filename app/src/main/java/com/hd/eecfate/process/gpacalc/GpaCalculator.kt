package com.hd.eecfate.process.gpacalc

// Function to calculate GPA
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