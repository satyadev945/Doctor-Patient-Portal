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

class UpdateStatusTest {

    private UpdateStatus servlet;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new UpdateStatus();
    }

    @Test
    void testDoPost_withValidParameters_shouldProcessRequest() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("doctorId")).thenReturn("5");
        when(request.getParameter("comment")).thenReturn("Treatment completed");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(request).getParameter("id");
        verify(request).getParameter("doctorId");
        verify(request).getParameter("comment");
        verify(response).sendRedirect("doctor/patient.jsp");
    }

    @Test
    void testDoPost_withInvalidId_shouldThrowNumberFormatException() {
        // Arrange
        when(request.getParameter("id")).thenReturn("invalid");
        when(request.getParameter("doctorId")).thenReturn("5");
        when(request.getParameter("comment")).thenReturn("Comment");
        when(request.getSession()).thenReturn(session);
        
        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            servlet.doPost(request, response);
        });
    }

    @Test
    void testDoPost_withInvalidDoctorId_shouldThrowNumberFormatException() {
        // Arrange
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("doctorId")).thenReturn("invalid");
        when(request.getParameter("comment")).thenReturn("Comment");
        when(request.getSession()).thenReturn(session);
        
        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            servlet.doPost(request, response);
        });
    }

    @Test
    void testDoPost_withNullComment_shouldProcessRequest() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("doctorId")).thenReturn("5");
        when(request.getParameter("comment")).thenReturn(null);
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(response).sendRedirect("doctor/patient.jsp");
    }

    @Test
    void testDoPost_withEmptyComment_shouldProcessRequest() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("doctorId")).thenReturn("5");
        when(request.getParameter("comment")).thenReturn("");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(response).sendRedirect("doctor/patient.jsp");
    }
}
