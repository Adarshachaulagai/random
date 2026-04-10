package com.example.grocery_management_system.controller;

import com.example.grocery_management_system.model.Product;
import com.example.grocery_management_system.service.ProductService;
import com.example.grocery_management_system.service.StockManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class StockController implements DashboardController.NeedsRefresh {

    @FXML private TableView<Product> stockTable;
    @FXML private TableColumn<Product, String>  colName;
    @FXML private TableColumn<Product, String>  colCategory;
    @FXML private TableColumn<Product, Integer> colQty;
    @FXML private TableColumn<Product, Integer> colMin;
    @FXML private TextField restockQtyField;
    @FXML private Label statusLabel;
    @FXML private Label countLabel;

    private final ProductService productService = new ProductService();
    private final StockManager   stockManager   = new StockManager();
    private DashboardController dashboard;

    @Override
    public void setDashboard(DashboardController d) { this.dashboard = d; }

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colMin.setCellValueFactory(new PropertyValueFactory<>("minimumStock"));

        stockTable.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                setStyle((p != null && !empty) ? "-fx-background-color: #ffcccc;" : "");
            }
        });

        loadData();
    }

    private void loadData() {
        var items = productService.getLowStock();
        stockTable.setItems(FXCollections.observableArrayList(items));
        int count = items.size();
        countLabel.setText(count == 0
            ? "All products have sufficient stock."
            : count + " product(s) need restocking.");
    }

    @FXML
    public void restock() {
        Product selected = stockTable.getSelectionModel().getSelectedItem();
        if (selected == null) { statusLabel.setText("Select a product to restock."); return; }

        int qty;
        try {
            qty = Integer.parseInt(restockQtyField.getText().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            statusLabel.setText("Enter a valid quantity (must be > 0)."); return;
        }

        try {
            productService.restockProduct(selected.getProductID(), qty);
            // Resolve any open alerts for this product
            stockManager.getUnresolvedAlerts().stream()
                .filter(a -> a.getProductID() == selected.getProductID())
                .forEach(a -> stockManager.resolveAlert(a.getAlertID()));

            statusLabel.setText("Restocked \"" + selected.getName() + "\" +  " + qty + " units.");
            restockQtyField.clear();
            loadData();
            if (dashboard != null) dashboard.refreshStats();
        } catch (RuntimeException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void refresh() {
        loadData();
        statusLabel.setText("Refreshed.");
    }
}
