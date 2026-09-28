package com.hms.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection utility class for Hospital Management System.
 * Updated for Java 17 compatibility - removed deprecated Class.forName() call.
 * 
 * Note: This implementation uses hardcoded credentials for demonstration purposes.
 * In production, use environment variables or configuration files for credentials.
 */
public class DBConnection {

	// Database configuration - should be externalized in production
	private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "wasim";
	
	/**
	 * Gets a database connection.
	 * Note: JDBC 4.0+ automatically loads drivers, so Class.forName() is no longer needed.
	 * 
	 * @return Connection object or null if connection fails
	 */
	public static Connection getConn() {
		Connection conn = null;
		
		try {
			// JDBC 4.0+ automatically loads the driver from the classpath
			// No need for Class.forName("com.mysql.cj.jdbc.Driver")
			conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
			
		} catch (SQLException e) {
			System.err.println("Database connection failed: " + e.getMessage());
			e.printStackTrace();
		}
		
		return conn;
	}
}
