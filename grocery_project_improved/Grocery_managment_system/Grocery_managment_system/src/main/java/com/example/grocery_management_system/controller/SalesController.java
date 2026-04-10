package com.example.grocery_management_system.controller;

import com.example.grocery_management_system.Session;
import com.example.grocery_management_system.model.CartItem;
import com.example.grocery_management_system.model.Product;
import com.example.grocery_management_system.service.ProductService;
import com.example.grocery_management_system.service.SalesService;
import com.example.grocery_management_system.service.StockManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;

public class SalesController implements DashboardController.NeedsRefresh {

    @FXML private ComboBox<Product> productCombo;
    @FXML private TextField qtyField;
    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String>  colProduct;
    @FXML private TableColumn<CartItem, Double>  colUnitPrice;
    @FXML private TableColumn<CartItem, Integer> colQty;
    @FXML private TableColumn<CartItem, Double>  colSubtotal;
    @FXML private Label totalLabel;
    @FXML private Label messageLabel;

    private final ProductService productService = new ProductService();
    private final SalesService   salesService   = new SalesService();
    private final StockManager   stockManager   = new StockManager();
    private final ObservableList<CartItem> cart = FXCollections.observableArrayList();
    private DashboardController dashboard;

    @Override
    public void setDashboard(DashboardController d) { this.dashboard = d; }

    @FXML
    public void initialize() {
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        cartTable.setItems(cart);
        refreshProducts();
        updateTotal();
    }

    private void refreshProducts() {
        productCombo.setItems(FXCollections.observableArrayList(productService.getAll()));
    }

    @FXML
    public void addToCart() {
        Product selected = productCombo.getSelectionModel().getSelectedItem();
        if (selected == null) { show("Select a product first.", "red"); return; }

        int qty;
        try {
            qty = Integer.parseInt(qtyField.getText().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            show("Enter a valid quantity (must be > 0).", "red"); return;
        }

        if (qty > selected.getQuantity()) {
            show("Not enough stock! Only " + selected.getQuantity() + " available.", "red"); return;
        }
        for (CartItem item : cart) {
            if (item.getProduct().getProductID() == selected.getProductID()) {
                int newQty = item.getQuantity() + qty;
                if (newQty > selected.getQuantity()) {
                    show("Total qty exceeds available stock (" + selected.getQuantity() + ").", "red"); return;
                }
                item.setQuantity(newQty);
                cartTable.refresh();
                updateTotal();
                qtyField.clear();
                show("Quantity updated in cart.", "green");
                return;
            }
        }

        cart.add(new CartItem(selected, qty));
        updateTotal();
        qtyField.clear();
        show("Added to cart.", "green");
    }

    @FXML
    public void removeFromCart() {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected == null) { show("Select an item to remove.", "red"); return; }
        cart.remove(selected);
        updateTotal();
        show("Item removed.", "green");
    }

    @FXML
    public void completeSale() {
        if (cart.isEmpty()) { show("Cart is empty!", "red"); return; }

        double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Complete sale for Rs. " + String.format("%.2f", total) + "?",
            ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirm Sale");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    int userID = Session.getCurrentUser() != null
                        ? Session.getCurrentUser().getUserID() : 1;
                    salesService.recordSale(new ArrayList<>(cart), userID);
                    stockManager.checkAndGenerateAlerts(productService.getAll());
                    show("Sale completed! Total: Rs. " + String.format("%.2f", total), "green");
                    cart.clear();
                    refreshProducts();
                    updateTotal();
                    if (dashboard != null) dashboard.refreshStats();
                } catch (RuntimeException e) {
                    show("Error: " + e.getMessage(), "red");
                }
            }
        });
    }

    @FXML
    public void clearCart() {
        cart.clear();
        updateTotal();
        show("Cart cleared.", "green");
    }

    private void updateTotal() {
        double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
        totalLabel.setText("Total: Rs. " + String.format("%.2f", total));
    }

    private void show(String msg, String color) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}
