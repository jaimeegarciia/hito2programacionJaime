package com.example.hito2entrega;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import org.bson.Document;

import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;

public class LoginRegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;
    @FXML
    private CheckBox robotCheckBox;

    private final MongoCollection<Document> usersCollection;

    public LoginRegisterController() {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://admin:admin@cluster0.ro73oyu.mongodb.net");
        MongoDatabase database = mongoClient.getDatabase("footbal_bd");
        usersCollection = database.getCollection("users");
    }

    @FXML
    protected void onRegisterButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Por favor, completa todos los campos.");
            return;
        }

        Document existingUser = usersCollection.find(eq("username", username)).first();
        if (existingUser != null) {
            messageLabel.setText("El usuario ya existe.");
            return;
        }

        Document newUser = new Document("username", username).append("password", password);
        usersCollection.insertOne(newUser);
        messageLabel.setText("Registro exitoso. Ahora puedes iniciar sesión.");
    }

    @FXML
    protected void onLoginButtonClick() throws IOException {
        // Verificar si la casilla de verificación está marcada
        if (!robotCheckBox.isSelected()) {
            messageLabel.setText("Por favor, confirma que no eres un robot.");
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Por favor, completa todos los campos.");
            return;
        }

        Document existingUser = usersCollection.find(eq("username", username)).first();
        if (existingUser == null || !existingUser.getString("password").equals(password)) {
            messageLabel.setText("Credenciales incorrectas.");
            return;
        }

        // Cargar la vista principal
        HelloApplication.showMainView();
    }
}
