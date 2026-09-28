package com.hms.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpecialistTest {

    private Specialist specialist;

    @BeforeEach
    void setUp() {
        specialist = new Specialist();
    }

    @Test
    void testDefaultConstructor_shouldCreateEmptySpecialist() {
        // Assert
        assertNotNull(specialist);
    }

    @Test
    void testConstructorWithParameters_shouldSetAllFields() {
        // Arrange & Act
        Specialist spec = new Specialist(1, "Cardiology");
        
        // Assert
        assertEquals(1, spec.getId());
        assertEquals("Cardiology", spec.getSpecialistName());
    }

    @Test
    void testSettersAndGetters_shouldWorkCorrectly() {
        // Act
        specialist.setId(5);
        specialist.setSpecialistName("Neurology");
        
        // Assert
        assertEquals(5, specialist.getId());
        assertEquals("Neurology", specialist.getSpecialistName());
    }

    @Test
    void testSetId_withNegativeValue_shouldAccept() {
        // Act
        specialist.setId(-1);
        
        // Assert
        assertEquals(-1, specialist.getId());
    }

    @Test
    void testSetId_withZero_shouldAccept() {
        // Act
        specialist.setId(0);
        
        // Assert
        assertEquals(0, specialist.getId());
    }

    @Test
    void testSetSpecialistName_withNull_shouldAccept() {
        // Act
        specialist.setSpecialistName(null);
        
        // Assert
        assertNull(specialist.getSpecialistName());
    }

    @Test
    void testSetSpecialistName_withEmptyString_shouldAccept() {
        // Act
        specialist.setSpecialistName("");
        
        // Assert
        assertEquals("", specialist.getSpecialistName());
    }

    @Test
    void testSetSpecialistName_withLongName_shouldAccept() {
        // Act
        String longName = "Cardiovascular and Thoracic Surgery Specialist";
        specialist.setSpecialistName(longName);
        
        // Assert
        assertEquals(longName, specialist.getSpecialistName());
    }
}
