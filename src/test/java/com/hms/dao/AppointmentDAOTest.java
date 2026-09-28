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

import com.hms.entity.Appointment;

class AppointmentDAOTest {

    private AppointmentDAO appointmentDAO;
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        appointmentDAO = new AppointmentDAO(mockConnection);
    }

    @Test
    void testConstructor_shouldCreateAppointmentDAO() {
        // Assert
        assertNotNull(appointmentDAO);
    }

    @Test
    void testAddAppointment_withValidAppointment_shouldReturnTrue() throws Exception {
        // Arrange
        Appointment appointment = new Appointment(1, "John Doe", "Male", "30", 
            "2024-01-15", "john@example.com", "1234567890", "Fever", 
            5, "123 Main St", "Pending");
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Act
        boolean result = appointmentDAO.addAppointment(appointment);
        
        // Assert
        assertTrue(result);
    }

    @Test
    void testAddAppointment_withNullAppointment_shouldHandleGracefully() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            appointmentDAO.addAppointment(null);
        });
    }

    @Test
    void testGetAllAppointmentByLoginUser_withValidUserId_shouldReturnList() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        // Act
        List<Appointment> result = appointmentDAO.getAllAppointmentByLoginUser(1);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllAppointmentByLoginDoctor_withValidDoctorId_shouldReturnList() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        // Act
        List<Appointment> result = appointmentDAO.getAllAppointmentByLoginDoctor(1);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAppointmentById_withValidId_shouldReturnAppointment() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        // Act
        Appointment result = appointmentDAO.getAppointmentById(1);
        
        // Assert
        assertNull(result);
    }

    @Test
    void testUpdateDrAppointmentCommentStatus_withValidData_shouldReturnTrue() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Act
        boolean result = appointmentDAO.updateDrAppointmentCommentStatus(1, 5, "Completed");
        
        // Assert
        assertTrue(result);
    }

    @Test
    void testGetAllAppointment_shouldReturnList() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        // Act
        List<Appointment> result = appointmentDAO.getAllAppointment();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
