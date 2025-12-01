package com.hd.eecfate.process.cgpacalc

import kotlin.math.round

// Function to calculate CGPA
fun calculateCGPA(semesters: List<Semester>): Double {
    // Handle edge case: empty list
    if (semesters.isEmpty()) return 0.0
    
    // Handle edge case: zero total credits
    val totalCredits = semesters.sumOf { it.credits }
    if (totalCredits == 0) return 0.0

    // Calculate weighted average: sum(gpa × credits) / sum(credits)
    val totalWeightedGPA = semesters.sumOf { it.gpa * it.credits }
    val cgpa = totalWeightedGPA / totalCredits
    
    // Round result to 2 decimal places
    return round(cgpa * 100) / 100
}