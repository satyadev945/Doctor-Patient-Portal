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

class ChangePasswordServletTest {

    private ChangePasswordServlet servlet;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new ChangePasswordServlet();
    }

    @Test
    void testDoPost_withValidParameters_shouldProcessRequest() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("oldPassword")).thenReturn("oldpass123");
        when(request.getParameter("newPassword")).thenReturn("newpass123");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(request).getParameter("userId");
        verify(request).getParameter("oldPassword");
        verify(request).getParameter("newPassword");
        verify(response).sendRedirect("change_password.jsp");
    }

    @Test
    void testDoPost_withInvalidUserId_shouldThrowNumberFormatException() {
        // Arrange
        when(request.getParameter("userId")).thenReturn("invalid");
        when(request.getParameter("oldPassword")).thenReturn("oldpass");
        when(request.getParameter("newPassword")).thenReturn("newpass");
        when(request.getSession()).thenReturn(session);
        
        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            servlet.doPost(request, response);
        });
    }

    @Test
    void testDoPost_withNullUserId_shouldThrowNumberFormatException() {
        // Arrange
        when(request.getParameter("userId")).thenReturn(null);
        when(request.getParameter("oldPassword")).thenReturn("oldpass");
        when(request.getParameter("newPassword")).thenReturn("newpass");
        when(request.getSession()).thenReturn(session);
        
        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            servlet.doPost(request, response);
        });
    }

    @Test
    void testDoPost_withEmptyPasswords_shouldProcessRequest() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("oldPassword")).thenReturn("");
        when(request.getParameter("newPassword")).thenReturn("");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(response).sendRedirect("change_password.jsp");
    }

    @Test
    void testDoPost_shouldRetrieveSession() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("userId")).thenReturn("1");
        when(request.getParameter("oldPassword")).thenReturn("oldpass");
        when(request.getParameter("newPassword")).thenReturn("newpass");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(request).getSession();
    }
}
