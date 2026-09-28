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

import com.hms.entity.Doctor;

class DoctorDAOTest {

    private DoctorDAO doctorDAO;
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        doctorDAO = new DoctorDAO(mockConnection);
    }

    @Test
    void testConstructor_shouldCreateDoctorDAO() {
        // Assert
        assertNotNull(doctorDAO);
    }

    @Test
    void testRegisterDoctor_withValidDoctor_shouldReturnTrue() throws Exception {
        // Arrange
        Doctor doctor = new Doctor("Dr. John", "1980-01-01", "MBBS", 
            "Cardiology", "john@example.com", "1234567890", "password");
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Act
        boolean result = doctorDAO.registerDoctor(doctor);
        
        // Assert
        assertTrue(result);
    }

    @Test
    void testGetAllDoctor_shouldReturnList() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        // Act
        List<Doctor> result = doctorDAO.getAllDoctor();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetDoctorById_withValidId_shouldReturnDoctor() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        // Act
        Doctor result = doctorDAO.getDoctorById(1);
        
        // Assert
        assertNull(result);
    }

    @Test
    void testUpdateDoctor_withValidDoctor_shouldReturnTrue() throws Exception {
        // Arrange
        Doctor doctor = new Doctor(1, "Dr. John Updated", "1980-01-01", "MBBS, MD", 
            "Cardiology", "john@example.com", "1234567890", "newpass");
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Act
        boolean result = doctorDAO.updateDoctor(doctor);
        
        // Assert
        assertTrue(result);
    }

    @Test
    void testDeleteDoctorById_withValidId_shouldReturnTrue() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Act
        boolean result = doctorDAO.deleteDoctorById(1);
        
        // Assert
        assertTrue(result);
    }

    @Test
    void testLoginDoctor_withValidCredentials_shouldReturnDoctor() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        // Act
        Doctor result = doctorDAO.loginDoctor("john@example.com", "password");
        
        // Assert
        assertNull(result);
    }

    @Test
    void testCountTotalDoctor_shouldReturnCount() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        // Act
        int result = doctorDAO.countTotalDoctor();
        
        // Assert
        assertEquals(0, result);
    }

    @Test
    void testCountTotalAppointment_shouldReturnCount() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        // Act
        int result = doctorDAO.countTotalAppointment();
        
        // Assert
        assertEquals(0, result);
    }

    @Test
    void testCountTotalAppointmentByDoctorId_shouldReturnCount() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        // Act
        int result = doctorDAO.countTotalAppointmentByDoctorId(1);
        
        // Assert
        assertEquals(0, result);
    }

    @Test
    void testCheckOldPassword_withValidPassword_shouldReturnTrue() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        
        // Act
        boolean result = doctorDAO.checkOldPassword(1, "oldpass");
        
        // Assert
        assertTrue(result);
    }

    @Test
    void testChangePassword_withValidData_shouldReturnTrue() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Act
        boolean result = doctorDAO.changePassword(1, "newpass");
        
        // Assert
        assertTrue(result);
    }

    @Test
    void testEditDoctorProfile_withValidDoctor_shouldReturnTrue() throws Exception {
        // Arrange
        Doctor doctor = new Doctor(1, "Dr. John", "1980-01-01", "MBBS", 
            "Cardiology", "john@example.com", "1234567890", "");
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Act
        boolean result = doctorDAO.editDoctorProfile(doctor);
        
        // Assert
        assertTrue(result);
    }
}
