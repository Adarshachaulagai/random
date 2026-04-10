package com.example.grocery_management_system.controller;

import  com.example.grocery_management_system.MainApp;
import com.example.grocery_management_system.Session;
import com.example.grocery_management_system.service.ProductService;
import com.example.grocery_management_system.service.SalesService;
import com.example.grocery_management_system.service.StockManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label totalProductsLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label todaySalesLabel;
    @FXML private Label todayRevenueLabel;
    @FXML private AnchorPane contentPane;

    private final ProductService productService = new ProductService();
    private final SalesService   salesService   = new SalesService();
    private final StockManager   stockManager   = new StockManager();

    @FXML
    public void initialize() {
        if (Session.getCurrentUser() != null)
            welcomeLabel.setText("Welcome, " + Session.getCurrentUser().getUsername() + "!");
        refreshStats();
        showProducts();
    }

    public void refreshStats() {
        try {
            totalProductsLabel.setText(String.valueOf(productService.getTotalCount()));
            lowStockLabel.setText(String.valueOf(productService.getLowStockCount()));
            todaySalesLabel.setText(String.valueOf(salesService.getTodayCount()));
            todayRevenueLabel.setText("Rs. " + String.format("%.0f", salesService.getTodayRevenue()));
            stockManager.checkAndGenerateAlerts(productService.getAll());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML public void showProducts()    { loadPanel("product.fxml"); }
    @FXML public void showAddProduct()  { loadPanel("add_product.fxml"); }
    @FXML public void showSales()       { loadPanel("sales.fxml"); }
    @FXML public void showTodaySales()  { loadPanel("today_sales.fxml"); }
    @FXML public void showStockAlerts() { loadPanel("stock.fxml"); }

    @FXML
    public void logout() {
        try {
            Session.clear();
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(MainApp.BASE + "login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Login");
            stage.setMaximized(true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadPanel(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(MainApp.BASE + fxml));
            Node node = loader.load();
            Object controller = loader.getController();
            if (controller instanceof NeedsRefresh) {
                ((NeedsRefresh) controller).setDashboard(this);
            }
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
            contentPane.getChildren().setAll(node);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public interface NeedsRefresh {
        void setDashboard(DashboardController dashboard);
    }
}
