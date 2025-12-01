package com.hd.eecfate.process.gpacalc

import kotlin.math.round

/**
 * Calculates the Grade Point Average (GPA) for a list of courses.
 * 
 * The GPA is calculated as a weighted average:
 * GPA = sum(grade_points × credits) / sum(credits)
 * 
 * @param courses List of courses with subject, credits, and grade
 * @return The calculated GPA rounded to 2 decimal places, or 0.0 for edge cases
 * 
 * Edge cases handled:
 * - Empty list: returns 0.0
 * - Zero total credits: returns 0.0
 * - Invalid grades: treated as 0.0 points via GradePointMapper
 */
fun calculateGPA(courses: List<Course>): Double {
    // Handle edge case: empty list
    if (courses.isEmpty()) return 0.0
    
    // Calculate total credits
    val totalCredits = courses.sumOf { it.credits }
    
    // Handle edge case: zero total credits
    if (totalCredits == 0) return 0.0

    // Calculate weighted sum of grade points
    val totalPoints = courses.sumOf { course ->
        GradePointMapper.getPoints(course.grade) * course.credits
    }

    // Calculate GPA and round to 2 decimal places
    val gpa = totalPoints / totalCredits
    return round(gpa * 100) / 100
}