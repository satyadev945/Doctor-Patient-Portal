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

class DoctorChangePasswordTest {

    private DoctorChangePassword servlet;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new DoctorChangePassword();
    }

    @Test
    void testDoPost_withValidParameters_shouldProcessRequest() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("doctorId")).thenReturn("1");
        when(request.getParameter("newPassword")).thenReturn("newpass123");
        when(request.getParameter("oldPassword")).thenReturn("oldpass123");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(request).getParameter("doctorId");
        verify(request).getParameter("newPassword");
        verify(request).getParameter("oldPassword");
        verify(response).sendRedirect("doctor/edit_profile.jsp");
    }

    @Test
    void testDoPost_withInvalidDoctorId_shouldThrowNumberFormatException() {
        // Arrange
        when(request.getParameter("doctorId")).thenReturn("invalid");
        when(request.getParameter("newPassword")).thenReturn("newpass");
        when(request.getParameter("oldPassword")).thenReturn("oldpass");
        when(request.getSession()).thenReturn(session);
        
        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            servlet.doPost(request, response);
        });
    }

    @Test
    void testDoPost_withNullDoctorId_shouldThrowNumberFormatException() {
        // Arrange
        when(request.getParameter("doctorId")).thenReturn(null);
        when(request.getParameter("newPassword")).thenReturn("newpass");
        when(request.getParameter("oldPassword")).thenReturn("oldpass");
        when(request.getSession()).thenReturn(session);
        
        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            servlet.doPost(request, response);
        });
    }

    @Test
    void testDoPost_withEmptyPasswords_shouldProcessRequest() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("doctorId")).thenReturn("1");
        when(request.getParameter("newPassword")).thenReturn("");
        when(request.getParameter("oldPassword")).thenReturn("");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(response).sendRedirect("doctor/edit_profile.jsp");
    }
}
