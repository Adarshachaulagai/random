package com.example.grocery_management_system.controller;

import com.example.grocery_management_system.model.Product;
import com.example.grocery_management_system.service.ProductService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProductController implements DashboardController.NeedsRefresh {

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> colID;
    @FXML private TableColumn<Product, String>  colName;
    @FXML private TableColumn<Product, String>  colCategory;
    @FXML private TableColumn<Product, Double>  colPrice;
    @FXML private TableColumn<Product, Integer> colQty;
    @FXML private TableColumn<Product, Integer> colMin;
    @FXML private TextField searchField;

    // Edit fields
    @FXML private TextField editName;
    @FXML private TextField editCategory;
    @FXML private TextField editPrice;
    @FXML private TextField editQty;
    @FXML private TextField editMin;
    @FXML private Label statusLabel;

    private final ProductService service = new ProductService();
    private DashboardController dashboard;

    @Override
    public void setDashboard(DashboardController d) { this.dashboard = d; }

    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colMin.setCellValueFactory(new PropertyValueFactory<>("minimumStock"));

        // Highlight low-stock rows in red
        productTable.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                setStyle((p != null && !empty && p.isLowStock())
                    ? "-fx-background-color: #ffcccc;" : "");
            }
        });

        // When a row is selected, populate edit fields
        productTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, selected) -> {
                if (selected != null) {
                    editName.setText(selected.getName());
                    editCategory.setText(selected.getCategory());
                    editPrice.setText(String.valueOf(selected.getPrice()));
                    editQty.setText(String.valueOf(selected.getQuantity()));
                    editMin.setText(String.valueOf(selected.getMinimumStock()));
                    statusLabel.setText("");
                }
            });

        loadAll();
    }

    private void loadAll() {
        productTable.setItems(FXCollections.observableArrayList(service.getAll()));
    }

    @FXML
    public void search() {
        String q = searchField.getText().trim().toLowerCase();
        if (q.isEmpty()) { loadAll(); return; }
        productTable.setItems(FXCollections.observableArrayList(
            service.getAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(q)
                          || p.getCategory().toLowerCase().contains(q))
                .toList()));
    }

    @FXML
    public void saveEdit() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) { statusLabel.setText("Select a product to edit."); return; }

        String name     = editName.getText().trim();
        String category = editCategory.getText().trim();
        if (name.isEmpty()) { statusLabel.setText("Name cannot be empty."); return; }

        try {
            double price = Double.parseDouble(editPrice.getText().trim());
            int    qty   = Integer.parseInt(editQty.getText().trim());
            int    min   = Integer.parseInt(editMin.getText().trim());

            selected.setName(name);
            selected.setCategory(category.isEmpty() ? "General" : category);
            selected.setPrice(price);
            selected.setQuantity(qty);
            selected.setMinimumStock(min);

            boolean ok = service.updateProduct(selected);
            if (ok) {
                statusLabel.setText("Product updated successfully.");
                loadAll();
                if (dashboard != null) dashboard.refreshStats();
            } else {
                statusLabel.setText("Name already used by another product.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Price/Qty/Min must be valid numbers.");
        } catch (RuntimeException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void deleteSelected() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) { statusLabel.setText("Select a product to delete."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete \"" + selected.getName() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirm Delete");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    service.deleteProduct(selected.getProductID());
                    statusLabel.setText("Deleted: " + selected.getName());
                    loadAll();
                    clearEditFields();
                    if (dashboard != null) dashboard.refreshStats();
                } catch (RuntimeException e) {
                    statusLabel.setText("Cannot delete — product is used in a sale.");
                }
            }
        });
    }

    @FXML
    public void refresh() {
        loadAll();
        clearEditFields();
        statusLabel.setText("Refreshed.");
    }

    private void clearEditFields() {
        editName.clear(); editCategory.clear();
        editPrice.clear(); editQty.clear(); editMin.clear();
    }
}
