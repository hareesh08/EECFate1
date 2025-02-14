package com.hd.eecfate.process.cgpacalc

// Data class for Semester
data class Semester(
    val sem: String = "", // Semester name (e.g., "Semester 1")
    val gpa: Double = 0.0, // GPA for the semester
    val credits: Int = 0 // Total credits for the semester
)