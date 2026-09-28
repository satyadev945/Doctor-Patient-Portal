package com.hms.user.servlet;

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

class AppointmentServletTest {

    private AppointmentServlet servlet;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new AppointmentServlet();
    }

    @Test
    void testDoPost_withValidAppointmentData_shouldProcessRequest() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("fullName")).thenReturn("John Doe");
        when(request.getParameter("gender")).thenReturn("Male");
        when(request.getParameter("age")).thenReturn("30");
        when(request.getParameter("appointmentDate")).thenReturn("2024-01-15");
        when(request.getParameter("email")).thenReturn("john@example.com");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getParameter("diseases")).thenReturn("Fever");
        when(request.getParameter("doctorNameSelect")).thenReturn("5");
        when(request.getParameter("address")).thenReturn("123 Main St");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(request).getParameter("userId");
        verify(request).getParameter("fullName");
        verify(response).sendRedirect("user_appointment.jsp");
    }

    @Test
    void testDoPost_withInvalidUserId_shouldThrowNumberFormatException() {
        // Arrange
        when(request.getParameter("userId")).thenReturn("invalid");
        when(request.getParameter("fullName")).thenReturn("John Doe");
        when(request.getParameter("gender")).thenReturn("Male");
        when(request.getParameter("age")).thenReturn("30");
        when(request.getParameter("appointmentDate")).thenReturn("2024-01-15");
        when(request.getParameter("email")).thenReturn("john@example.com");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getParameter("diseases")).thenReturn("Fever");
        when(request.getParameter("doctorNameSelect")).thenReturn("5");
        when(request.getParameter("address")).thenReturn("123 Main St");
        when(request.getSession()).thenReturn(session);
        
        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            servlet.doPost(request, response);
        });
    }

    @Test
    void testDoPost_withInvalidDoctorId_shouldThrowNumberFormatException() {
        // Arrange
        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("fullName")).thenReturn("John Doe");
        when(request.getParameter("gender")).thenReturn("Male");
        when(request.getParameter("age")).thenReturn("30");
        when(request.getParameter("appointmentDate")).thenReturn("2024-01-15");
        when(request.getParameter("email")).thenReturn("john@example.com");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getParameter("diseases")).thenReturn("Fever");
        when(request.getParameter("doctorNameSelect")).thenReturn("invalid");
        when(request.getParameter("address")).thenReturn("123 Main St");
        when(request.getSession()).thenReturn(session);
        
        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            servlet.doPost(request, response);
        });
    }

    @Test
    void testDoPost_withAllParameters_shouldRetrieveAllFields() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("fullName")).thenReturn("Jane Smith");
        when(request.getParameter("gender")).thenReturn("Female");
        when(request.getParameter("age")).thenReturn("25");
        when(request.getParameter("appointmentDate")).thenReturn("2024-02-20");
        when(request.getParameter("email")).thenReturn("jane@example.com");
        when(request.getParameter("phone")).thenReturn("9876543210");
        when(request.getParameter("diseases")).thenReturn("Cold");
        when(request.getParameter("doctorNameSelect")).thenReturn("3");
        when(request.getParameter("address")).thenReturn("456 Oak Ave");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(request).getParameter("userId");
        verify(request).getParameter("fullName");
        verify(request).getParameter("gender");
        verify(request).getParameter("age");
        verify(request).getParameter("appointmentDate");
        verify(request).getParameter("email");
        verify(request).getParameter("phone");
        verify(request).getParameter("diseases");
        verify(request).getParameter("doctorNameSelect");
        verify(request).getParameter("address");
    }
}
