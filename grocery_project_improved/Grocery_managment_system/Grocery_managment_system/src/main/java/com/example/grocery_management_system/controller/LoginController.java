package com.example.grocery_management_system.controller;

import com.example.grocery_management_system.MainApp;
import com.example.grocery_management_system.Session;
import com.example.grocery_management_system.model.User;
import com.example.grocery_management_system.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    public void login() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        messageLabel.setText("");

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password."); return;
        }
        try {
            User user = userService.login(username, password);
            if (user != null) {
                Session.setCurrentUser(user);
                navigate("dashboard.fxml", "Dashboard");
            } else {
                messageLabel.setText("Invalid username or password.");
                passwordField.clear();
            }
        } catch (RuntimeException e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void goToRegister() {
        navigate("register.fxml", "Register");
    }

    private void navigate(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(MainApp.BASE + fxml));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
            stage.setMaximized(true);
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
