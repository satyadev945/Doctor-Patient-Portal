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

class UserLoginServletTest {

    private UserLoginServlet servlet;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new UserLoginServlet();
    }

    @Test
    void testDoPost_withValidCredentials_shouldProcessRequest() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("email")).thenReturn("user@example.com");
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
        verify(response).sendRedirect("user_login.jsp");
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
        when(request.getParameter("email")).thenReturn("user@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(request).getSession();
    }

    @Test
    void testDoPost_withInvalidCredentials_shouldRedirectToLoginPage() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("email")).thenReturn("wrong@example.com");
        when(request.getParameter("password")).thenReturn("wrongpass");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(response).sendRedirect(anyString());
    }
}
