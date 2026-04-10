package com.example.grocery_management_system.service;

import com.example.grocery_management_system.database.DBConnection;
import com.example.grocery_management_system.model.User;
import java.sql.*;

public class UserService {

    public User login(String username, String password) {
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("userID"), rs.getString("username"),
                                rs.getString("password"), rs.getString("role"));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }


    public boolean register(String username, String password) {
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement check = conn.prepareStatement(
                "SELECT userID FROM users WHERE username=?");
            check.setString(1, username);
            if (check.executeQuery().next()) return false;

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (username, password, role) VALUES (?,?,'user')");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Register failed: " + e.getMessage());
        }
    }
}
