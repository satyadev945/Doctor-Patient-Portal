package com.hms.user.servlet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MyNewServletTest {

    private MyNewServlet servlet;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        servlet = new MyNewServlet();
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @Test
    void testConstructor_shouldCreateServlet() {
        // Assert
        assertNotNull(servlet);
    }

    @Test
    void testDoGet_shouldWriteResponse() throws ServletException, IOException {
        // Arrange
        when(response.getWriter()).thenReturn(writer);
        when(request.getContextPath()).thenReturn("/testapp");
        
        // Act
        servlet.doGet(request, response);
        
        // Assert
        verify(response).getWriter();
        verify(request).getContextPath();
    }

    @Test
    void testDoPost_shouldCallDoGet() throws ServletException, IOException {
        // Arrange
        when(response.getWriter()).thenReturn(writer);
        when(request.getContextPath()).thenReturn("/testapp");
        
        // Act
        servlet.doPost(request, response);
        
        // Assert
        verify(response).getWriter();
        verify(request).getContextPath();
    }

    @Test
    void testDoGet_withNullContextPath_shouldHandleGracefully() throws ServletException, IOException {
        // Arrange
        when(response.getWriter()).thenReturn(writer);
        when(request.getContextPath()).thenReturn(null);
        
        // Act & Assert
        assertDoesNotThrow(() -> {
            servlet.doGet(request, response);
        });
    }

    @Test
    void testDoGet_withEmptyContextPath_shouldHandleGracefully() throws ServletException, IOException {
        // Arrange
        when(response.getWriter()).thenReturn(writer);
        when(request.getContextPath()).thenReturn("");
        
        // Act
        servlet.doGet(request, response);
        
        // Assert
        verify(response).getWriter();
    }
}
