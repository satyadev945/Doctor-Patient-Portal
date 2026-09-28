package com.hms.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hms.entity.User;

class UserDAOTest {

    private UserDAO userDAO;
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        userDAO = new UserDAO(mockConnection);
    }

    @Test
    void testConstructor_shouldCreateUserDAO() {
        // Assert
        assertNotNull(userDAO);
    }

    @Test
    void testUserRegister_withValidUser_shouldReturnTrue() throws Exception {
        // Arrange
        User user = new User("John Doe", "john@example.com", "password123");
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Act
        boolean result = userDAO.userRegister(user);
        
        // Assert
        assertTrue(result);
    }

    @Test
    void testUserRegister_withNullUser_shouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            userDAO.userRegister(null);
        });
    }

    @Test
    void testLoginUser_withValidCredentials_shouldReturnUser() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("full_name")).thenReturn("John Doe");
        when(mockResultSet.getString("email")).thenReturn("john@example.com");
        when(mockResultSet.getString("password")).thenReturn("password123");
        
        // Act
        User result = userDAO.loginUser("john@example.com", "password123");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John Doe", result.getFullName());
    }

    @Test
    void testLoginUser_withInvalidCredentials_shouldReturnNull() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        // Act
        User result = userDAO.loginUser("wrong@example.com", "wrongpass");
        
        // Assert
        assertNull(result);
    }

    @Test
    void testCheckOldPassword_withValidPassword_shouldReturnTrue() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        
        // Act
        boolean result = userDAO.checkOldPassword(1, "oldpass");
        
        // Assert
        assertTrue(result);
    }

    @Test
    void testCheckOldPassword_withInvalidPassword_shouldReturnFalse() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        // Act
        boolean result = userDAO.checkOldPassword(1, "wrongpass");
        
        // Assert
        assertFalse(result);
    }

    @Test
    void testChangePassword_withValidData_shouldReturnTrue() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Act
        boolean result = userDAO.changePassword(1, "newpass");
        
        // Assert
        assertTrue(result);
    }

    @Test
    void testChangePassword_withNullPassword_shouldHandleGracefully() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Act
        boolean result = userDAO.changePassword(1, null);
        
        // Assert
        assertTrue(result);
    }
}
