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

class DoctorLoginServletTest {

    private DoctorLoginServlet servlet;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new DoctorLoginServlet();
    }

    @Test
    void testDoPost_withValidCredentials_shouldProcessRequest() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("email")).thenReturn("doctor@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(request).getParameter("email");
        verify(request).getParameter("password");
        verify(response).sendRedirect(anyString());
    }

    @Test
    void testDoPost_withNullEmail_shouldHandleGracefully() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("email")).thenReturn(null);
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(session).setAttribute(eq("errorMsg"), anyString());
        verify(response).sendRedirect("doctor_login.jsp");
    }

    @Test
    void testDoPost_withEmptyCredentials_shouldHandleGracefully() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("email")).thenReturn("");
        when(request.getParameter("password")).thenReturn("");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(response).sendRedirect(anyString());
    }

    @Test
    void testDoPost_shouldRetrieveSession() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("email")).thenReturn("doctor@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(request).getSession();
    }
}
