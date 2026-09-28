package com.hms.doctor.servlet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DoctorEditProfileServletTest {

    private DoctorEditProfileServlet servlet;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new DoctorEditProfileServlet();
    }

    @Test
    void testDoPost_withValidDoctorData_shouldProcessRequest() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("doctorId")).thenReturn("1");
        when(request.getParameter("fullName")).thenReturn("Dr. John Updated");
        when(request.getParameter("dateOfBirth")).thenReturn("1980-01-01");
        when(request.getParameter("qualification")).thenReturn("MBBS, MD");
        when(request.getParameter("specialist")).thenReturn("Cardiology");
        when(request.getParameter("email")).thenReturn("john@example.com");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(request).getParameter("doctorId");
        verify(request).getParameter("fullName");
        verify(response).sendRedirect("doctor/edit_profile.jsp");
    }

    @Test
    void testDoPost_withInvalidDoctorId_shouldThrowNumberFormatException() {
        // Arrange
        when(request.getParameter("doctorId")).thenReturn("invalid");
        when(request.getParameter("fullName")).thenReturn("Dr. John");
        when(request.getParameter("dateOfBirth")).thenReturn("1980-01-01");
        when(request.getParameter("qualification")).thenReturn("MBBS");
        when(request.getParameter("specialist")).thenReturn("Cardiology");
        when(request.getParameter("email")).thenReturn("john@example.com");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getSession()).thenReturn(session);
        
        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            servlet.doPost(request, response);
        });
    }

    @Test
    void testDoPost_withAllParameters_shouldRetrieveAllFields() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("doctorId")).thenReturn("5");
        when(request.getParameter("fullName")).thenReturn("Dr. Jane Smith");
        when(request.getParameter("dateOfBirth")).thenReturn("1985-05-15");
        when(request.getParameter("qualification")).thenReturn("MD");
        when(request.getParameter("specialist")).thenReturn("Neurology");
        when(request.getParameter("email")).thenReturn("jane@example.com");
        when(request.getParameter("phone")).thenReturn("9876543210");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(request).getParameter("doctorId");
        verify(request).getParameter("fullName");
        verify(request).getParameter("dateOfBirth");
        verify(request).getParameter("qualification");
        verify(request).getParameter("specialist");
        verify(request).getParameter("email");
        verify(request).getParameter("phone");
    }

    @Test
    void testDoPost_withNullDoctorId_shouldThrowNumberFormatException() {
        // Arrange
        when(request.getParameter("doctorId")).thenReturn(null);
        when(request.getSession()).thenReturn(session);
        
        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            servlet.doPost(request, response);
        });
    }
}
