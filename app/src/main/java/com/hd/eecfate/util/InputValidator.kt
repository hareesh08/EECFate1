package com.hd.eecfate.util

import com.hd.eecfate.process.gpacalc.Course
import com.hd.eecfate.process.gpacalc.GradePointMapper
import com.hd.eecfate.process.cgpacalc.Semester

/**
 * Sealed class representing the result of a validation operation.
 */
sealed class ValidationResult {
    /**
     * Indicates that the validation passed successfully.
     */
    object Valid : ValidationResult()
    
    /**
     * Indicates that the validation failed with a specific error message.
     * @param message The error message describing why validation failed
     */
    data class Invalid(val message: String) : ValidationResult()
}

/**
 * Provides validation functions for calculator inputs.
 * Validates credits, GPA values, courses, and semesters according to application requirements.
 */
object InputValidator {
    
    /**
     * Validates that credits are positive integers.
     * Requirements: 4.3, 5.4
     * 
     * @param credits The number of credits to validate
     * @return ValidationResult.Valid if credits > 0, ValidationResult.Invalid otherwise
     */
    fun validateCredits(credits: Int): ValidationResult {
        return if (credits > 0) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("Credits must be a positive integer")
        }
    }
    
    /**
     * Validates that GPA is within the valid range of 0 to 10.
     * Requirements: 5.3
     * 
     * @param gpa The GPA value to validate
     * @return ValidationResult.Valid if 0 <= gpa <= 10, ValidationResult.Invalid otherwise
     */
    fun validateGpa(gpa: Double): ValidationResult {
        return if (gpa in 0.0..10.0) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("GPA must be between 0 and 10")
        }
    }
    
    /**
     * Validates a complete course entry.
     * Checks that the subject is not empty, credits are positive, and grade is valid.
     * Requirements: 4.3
     * 
     * @param course The course to validate
     * @return ValidationResult.Valid if all fields are valid, ValidationResult.Invalid otherwise
     */
    fun validateCourse(course: Course): ValidationResult {
        // Check if subject is empty
        if (course.subject.isBlank()) {
            return ValidationResult.Invalid("Subject name cannot be empty")
        }
        
        // Validate credits
        val creditsValidation = validateCredits(course.credits)
        if (creditsValidation is ValidationResult.Invalid) {
            return creditsValidation
        }
        
        // Check if grade is valid
        if (course.grade.isBlank()) {
            return ValidationResult.Invalid("Grade must be selected")
        }
        
        if (!GradePointMapper.getAllGrades().contains(course.grade)) {
            return ValidationResult.Invalid("Invalid grade selected")
        }
        
        return ValidationResult.Valid
    }
    
    /**
     * Validates a complete semester entry.
     * Checks that the semester name is not empty, GPA is in valid range, and credits are positive.
     * Requirements: 5.3, 5.4
     * 
     * @param semester The semester to validate
     * @return ValidationResult.Valid if all fields are valid, ValidationResult.Invalid otherwise
     */
    fun validateSemester(semester: Semester): ValidationResult {
        // Check if semester name is empty
        if (semester.sem.isBlank()) {
            return ValidationResult.Invalid("Semester name cannot be empty")
        }
        
        // Validate GPA
        val gpaValidation = validateGpa(semester.gpa)
        if (gpaValidation is ValidationResult.Invalid) {
            return gpaValidation
        }
        
        // Validate credits
        val creditsValidation = validateCredits(semester.credits)
        if (creditsValidation is ValidationResult.Invalid) {
            return creditsValidation
        }
        
        return ValidationResult.Valid
    }
}
