package com.hms.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppointmentTest {

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        appointment = new Appointment();
    }

    @Test
    void testDefaultConstructor_shouldCreateEmptyAppointment() {
        // Assert
        assertNotNull(appointment);
    }

    @Test
    void testConstructorWithAllParameters_shouldSetAllFields() {
        // Arrange & Act
        Appointment app = new Appointment(1, 100, "John Doe", "Male", "30", 
            "2024-01-15", "john@example.com", "1234567890", "Fever", 
            5, "123 Main St", "Pending");
        
        // Assert
        assertEquals(1, app.getId());
        assertEquals(100, app.getUserId());
        assertEquals("John Doe", app.getFullName());
        assertEquals("Male", app.getGender());
        assertEquals("30", app.getAge());
        assertEquals("2024-01-15", app.getAppointmentDate());
        assertEquals("john@example.com", app.getEmail());
        assertEquals("1234567890", app.getPhone());
        assertEquals("Fever", app.getDiseases());
        assertEquals(5, app.getDoctorId());
        assertEquals("123 Main St", app.getAddress());
        assertEquals("Pending", app.getStatus());
    }

    @Test
    void testConstructorWithoutId_shouldSetAllFieldsExceptId() {
        // Arrange & Act
        Appointment app = new Appointment(100, "Jane Doe", "Female", "25", 
            "2024-02-20", "jane@example.com", "9876543210", "Cold", 
            3, "456 Oak Ave", "Confirmed");
        
        // Assert
        assertEquals(100, app.getUserId());
        assertEquals("Jane Doe", app.getFullName());
        assertEquals("Female", app.getGender());
        assertEquals("25", app.getAge());
        assertEquals("Confirmed", app.getStatus());
    }

    @Test
    void testSettersAndGetters_shouldWorkCorrectly() {
        // Act
        appointment.setId(10);
        appointment.setUserId(200);
        appointment.setFullName("Test User");
        appointment.setGender("Male");
        appointment.setAge("40");
        appointment.setAppointmentDate("2024-03-10");
        appointment.setEmail("test@example.com");
        appointment.setPhone("5555555555");
        appointment.setDiseases("Headache");
        appointment.setDoctorId(7);
        appointment.setAddress("789 Pine Rd");
        appointment.setStatus("Completed");
        
        // Assert
        assertEquals(10, appointment.getId());
        assertEquals(200, appointment.getUserId());
        assertEquals("Test User", appointment.getFullName());
        assertEquals("Male", appointment.getGender());
        assertEquals("40", appointment.getAge());
        assertEquals("2024-03-10", appointment.getAppointmentDate());
        assertEquals("test@example.com", appointment.getEmail());
        assertEquals("5555555555", appointment.getPhone());
        assertEquals("Headache", appointment.getDiseases());
        assertEquals(7, appointment.getDoctorId());
        assertEquals("789 Pine Rd", appointment.getAddress());
        assertEquals("Completed", appointment.getStatus());
    }

    @Test
    void testSetId_withNegativeValue_shouldAccept() {
        // Act
        appointment.setId(-1);
        
        // Assert
        assertEquals(-1, appointment.getId());
    }

    @Test
    void testSetUserId_withZero_shouldAccept() {
        // Act
        appointment.setUserId(0);
        
        // Assert
        assertEquals(0, appointment.getUserId());
    }

    @Test
    void testSetFullName_withNull_shouldAccept() {
        // Act
        appointment.setFullName(null);
        
        // Assert
        assertNull(appointment.getFullName());
    }

    @Test
    void testSetEmail_withEmptyString_shouldAccept() {
        // Act
        appointment.setEmail("");
        
        // Assert
        assertEquals("", appointment.getEmail());
    }
}
