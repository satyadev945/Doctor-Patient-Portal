package com.hms.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testDefaultConstructor_shouldCreateEmptyUser() {
        // Assert
        assertNotNull(user);
    }

    @Test
    void testConstructorWithoutId_shouldSetAllFieldsExceptId() {
        // Arrange & Act
        User u = new User("John Doe", "john@example.com", "password123");
        
        // Assert
        assertEquals("John Doe", u.getFullName());
        assertEquals("john@example.com", u.getEmail());
        assertEquals("password123", u.getPassword());
    }

    @Test
    void testConstructorWithId_shouldSetAllFields() {
        // Arrange & Act
        User u = new User(1, "Jane Smith", "jane@example.com", "securepass");
        
        // Assert
        assertEquals(1, u.getId());
        assertEquals("Jane Smith", u.getFullName());
        assertEquals("jane@example.com", u.getEmail());
        assertEquals("securepass", u.getPassword());
    }

    @Test
    void testSettersAndGetters_shouldWorkCorrectly() {
        // Act
        user.setId(10);
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("testpass");
        
        // Assert
        assertEquals(10, user.getId());
        assertEquals("Test User", user.getFullName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testpass", user.getPassword());
    }

    @Test
    void testToString_shouldReturnFormattedString() {
        // Arrange
        user.setId(5);
        user.setFullName("Alice Brown");
        user.setEmail("alice@example.com");
        user.setPassword("alicepass");
        
        // Act
        String result = user.toString();
        
        // Assert
        assertTrue(result.contains("id=5"));
        assertTrue(result.contains("fullName=Alice Brown"));
        assertTrue(result.contains("email=alice@example.com"));
        assertTrue(result.contains("password=alicepass"));
    }

    @Test
    void testSetId_withNegativeValue_shouldAccept() {
        // Act
        user.setId(-1);
        
        // Assert
        assertEquals(-1, user.getId());
    }

    @Test
    void testSetFullName_withNull_shouldAccept() {
        // Act
        user.setFullName(null);
        
        // Assert
        assertNull(user.getFullName());
    }

    @Test
    void testSetEmail_withEmptyString_shouldAccept() {
        // Act
        user.setEmail("");
        
        // Assert
        assertEquals("", user.getEmail());
    }

    @Test
    void testSetPassword_withNull_shouldAccept() {
        // Act
        user.setPassword(null);
        
        // Assert
        assertNull(user.getPassword());
    }
}
