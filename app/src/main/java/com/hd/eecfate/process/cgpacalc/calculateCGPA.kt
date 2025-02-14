package com.hd.eecfate.process.cgpacalc

// Function to calculate CGPA
fun calculateCGPA(semesters: List<Semester>): Double {
    val totalCredits = semesters.sumOf { it.credits }
    if (totalCredits == 0) return 0.0

    val totalWeightedGPA = semesters.sumOf { it.gpa * it.credits }
    return totalWeightedGPA / totalCredits
}