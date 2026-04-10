package com.example.grocery_management_system.service;

import com.example.grocery_management_system.database.DBConnection;
import com.example.grocery_management_system.model.Product;
import com.example.grocery_management_system.model.StockAlert;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockManager {

    public int checkStock(Product product) {
        return product.getQuantity();
    }

    public void restockProduct(Product product, int qty) {
        product.updateStock(qty);
    }

    public boolean detectLowStock(Product product) {
        return product.isLowStock();
    }


    public void generateLowStockAlert(Product product) {
        Connection conn = DBConnection.getConnection();
        try {

            PreparedStatement check = conn.prepareStatement(
                "SELECT alertID FROM stock_alert WHERE productID=? AND resolved=FALSE");
            check.setInt(1, product.getProductID());
            if (check.executeQuery().next()) return;

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO stock_alert (productID, alertMessage) VALUES (?,?)");
            ps.setInt(1, product.getProductID());
            ps.setString(2, "LOW STOCK: " + product.getName() +
                " — only " + product.getQuantity() + " left (min: " + product.getMinimumStock() + ")");
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<StockAlert> getUnresolvedAlerts() {
        List<StockAlert> list = new ArrayList<>();
        try {
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(
                "SELECT sa.*, p.name AS productName FROM stock_alert sa " +
                "JOIN product p ON sa.productID=p.productID " +
                "WHERE sa.resolved=FALSE ORDER BY sa.alertDate DESC");
            while (rs.next()) {
                StockAlert a = new StockAlert();
                a.setAlertID(rs.getInt("alertID"));
                a.setProductID(rs.getInt("productID"));
                a.setAlertMessage(rs.getString("alertMessage"));
                a.setAlertDate(rs.getString("alertDate"));
                a.setProductName(rs.getString("productName"));
                a.setResolved(false);
                list.add(a);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void resolveAlert(int alertID) {
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "UPDATE stock_alert SET resolved=TRUE WHERE alertID=?");
            ps.setInt(1, alertID);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }


    public void checkAndGenerateAlerts(List<Product> products) {
        for (Product p : products) {
            if (detectLowStock(p)) generateLowStockAlert(p);
        }
    }
}
