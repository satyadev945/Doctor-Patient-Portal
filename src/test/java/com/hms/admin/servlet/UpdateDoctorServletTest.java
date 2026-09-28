package com.hms.admin.servlet;

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

class UpdateDoctorServletTest {

    private UpdateDoctorServlet servlet;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new UpdateDoctorServlet();
    }

    @Test
    void testDoPost_withValidDoctorData_shouldProcessRequest() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("fullName")).thenReturn("Dr. John Updated");
        when(request.getParameter("dateOfBirth")).thenReturn("1980-01-01");
        when(request.getParameter("qualification")).thenReturn("MBBS, MD");
        when(request.getParameter("specialist")).thenReturn("Cardiology");
        when(request.getParameter("email")).thenReturn("john.updated@example.com");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getParameter("password")).thenReturn("newpassword");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(request).getParameter("id");
        verify(request).getParameter("fullName");
        verify(response).sendRedirect("admin/view_doctor.jsp");
    }

    @Test
    void testDoPost_withInvalidId_shouldThrowNumberFormatException() {
        // Arrange
        when(request.getParameter("id")).thenReturn("invalid");
        when(request.getParameter("fullName")).thenReturn("Dr. John");
        when(request.getParameter("dateOfBirth")).thenReturn("1980-01-01");
        when(request.getParameter("qualification")).thenReturn("MBBS");
        when(request.getParameter("specialist")).thenReturn("Cardiology");
        when(request.getParameter("email")).thenReturn("john@example.com");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getParameter("password")).thenReturn("password");
        when(request.getSession()).thenReturn(session);
        
        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            servlet.doPost(request, response);
        });
    }

    @Test
    void testDoPost_withAllParameters_shouldRetrieveAllFields() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("id")).thenReturn("5");
        when(request.getParameter("fullName")).thenReturn("Dr. Jane Smith");
        when(request.getParameter("dateOfBirth")).thenReturn("1985-05-15");
        when(request.getParameter("qualification")).thenReturn("MD");
        when(request.getParameter("specialist")).thenReturn("Neurology");
        when(request.getParameter("email")).thenReturn("jane@example.com");
        when(request.getParameter("phone")).thenReturn("9876543210");
        when(request.getParameter("password")).thenReturn("securepass");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(request).getParameter("id");
        verify(request).getParameter("fullName");
        verify(request).getParameter("dateOfBirth");
        verify(request).getParameter("qualification");
        verify(request).getParameter("specialist");
        verify(request).getParameter("email");
        verify(request).getParameter("phone");
        verify(request).getParameter("password");
    }

    @Test
    void testDoPost_withNullId_shouldThrowNumberFormatException() {
        // Arrange
        when(request.getParameter("id")).thenReturn(null);
        when(request.getSession()).thenReturn(session);
        
        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            servlet.doPost(request, response);
        });
    }
}
