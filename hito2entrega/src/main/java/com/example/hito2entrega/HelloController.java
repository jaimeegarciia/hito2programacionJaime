package com.example.hito2entrega;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bson.Document;

import java.io.IOException;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;

public class HelloController {

    @FXML
    private ComboBox<String> teamComboBox;
    @FXML
    private TextField winsField, drawsField, lossesField, goalsDiffField;

    @FXML
    private TableView<Team> tableView;
    @FXML
    private TableColumn<Team, String> teamColumn;
    @FXML
    private TableColumn<Team, Integer> winsColumn;
    @FXML
    private TableColumn<Team, Integer> drawsColumn;
    @FXML
    private TableColumn<Team, Integer> lossesColumn;
    @FXML
    private TableColumn<Team, Integer> goalsDiffColumn;
    @FXML
    private TableColumn<Team, Integer> pointsColumn;
    @FXML
    private Button modifyButton, deleteButton;
    @FXML
    private Label messageLabel;

    private final ObservableList<Team> teams = FXCollections.observableArrayList();

    private final MongoCollection<Document> collection;

    public HelloController() {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://admin:admin@cluster0.ro73oyu.mongodb.net");
        MongoDatabase database = mongoClient.getDatabase("footbal_bd");
        collection = database.getCollection("teams");
    }

    @FXML
    protected void onSaveButtonClick() {
        Team team = createTeam(winsField, drawsField, lossesField, goalsDiffField);
        if (team != null) {
            teams.add(team);
            teams.sort((t1, t2) -> {
                if (t1.getPoints() != t2.getPoints()) {
                    return Integer.compare(t2.getPoints(), t1.getPoints());
                } else {
                    return Integer.compare(t2.getGoalsDiff(), t1.getGoalsDiff());
                }
            });
            saveTeamToDatabase(team);
            tableView.refresh();
            clearFields();
            messageLabel.setText("Equipo guardado exitosamente.");
        }
    }

    @FXML
    protected void onModifyButtonClick() {
        Team selectedTeam = tableView.getSelectionModel().getSelectedItem();
        if (selectedTeam != null) {
            try {
                int wins = Integer.parseInt(winsField.getText());
                int draws = Integer.parseInt(drawsField.getText());
                int losses = Integer.parseInt(lossesField.getText());
                int goalsDiff = Integer.parseInt(goalsDiffField.getText());

                selectedTeam.setWins(wins);
                selectedTeam.setDraws(draws);
                selectedTeam.setLosses(losses);
                selectedTeam.setGoalsDiff(goalsDiff);
                selectedTeam.setPoints(wins * 3 + draws);

                updateTeamInDatabase(selectedTeam);

                tableView.refresh();

                clearFields();

                messageLabel.setText("Equipo modificado exitosamente.");
            } catch (NumberFormatException e) {
                messageLabel.setText("Por favor, ingresa valores numéricos válidos.");
            }
        }
    }

    @FXML
    protected void onDeleteButtonClick() {
        Team selectedTeam = tableView.getSelectionModel().getSelectedItem();
        if (selectedTeam != null) {
            teams.remove(selectedTeam);
            deleteTeamFromDatabase(selectedTeam); // Delete team from MongoDB
            tableView.refresh();
            clearFields();
            messageLabel.setText("Equipo eliminado exitosamente.");
        }
    }

    @FXML
    protected void onLogoutButtonClick() throws IOException {
        HelloApplication.showLoginRegisterView();
    }

    private void clearFields() {
        teamComboBox.getSelectionModel().clearSelection();
        winsField.clear();
        drawsField.clear();
        lossesField.clear();
        goalsDiffField.clear();
    }

    private Team createTeam(TextField winsField, TextField drawsField, TextField lossesField, TextField goalsDiffField) {
        try {
            String name = teamComboBox.getValue();

            // Verificar si el equipo ya existe en la tabla
            if (isTeamAlreadyAdded(name)) {
                messageLabel.setText("El equipo ya está en la tabla.");
                return null;
            }

            int wins = Integer.parseInt(winsField.getText());
            int draws = Integer.parseInt(drawsField.getText());
            int losses = Integer.parseInt(lossesField.getText());
            int goalsDiff = Integer.parseInt(goalsDiffField.getText());
            int points = wins * 3 + draws;

            return new Team(name, wins, draws, losses, goalsDiff, points);
        } catch (NumberFormatException e) {
            messageLabel.setText("Por favor, ingresa valores numéricos válidos.");
            return null;
        }
    }

    private boolean isTeamAlreadyAdded(String name) {
        for (Team team : teams) {
            if (team.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }


    private void saveTeamToDatabase(Team team) {
        Document doc = new Document("name", team.getName())
                .append("wins", team.getWins())
                .append("draws", team.getDraws())
                .append("losses", team.getLosses())
                .append("goalsDiff", team.getGoalsDiff())
                .append("points", team.getPoints());

        collection.insertOne(doc);
    }

    private void updateTeamInDatabase(Team team) {
        collection.updateOne(eq("name", team.getName()), new Document("$set", new Document()
                .append("wins", team.getWins())
                .append("draws", team.getDraws())
                .append("losses", team.getLosses())
                .append("goalsDiff", team.getGoalsDiff())
                .append("points", team.getPoints())));
    }

    private void deleteTeamFromDatabase(Team team) {
        collection.deleteOne(eq("name", team.getName()));
    }

    @FXML
    public void initialize() {
        ObservableList<Team> teamData = FXCollections.observableArrayList();
        collection.find().forEach((Consumer<? super Document>) doc -> {
            Team team = new Team(
                    doc.getString("name"),
                    doc.getInteger("wins"),
                    doc.getInteger("draws"),
                    doc.getInteger("losses"),
                    doc.getInteger("goalsDiff"),
                    doc.getInteger("points")
            );
            teamData.add(team);
        });
        teams.addAll(teamData);

        ObservableList<String> teamNames = FXCollections.observableArrayList(
                "Betis", "Atleti", "Barcelona", "Real Madrid", "Girona", "Athletic"
        );

        teamComboBox.setItems(teamNames);

        teamColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        winsColumn.setCellValueFactory(new PropertyValueFactory<>("wins"));
        drawsColumn.setCellValueFactory(new PropertyValueFactory<>("draws"));
        lossesColumn.setCellValueFactory(new PropertyValueFactory<>("losses"));
        goalsDiffColumn.setCellValueFactory(new PropertyValueFactory<>("goalsDiff"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));

        tableView.setItems(teams);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                teamComboBox.setValue(newSelection.getName());
                winsField.setText(Integer.toString(newSelection.getWins()));
                drawsField.setText(Integer.toString(newSelection.getDraws()));
                lossesField.setText(Integer.toString(newSelection.getLosses()));
                goalsDiffField.setText(Integer.toString(newSelection.getGoalsDiff()));
            }
        });
    }
}
