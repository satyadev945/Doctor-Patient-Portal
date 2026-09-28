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

class DeleteDoctorServletTest {

    private DeleteDoctorServlet servlet;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new DeleteDoctorServlet();
    }

    @Test
    void testDoGet_withValidId_shouldParseIdCorrectly() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("id")).thenReturn("123");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doGet(request, response);
        
        // Assert
        verify(request).getParameter("id");
    }

    @Test
    void testDoGet_withNegativeId_shouldHandleGracefully() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("id")).thenReturn("-1");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doGet(request, response);
        
        // Assert
        verify(session).setAttribute(eq("errorMsg"), anyString());
        verify(response).sendRedirect("admin/view_doctor.jsp");
    }

    @Test
    void testDoGet_withZeroId_shouldHandleGracefully() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("id")).thenReturn("0");
        when(request.getSession()).thenReturn(session);
        
        // Act
        servlet.doGet(request, response);
        
        // Assert
        verify(response).sendRedirect("admin/view_doctor.jsp");
    }

    @Test
    void testDoGet_withInvalidIdFormat_shouldThrowNumberFormatException() {
        // Arrange
        when(request.getParameter("id")).thenReturn("invalid");
        when(request.getSession()).thenReturn(session);
        
        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            servlet.doGet(request, response);
        });
    }

    @Test
    void testDoGet_withNullId_shouldThrowNumberFormatException() {
        // Arrange
        when(request.getParameter("id")).thenReturn(null);
        when(request.getSession()).thenReturn(session);
        
        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            servlet.doGet(request, response);
        });
    }
}
