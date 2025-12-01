package com.hd.eecfate.process.gpacalc

/**
 * Maps grade strings to their corresponding grade points.
 * Provides centralized grade point mapping for GPA calculations.
 */
object GradePointMapper {
    private val gradePoints = mapOf(
        "O" to 10.0,
        "A+" to 9.0,
        "A" to 8.0,
        "B+" to 7.0,
        "B" to 6.0,
        "C" to 5.0,
        "W" to 0.0,
        "F" to 0.0,
        "Ab" to 0.0,
        "I" to 0.0,
        "*" to 0.0
    )
    
    /**
     * Returns the grade point value for a given grade.
     * @param grade The grade string (e.g., "A+", "B", "O")
     * @return The corresponding grade point value, or 0.0 if grade is not found
     */
    fun getPoints(grade: String): Double = gradePoints[grade] ?: 0.0
    
    /**
     * Returns a list of all valid grade strings.
     * @return List of all grade options
     */
    fun getAllGrades(): List<String> = gradePoints.keys.toList()
}
