package com.hd.eecfate

import com.hd.eecfate.process.cgpacalc.Semester
import com.hd.eecfate.process.gpacalc.Course
import com.hd.eecfate.util.InputValidator
import com.hd.eecfate.util.ValidationResult
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for InputValidator
 */
class InputValidatorTest {
    
    @Test
    fun validateCredits_positiveValue_returnsValid() {
        val result = InputValidator.validateCredits(3)
        assertTrue(result is ValidationResult.Valid)
    }
    
    @Test
    fun validateCredits_zeroValue_returnsInvalid() {
        val result = InputValidator.validateCredits(0)
        assertTrue(result is ValidationResult.Invalid)
    }
    
    @Test
    fun validateCredits_negativeValue_returnsInvalid() {
        val result = InputValidator.validateCredits(-5)
        assertTrue(result is ValidationResult.Invalid)
    }
    
    @Test
    fun validateGpa_validRange_returnsValid() {
        assertTrue(InputValidator.validateGpa(0.0) is ValidationResult.Valid)
        assertTrue(InputValidator.validateGpa(5.5) is ValidationResult.Valid)
        assertTrue(InputValidator.validateGpa(10.0) is ValidationResult.Valid)
    }
    
    @Test
    fun validateGpa_belowRange_returnsInvalid() {
        val result = InputValidator.validateGpa(-0.1)
        assertTrue(result is ValidationResult.Invalid)
    }
    
    @Test
    fun validateGpa_aboveRange_returnsInvalid() {
        val result = InputValidator.validateGpa(10.1)
        assertTrue(result is ValidationResult.Invalid)
    }
    
    @Test
    fun validateCourse_validCourse_returnsValid() {
        val course = Course(subject = "Mathematics", credits = 4, grade = "A+")
        val result = InputValidator.validateCourse(course)
        assertTrue(result is ValidationResult.Valid)
    }
    
    @Test
    fun validateCourse_emptySubject_returnsInvalid() {
        val course = Course(subject = "", credits = 4, grade = "A+")
        val result = InputValidator.validateCourse(course)
        assertTrue(result is ValidationResult.Invalid)
    }
    
    @Test
    fun validateCourse_invalidCredits_returnsInvalid() {
        val course = Course(subject = "Mathematics", credits = 0, grade = "A+")
        val result = InputValidator.validateCourse(course)
        assertTrue(result is ValidationResult.Invalid)
    }
    
    @Test
    fun validateCourse_emptyGrade_returnsInvalid() {
        val course = Course(subject = "Mathematics", credits = 4, grade = "")
        val result = InputValidator.validateCourse(course)
        assertTrue(result is ValidationResult.Invalid)
    }
    
    @Test
    fun validateSemester_validSemester_returnsValid() {
        val semester = Semester(sem = "Semester 1", gpa = 8.5, credits = 20)
        val result = InputValidator.validateSemester(semester)
        assertTrue(result is ValidationResult.Valid)
    }
    
    @Test
    fun validateSemester_emptySemesterName_returnsInvalid() {
        val semester = Semester(sem = "", gpa = 8.5, credits = 20)
        val result = InputValidator.validateSemester(semester)
        assertTrue(result is ValidationResult.Invalid)
    }
    
    @Test
    fun validateSemester_invalidGpa_returnsInvalid() {
        val semester = Semester(sem = "Semester 1", gpa = 11.0, credits = 20)
        val result = InputValidator.validateSemester(semester)
        assertTrue(result is ValidationResult.Invalid)
    }
    
    @Test
    fun validateSemester_invalidCredits_returnsInvalid() {
        val semester = Semester(sem = "Semester 1", gpa = 8.5, credits = -5)
        val result = InputValidator.validateSemester(semester)
        assertTrue(result is ValidationResult.Invalid)
    }
}
