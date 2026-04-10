package com.example.grocery_management_system.controller;

import com.example.grocery_management_system.model.Sale;
import com.example.grocery_management_system.service.SalesService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class TodaySalesController implements DashboardController.NeedsRefresh {

    @FXML private TableView<Sale> salesTable;
    @FXML private TableColumn<Sale, Integer> colID;
    @FXML private TableColumn<Sale, String>  colDate;
    @FXML private TableColumn<Sale, Double>  colTotal;
    @FXML private TableColumn<Sale, String>  colUser;
    @FXML private Label summaryLabel;

    private final SalesService salesService = new SalesService();
    private DashboardController dashboard;

    @Override
    public void setDashboard(DashboardController d) { this.dashboard = d; }

    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("saleID"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        loadSales();
    }

    private void loadSales() {
        try {
            var sales = salesService.getTodaySales();
            salesTable.setItems(FXCollections.observableArrayList(sales));
            double total = sales.stream().mapToDouble(Sale::getTotalAmount).sum();
            summaryLabel.setText("Transactions today: " + sales.size() +
                "   |   Total revenue: Rs. " + String.format("%.2f", total));
        } catch (RuntimeException e) {
            summaryLabel.setText("Error loading sales: " + e.getMessage());
        }
    }

    @FXML
    public void refresh() { loadSales(); }
}
