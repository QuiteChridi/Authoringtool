package controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import model.Database;
import model.User;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsController {

    @FXML
    private Label noHighscoresLabel;
    @FXML
    private Label userCountLabel;
    @FXML
    private Label totalJokersLabel;
    @FXML
    private Label totalQuestionsLabel;
    @FXML
    private Label totalQuizzesLabel;
    @FXML
    private BarChart<String, Number> coinsHistogram;
    @FXML
    private TableView<Map<String, Object>> quizTableView;
    @FXML
    private TableColumn<Map<String, Object>, String> quizNameColumn;
    @FXML
    private TableColumn<Map<String, Object>, Number> averageHighscoreColumn;
    @FXML
    private BarChart<String, Number> userHighscoresHistogram;
    @FXML
    private ListView<String> usersListView;
    @FXML
    private TextField searchUserTextField;
    @FXML
    private ListView<String> selectedUsersListView;

    private ObservableList<String> allUsers = FXCollections.observableArrayList();
    private ObservableList<String> filteredUsers = FXCollections.observableArrayList();
    private ObservableList<User> selectedUserReal = FXCollections.observableArrayList();
    Connection connection = Database.getInstance().conn;

    public void initialize() {
        updateUserCount();
        updateTotalJokers();
        updateTotalQuestions();
        updateTotalQuizzes();

        quizNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("quizName").toString()));
        averageHighscoreColumn.setCellValueFactory(data -> new SimpleDoubleProperty((Double)data.getValue().get("averageHighscore")));
        loadQuizData();

        selectedUserReal.addListener(this::onSelectedUsersChanged);

        usersListView.setItems(filteredUsers);
        loadAllUsers();
    }

    public void onSelectedUsersChanged(ListChangeListener.Change<? extends User> change) {
        while (change.next()) {
            if (change.wasAdded() || change.wasRemoved()) {
                updateSelectedUsersTableView();
                updateHistogram();
            }
        }
    }

    @FXML
    private void removeUserFromSelectedUser() {
        String selectedUsername = selectedUsersListView.getSelectionModel().getSelectedItem();
        if (selectedUsername != null) {
            User userToRemove = null;
            for (User user : selectedUserReal) {
                if (user.getUsername().equals(selectedUsername)) {
                    userToRemove = user;
                    break;
                }
            }
            if (userToRemove != null) {
                selectedUserReal.remove(userToRemove);
            }
        }
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

    private void updateTotalJokers() {
        String query = "SELECT SUM(fifty_fifty_joker + double_points_joker + pause_joker) AS totalJokers FROM user";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int totalJokers = resultSet.getInt("totalJokers");
                totalJokersLabel.setText("Gesamtanzahl der Joker: " + totalJokers);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            totalJokersLabel.setText("Fehler beim Laden der Joker-Anzahl");
        }
    }

    private void updateTotalQuestions() {
        String query = "SELECT COUNT(*) AS totalQuestions FROM questions";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int totalQuestions = resultSet.getInt("totalQuestions");
                totalQuestionsLabel.setText("Gesamtanzahl der Fragen: " + totalQuestions);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            totalQuestionsLabel.setText("Fehler beim Laden der Fragenanzahl");
        }
    }

    private void updateTotalQuizzes() {
        String query = "SELECT COUNT(*) AS totalQuizzes FROM quiz";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int totalQuizzes = resultSet.getInt("totalQuizzes");
                totalQuizzesLabel.setText("Gesamtanzahl der Quizzes: " + totalQuizzes);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            totalQuizzesLabel.setText("Fehler beim Laden der Quizanzahl");
        }
    }

    private void loadAllUsers() {
        String query = "SELECT name FROM user";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                allUsers.add(resultSet.getString("name"));
            }
            filterUsersList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void filterUsersList() {
        String searchText = searchUserTextField.getText().toLowerCase();
        filteredUsers.setAll(allUsers.stream()
                .filter(name -> name.toLowerCase().contains(searchText))
                .collect(Collectors.toList()));
        usersListView.refresh();
    }

    private void updateSelectedUsersTableView(){
        selectedUsersListView.getItems().clear();
        for (User user : selectedUserReal) {
            selectedUsersListView.getItems().add(user.getUsername());
        }
    }

    @FXML
    private void handleUserSelection() {
        String selectedUsername = usersListView.getSelectionModel().getSelectedItem();
        if (selectedUsername != null) {
            int userId = getUserIdByUsername(selectedUsername);
            selectedUserReal.add(getUserById(userId));
        }
    }

    private int getUserIdByUsername(String username) {

        String query = "SELECT iduser FROM user WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("iduser");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Rückgabe von -1, wenn keine userId gefunden wurde
    }

    private User getUserById(int userId) {
        String query = "SELECT * FROM user WHERE iduser = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("iduser");
                String username = resultSet.getString("name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                int coins = resultSet.getInt("coins");

                return new User(id, username, email, password, coins);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ResultSet loadUserHighscores(User user) throws SQLException {
        String query = "SELECT quiz.name, highscores.highscore "
                + "FROM highscores "
                + "JOIN quiz ON highscores.quiz_idQuiz = quiz.idQuiz "
                + "WHERE highscores.user_iduser = ? "
                + "ORDER BY highscores.highscore DESC;";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, user.getUserId());
        return preparedStatement.executeQuery();
    }

    private void updateHistogram() {
        userHighscoresHistogram.getData().clear();

        for (User user : selectedUserReal) {
            try {
                ResultSet resultSet = loadUserHighscores(user);
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(user.getUsername());

                boolean hasData = false;
                while (resultSet.next()) {
                    hasData = true;
                    series.getData().add(new XYChart.Data<>(
                            resultSet.getString("name"),
                            resultSet.getInt("highscore")
                    ));
                }
                if (hasData) {
                    userHighscoresHistogram.getData().add(series);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        noHighscoresLabel.setVisible(userHighscoresHistogram.getData().isEmpty());
        userHighscoresHistogram.setVisible(!userHighscoresHistogram.getData().isEmpty());
    }
}