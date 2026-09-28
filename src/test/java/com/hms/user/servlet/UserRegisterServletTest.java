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

class UserRegisterServletTest {

    private UserRegisterServlet servlet;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new UserRegisterServlet();
    }

    @Test
    void testDoPost_withValidUserData_shouldProcessRequest() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("fullName")).thenReturn("John Doe");
        when(request.getParameter("email")).thenReturn("john@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(request).getParameter("fullName");
        verify(request).getParameter("email");
        verify(request).getParameter("password");
        verify(response).sendRedirect("signup.jsp");
    }

    @Test
    void testDoPost_withNullFullName_shouldHandleGracefully() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("fullName")).thenReturn(null);
        when(request.getParameter("email")).thenReturn("john@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(session).setAttribute(eq("errorMsg"), anyString());
        verify(response).sendRedirect("signup.jsp");
    }

    @Test
    void testDoPost_withEmptyEmail_shouldHandleGracefully() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("fullName")).thenReturn("John Doe");
        when(request.getParameter("email")).thenReturn("");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(response).sendRedirect("signup.jsp");
    }

    @Test
    void testDoPost_withAllParameters_shouldRetrieveAllFields() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("fullName")).thenReturn("Jane Smith");
        when(request.getParameter("email")).thenReturn("jane@example.com");
        when(request.getParameter("password")).thenReturn("securepass");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(request).getParameter("fullName");
        verify(request).getParameter("email");
        verify(request).getParameter("password");
    }

    @Test
    void testDoPost_shouldRetrieveSession() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("fullName")).thenReturn("Test User");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("password")).thenReturn("testpass");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(request).getSession();
    }
}
