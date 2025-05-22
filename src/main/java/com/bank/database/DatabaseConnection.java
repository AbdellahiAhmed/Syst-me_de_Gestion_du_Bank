package com.bank.database;

import com.bank.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/bank";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static DatabaseConnection instance;

    private DatabaseConnection() {}

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }


    public Connection getConnection() throws DatabaseException {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("JDBC driver not found", e);
        } catch (SQLException e) {
            throw new DatabaseException("Database connection error", e);
        }
    }


    public boolean testConnection() {
        try (Connection testConn = getConnection()) {
            return testConn != null && !testConn.isClosed();
        } catch (DatabaseException | SQLException e) {
            System.err.println("Connection test error: " + e.getMessage());
            return false;
        }
    }

    public void closeConnection() {}
}