package com.example.grocery_management_system.service;

import com.example.grocery_management_system.database.DBConnection;
import com.example.grocery_management_system.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService {


    public boolean addProduct(Product p) {
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement check = conn.prepareStatement(
                "SELECT productID FROM product WHERE LOWER(name)=LOWER(?)");
            check.setString(1, p.getName());
            if (check.executeQuery().next()) return false;
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO product (name,category,price,quantity,minimumStock) VALUES (?,?,?,?,?)");
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getQuantity());
            ps.setInt(5, p.getMinimumStock());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Add product failed: " + e.getMessage());
        }
    }

    public boolean updateProduct(Product p) {
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement check = conn.prepareStatement(
                "SELECT productID FROM product WHERE LOWER(name)=LOWER(?) AND productID!=?");
            check.setString(1, p.getName());
            check.setInt(2, p.getProductID());
            if (check.executeQuery().next()) return false; // name taken by another product

            PreparedStatement ps = conn.prepareStatement(
                "UPDATE product SET name=?,category=?,price=?,quantity=?,minimumStock=? WHERE productID=?");
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getQuantity());
            ps.setInt(5, p.getMinimumStock());
            ps.setInt(6, p.getProductID());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Update product failed: " + e.getMessage());
        }
    }

    public boolean deleteProduct(int id) {
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM product WHERE productID=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Delete failed: " + e.getMessage());
        }
    }

    public boolean restockProduct(int id, int qty) {
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE product SET quantity=quantity+? WHERE productID=?");
            ps.setInt(1, qty);
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Restock failed: " + e.getMessage());
        }
    }

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT * FROM product ORDER BY name");
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e.getMessage()); }
        return list;
    }

    public List<Product> getLowStock() {
        List<Product> list = new ArrayList<>();
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT * FROM product WHERE quantity<=minimumStock ORDER BY quantity");
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e.getMessage()); }
        return list;
    }

    public int getTotalCount() {
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT COUNT(*) FROM product");
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int getLowStockCount() {
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT COUNT(*) FROM product WHERE quantity<=minimumStock");
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private Product map(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductID(rs.getInt("productID"));
        p.setName(rs.getString("name"));
        p.setCategory(rs.getString("category"));
        p.setPrice(rs.getDouble("price"));
        p.setQuantity(rs.getInt("quantity"));
        p.setMinimumStock(rs.getInt("minimumStock"));
        return p;
    }
}
