package com.example.grocery_management_system.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL  = "jdbc:mysql://localhost:3306/grocery_shopingDB";
    private static final String USER = "root";
    private static final String PASS = "winter@#123";

    private static Connection connection;

    private DBConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASS);
            }
            return connection;
        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg != null && msg.toLowerCase().contains("access denied")) {
                throw new RuntimeException("Wrong MySQL password in DBConnection.java");
            } else if (msg != null && (msg.toLowerCase().contains("communications") || msg.toLowerCase().contains("connect"))) {
                throw new RuntimeException("Cannot connect to MySQL — is it running on port 3306?");
            } else {
                throw new RuntimeException("Database error: " + msg);
            }
        }
    }
}
