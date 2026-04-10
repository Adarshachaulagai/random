package com.example.grocery_management_system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static final String BASE = "/com/example/grocery_management_system/";

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(BASE + "login.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Grocery Management System");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
