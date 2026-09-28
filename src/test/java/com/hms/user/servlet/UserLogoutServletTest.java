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

class UserLogoutServletTest {

    private UserLogoutServlet servlet;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new UserLogoutServlet();
    }

    @Test
    void testDoGet_shouldRemoveUserObjectFromSession() throws ServletException, IOException {
        // Arrange
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doGet(request, response);
        
        // Assert
        verify(session).removeAttribute("userObj");
    }

    @Test
    void testDoGet_shouldSetSuccessMessage() throws ServletException, IOException {
        // Arrange
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doGet(request, response);
        
        // Assert
        verify(session).setAttribute("successMsg", "User Logout Successfully.");
    }

    @Test
    void testDoGet_shouldRedirectToUserLoginPage() throws ServletException, IOException {
        // Arrange
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doGet(request, response);
        
        // Assert
        verify(response).sendRedirect("user_login.jsp");
    }

    @Test
    void testDoGet_shouldExecuteAllStepsInOrder() throws ServletException, IOException {
        // Arrange
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doGet(request, response);
        
        // Assert - verify all operations were called
        verify(session).removeAttribute("userObj");
        verify(session).setAttribute("successMsg", "User Logout Successfully.");
        verify(response).sendRedirect("user_login.jsp");
    }
}
