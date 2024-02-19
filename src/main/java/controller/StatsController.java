package controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class StatsController {

    @FXML
    private Label userCountLabel;

    @FXML
    private BarChart<String, Number> coinsHistogram;
    @FXML
    private TableView<Map<String, Object>> quizTableView;
    @FXML
    private TableColumn<Map<String, Object>, String> quizNameColumn;
    @FXML
    private TableColumn<Map<String, Object>, Number> averageHighscoreColumn;



    Connection connection = Database.getInstance().conn;

    public void initialize() {
        updateUserCount();
        loadCoinsHistogram();
        quizNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("quizName").toString()));
        averageHighscoreColumn.setCellValueFactory(data -> new SimpleDoubleProperty((Double)data.getValue().get("averageHighscore")));
        loadQuizData();
    }

    private void updateUserCount() {
        String query = "SELECT COUNT(*) AS count FROM user";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                // Aktualisiere den Text des Labels, um die Anzahl der User anzuzeigen
                userCountLabel.setText("Anzahl der User: " + count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            userCountLabel.setText("Fehler beim Laden der User-Anzahl");
        }
    }

    private void loadCoinsHistogram() {
        String query = "SELECT name, coins FROM user ORDER BY coins DESC";
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Coins");
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("name");
                int coins = resultSet.getInt("coins");
                if (coins > 0) {
                    series.getData().add(new XYChart.Data<>(username, coins));
                }
            }
            if (!series.getData().isEmpty()) {
                coinsHistogram.getData().add(series);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadQuizData() {

        ObservableList<Map<String, Object>> quizData = FXCollections.observableArrayList();
        String query = "SELECT q.name, AVG(h.highscore) AS averageHighscore FROM quiz q LEFT JOIN highscores h ON q.idQuiz = h.quiz_idQuiz GROUP BY q.name ORDER BY q.name;";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("quizName", rs.getString("name"));
                row.put("averageHighscore", rs.getDouble("averageHighscore"));
                quizData.add(row);
            }
            quizTableView.setItems(quizData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
