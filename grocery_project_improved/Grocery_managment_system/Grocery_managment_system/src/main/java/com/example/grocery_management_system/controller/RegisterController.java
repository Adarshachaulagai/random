package com.example.grocery_management_system.controller;

import com.example.grocery_management_system.MainApp;
import com.example.grocery_management_system.service.UserService;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class    RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    public void register() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm  = confirmPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            messageLabel.setText("Please fill in all fields."); return;
        }
        if (username.length() < 3) {
            messageLabel.setText("Username must be at least 3 characters."); return;
        }
        if (password.length() < 6) {
            messageLabel.setText("Password must be at least 6 characters."); return;
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            messageLabel.setText("Password must contain at least one special character (e.g. @, #, !, $)."); return;
        }

        if (!password.matches(".*[0-9].*")) {
            messageLabel.setText("Password must contain at least one number."); return;
        }

        if (!password.matches(".*[A-Z].*")) {
            messageLabel.setText("Password must contain at least one uppercase letter."); return;
        }
        if (!password.equals(confirm)) {
            messageLabel.setText("Passwords do not match."); return;
        }

        try {
            boolean success = userService.register(username, password);
            if (success) {
                messageLabel.setText("Account created! Redirecting to login...");
                PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                pause.setOnFinished(e -> goToLogin());
                pause.play();
            } else {
                messageLabel.setText("Username already taken. Try another.");
            }
        } catch (RuntimeException e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(MainApp.BASE + "login.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Login");
            stage.setMaximized(true);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
