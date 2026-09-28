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

class AdminLoginServletTest {

    private AdminLoginServlet servlet;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new AdminLoginServlet();
    }

    @Test
    void testDoPost_withValidCredentials_shouldRedirectToAdminIndex() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("email")).thenReturn("admin@gmail.com");
        when(request.getParameter("password")).thenReturn("admin");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(session).setAttribute(eq("adminObj"), any());
        verify(response).sendRedirect("admin/index.jsp");
    }

    @Test
    void testDoPost_withInvalidEmail_shouldRedirectToLoginWithError() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("email")).thenReturn("wrong@gmail.com");
        when(request.getParameter("password")).thenReturn("admin");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(session).setAttribute("errorMsg", "Invalid Username or Password.");
        verify(response).sendRedirect("admin_login.jsp");
    }

    @Test
    void testDoPost_withInvalidPassword_shouldRedirectToLoginWithError() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("email")).thenReturn("admin@gmail.com");
        when(request.getParameter("password")).thenReturn("wrongpassword");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(session).setAttribute("errorMsg", "Invalid Username or Password.");
        verify(response).sendRedirect("admin_login.jsp");
    }

    @Test
    void testDoPost_withNullEmail_shouldRedirectToLoginWithError() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("email")).thenReturn(null);
        when(request.getParameter("password")).thenReturn("admin");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(session).setAttribute("errorMsg", "Invalid Username or Password.");
        verify(response).sendRedirect("admin_login.jsp");
    }

    @Test
    void testDoPost_withEmptyCredentials_shouldRedirectToLoginWithError() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("email")).thenReturn("");
        when(request.getParameter("password")).thenReturn("");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(session).setAttribute("errorMsg", "Invalid Username or Password.");
        verify(response).sendRedirect("admin_login.jsp");
    }
}
