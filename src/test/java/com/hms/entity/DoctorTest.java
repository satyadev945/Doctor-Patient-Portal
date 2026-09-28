package com.hms.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DoctorTest {

    private Doctor doctor;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
    }

    @Test
    void testDefaultConstructor_shouldCreateEmptyDoctor() {
        // Assert
        assertNotNull(doctor);
    }

    @Test
    void testConstructorWithoutId_shouldSetAllFieldsExceptId() {
        // Arrange & Act
        Doctor doc = new Doctor("Dr. John Doe", "1980-01-01", "MBBS", 
            "Cardiology", "john@example.com", "1234567890", "password123");
        
        // Assert
        assertEquals("Dr. John Doe", doc.getFullName());
        assertEquals("1980-01-01", doc.getDateOfBirth());
        assertEquals("MBBS", doc.getQualification());
        assertEquals("Cardiology", doc.getSpecialist());
        assertEquals("john@example.com", doc.getEmail());
        assertEquals("1234567890", doc.getPhone());
        assertEquals("password123", doc.getPassword());
    }

    @Test
    void testConstructorWithId_shouldSetAllFields() {
        // Arrange & Act
        Doctor doc = new Doctor(1, "Dr. Jane Smith", "1985-05-15", "MD", 
            "Neurology", "jane@example.com", "9876543210", "securepass");
        
        // Assert
        assertEquals(1, doc.getId());
        assertEquals("Dr. Jane Smith", doc.getFullName());
        assertEquals("1985-05-15", doc.getDateOfBirth());
        assertEquals("MD", doc.getQualification());
        assertEquals("Neurology", doc.getSpecialist());
        assertEquals("jane@example.com", doc.getEmail());
        assertEquals("9876543210", doc.getPhone());
        assertEquals("securepass", doc.getPassword());
    }

    @Test
    void testSettersAndGetters_shouldWorkCorrectly() {
        // Act
        doctor.setId(5);
        doctor.setFullName("Dr. Test Doctor");
        doctor.setDateOfBirth("1990-12-25");
        doctor.setQualification("MBBS, MD");
        doctor.setSpecialist("Orthopedics");
        doctor.setEmail("test@example.com");
        doctor.setPhone("5555555555");
        doctor.setPassword("testpass");
        
        // Assert
        assertEquals(5, doctor.getId());
        assertEquals("Dr. Test Doctor", doctor.getFullName());
        assertEquals("1990-12-25", doctor.getDateOfBirth());
        assertEquals("MBBS, MD", doctor.getQualification());
        assertEquals("Orthopedics", doctor.getSpecialist());
        assertEquals("test@example.com", doctor.getEmail());
        assertEquals("5555555555", doctor.getPhone());
        assertEquals("testpass", doctor.getPassword());
    }

    @Test
    void testSetId_withNegativeValue_shouldAccept() {
        // Act
        doctor.setId(-1);
        
        // Assert
        assertEquals(-1, doctor.getId());
    }

    @Test
    void testSetFullName_withNull_shouldAccept() {
        // Act
        doctor.setFullName(null);
        
        // Assert
        assertNull(doctor.getFullName());
    }

    @Test
    void testSetEmail_withEmptyString_shouldAccept() {
        // Act
        doctor.setEmail("");
        
        // Assert
        assertEquals("", doctor.getEmail());
    }

    @Test
    void testSetPassword_withNull_shouldAccept() {
        // Act
        doctor.setPassword(null);
        
        // Assert
        assertNull(doctor.getPassword());
    }

    @Test
    void testSetPhone_withSpecialCharacters_shouldAccept() {
        // Act
        doctor.setPhone("+1-234-567-8900");
        
        // Assert
        assertEquals("+1-234-567-8900", doctor.getPhone());
    }
}
