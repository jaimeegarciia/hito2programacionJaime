package com.example.hito2entrega;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        HelloApplication.primaryStage = primaryStage;
        showLoginRegisterView();
    }

    public static void showLoginRegisterView() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("login-register-view.fxml")));
        primaryStage.setTitle("Registro e Inicio de Sesión");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void showMainView() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("hello-view.fxml")));
        primaryStage.setTitle("Liga de Fútbol");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
