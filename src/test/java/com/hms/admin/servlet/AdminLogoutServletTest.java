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

class AdminLogoutServletTest {

    private AdminLogoutServlet servlet;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new AdminLogoutServlet();
    }

    @Test
    void testDoGet_shouldRemoveAdminObjectFromSession() throws ServletException, IOException {
        // Arrange
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doGet(request, response);
        
        // Assert
        verify(session).removeAttribute("adminObj");
    }

    @Test
    void testDoGet_shouldSetSuccessMessage() throws ServletException, IOException {
        // Arrange
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doGet(request, response);
        
        // Assert
        verify(session).setAttribute("successMsg", "Admin Logout Successfully");
    }

    @Test
    void testDoGet_shouldRedirectToAdminLoginPage() throws ServletException, IOException {
        // Arrange
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doGet(request, response);
        
        // Assert
        verify(response).sendRedirect("admin_login.jsp");
    }

    @Test
    void testDoGet_shouldExecuteAllStepsInOrder() throws ServletException, IOException {
        // Arrange
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doGet(request, response);
        
        // Assert - verify all operations were called
        verify(session).removeAttribute("adminObj");
        verify(session).setAttribute("successMsg", "Admin Logout Successfully");
        verify(response).sendRedirect("admin_login.jsp");
    }
}
