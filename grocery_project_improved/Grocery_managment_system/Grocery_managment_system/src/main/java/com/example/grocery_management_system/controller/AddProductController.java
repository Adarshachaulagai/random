package com.example.grocery_management_system.controller;

import com.example.grocery_management_system.model.Product;
import com.example.grocery_management_system.service.ProductService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AddProductController implements DashboardController.NeedsRefresh {

    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private TextField minStockField;
    @FXML private Label messageLabel;

    private final ProductService service = new ProductService();
    private DashboardController dashboard;

    @Override
    public void setDashboard(DashboardController d) { this.dashboard = d; }

    @FXML
    public void addProduct() {
        String name     = nameField.getText().trim();
        String category = categoryField.getText().trim();
        String priceStr = priceField.getText().trim();
        String qtyStr   = quantityField.getText().trim();
        String minStr   = minStockField.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty() || minStr.isEmpty()) {
            messageLabel.setText("Please fill in all required fields (*).");
            return;
        }
        if (category.isEmpty()) category = "General";

        try {
            double price = Double.parseDouble(priceStr);
            int qty      = Integer.parseInt(qtyStr);
            int minStock = Integer.parseInt(minStr);

            if (price <= 0)    { messageLabel.setText("Price must be greater than 0."); return; }
            if (qty < 0)       { messageLabel.setText("Quantity cannot be negative."); return; }
            if (minStock < 0)  { messageLabel.setText("Minimum stock cannot be negative."); return; }

            Product p = new Product();
            p.setName(name);
            p.setCategory(category);
            p.setPrice(price);
            p.setQuantity(qty);
            p.setMinimumStock(minStock);

            boolean added = service.addProduct(p);
            if (added) {
                messageLabel.setText("Product \"" + name + "\" added successfully!");
                clearFields();
                if (dashboard != null) dashboard.refreshStats();
            } else {
                messageLabel.setText("A product named \"" + name + "\" already exists. Use Edit to update it.");
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Price, Quantity and Min Stock must be valid numbers.");
        } catch (RuntimeException e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void clearFields() {
        nameField.clear(); categoryField.clear(); priceField.clear();
        quantityField.clear(); minStockField.clear();
        messageLabel.setText("");
    }
}
