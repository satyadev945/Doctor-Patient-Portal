package com.hms.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hms.entity.Specialist;

class SpecialistDAOTest {

    private SpecialistDAO specialistDAO;
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        specialistDAO = new SpecialistDAO(mockConnection);
    }

    @Test
    void testConstructor_shouldCreateSpecialistDAO() {
        // Assert
        assertNotNull(specialistDAO);
    }

    @Test
    void testAddSpecialist_withValidName_shouldReturnTrue() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Act
        boolean result = specialistDAO.addSpecialist("Cardiology");
        
        // Assert
        assertTrue(result);
    }

    @Test
    void testAddSpecialist_withNullName_shouldHandleGracefully() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Act
        boolean result = specialistDAO.addSpecialist(null);
        
        // Assert
        assertTrue(result);
    }

    @Test
    void testAddSpecialist_withEmptyName_shouldHandleGracefully() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Act
        boolean result = specialistDAO.addSpecialist("");
        
        // Assert
        assertTrue(result);
    }

    @Test
    void testGetAllSpecialist_shouldReturnList() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        // Act
        List<Specialist> result = specialistDAO.getAllSpecialist();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllSpecialist_withMultipleRecords_shouldReturnList() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt(1)).thenReturn(1, 2);
        when(mockResultSet.getString(2)).thenReturn("Cardiology", "Neurology");
        
        // Act
        List<Specialist> result = specialistDAO.getAllSpecialist();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllSpecialist_withEmptyResult_shouldReturnEmptyList() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        // Act
        List<Specialist> result = specialistDAO.getAllSpecialist();
        
        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
