package com.example.grocery_management_system.service;

import com.example.grocery_management_system.database.DBConnection;
import com.example.grocery_management_system.model.CartItem;
import com.example.grocery_management_system.model.Sale;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalesService {

    public boolean recordSale(List<CartItem> cart, int userID) {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO sales (totalAmount, userID) VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, total);
            ps.setInt(2, userID);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (!keys.next()) { conn.rollback(); return false; }
            int saleID = keys.getInt(1);

            PreparedStatement pi = conn.prepareStatement(
                "INSERT INTO sale_items (quantity, unitPrice, saleID, productID) VALUES (?,?,?,?)");
            PreparedStatement pu = conn.prepareStatement(
                "UPDATE product SET quantity=quantity-? WHERE productID=?");

            for (CartItem item : cart) {
                pi.setInt(1, item.getQuantity());
                pi.setDouble(2, item.getUnitPrice());
                pi.setInt(3, saleID);
                pi.setInt(4, item.getProduct().getProductID());
                pi.addBatch();

                pu.setInt(1, item.getQuantity());
                pu.setInt(2, item.getProduct().getProductID());
                pu.addBatch();
            }
            pi.executeBatch();
            pu.executeBatch();
            conn.commit();
            return true;
        } catch (Exception e) {
            try { conn.rollback(); } catch (Exception ignored) {}
            throw new RuntimeException("Sale failed: " + e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (Exception ignored) {}
        }
    }

    public List<Sale> getTodaySales() {
        List<Sale> list = new ArrayList<>();
        try {
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(
                "SELECT s.saleID, s.totalAmount, s.saleDate, u.username " +
                "FROM sales s JOIN users u ON s.userID=u.userID " +
                "WHERE DATE(s.saleDate)=CURDATE() ORDER BY s.saleDate DESC");
            while (rs.next()) {
                Sale s = new Sale();
                s.setSaleID(rs.getInt("saleID"));
                s.setTotalAmount(rs.getDouble("totalAmount"));
                s.setSaleDate(rs.getString("saleDate"));
                s.setUsername(rs.getString("username"));
                list.add(s);
            }
        } catch (SQLException e) { throw new RuntimeException(e.getMessage()); }
        return list;
    }

    public double getTodayRevenue() {
        try {
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(
                "SELECT IFNULL(SUM(totalAmount),0) FROM sales WHERE DATE(saleDate)=CURDATE()");
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int getTodayCount() {
        try {
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(
                "SELECT COUNT(*) FROM sales WHERE DATE(saleDate)=CURDATE()");
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}
