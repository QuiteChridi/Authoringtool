package controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.util.Duration;
import model.Database;
import model.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javax.swing.*;


public class StatsController {

    @FXML
    private Label noUserSelected;
    @FXML
    private Label userCountLabel;
    @FXML
    private Label totalJokersLabel;
    @FXML
    private Label totalQuestionsLabel;
    @FXML
    private Label totalQuizzesLabel;
    @FXML
    private BarChart<String, Number> userHighscoresHistogram;
    @FXML
    private ListView<String> usersListView;
    @FXML
    private TextField searchUserTextField;
    @FXML
    private ListView<String> selectedUsersListView;
    @FXML
    private DatePicker datePicker;
    @FXML
    private LineChart<String, Number> lineChart;

    private ObservableList<String> allUsers = FXCollections.observableArrayList();
    private ObservableList<String> filteredUsers = FXCollections.observableArrayList();
    private ObservableList<User> selectedUserReal = FXCollections.observableArrayList();
    Connection connection = Database.getInstance().conn;

    public void initialize() {
        updateUserCount();
        updateTotalJokers();
        updateTotalQuestions();
        updateTotalQuizzes();

        selectedUserReal.addListener(this::onSelectedUsersChanged);

        usersListView.setItems(filteredUsers);
        loadAllUsers();
        updateHighscoreHistogram();

        datePicker.setValue(LocalDate.now());
        updateMessagesChart(LocalDate.now());


    }

    public void onSelectedUsersChanged(ListChangeListener.Change<? extends User> change) {
        while (change.next()) {
            if (change.wasAdded() || change.wasRemoved()) {
                updateSelectedUsersTableView();
                updateHighscoreHistogram();
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
            if (!selectedUserReal.contains(getUserById(userId))) {
                selectedUserReal.add(getUserById(userId));
            }else {
                showAlreadyInListAlert();
            }

        }
    }

    private void showAlreadyInListAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Der Nutzer wurde bereits ausgewählt");

        alert.showAndWait();
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

    private void updateHighscoreHistogram() {

        userHighscoresHistogram.setAnimated(false);
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
                    addTooltipToBarChartSeries(series);

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        noUserSelected.setVisible(selectedUserReal.isEmpty());
        userHighscoresHistogram.setVisible(!selectedUserReal.isEmpty());
    }

    private void addTooltipToBarChartSeries(XYChart.Series<String, Number> series) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            Tooltip tooltip = new Tooltip();
            tooltip.setText(data.getYValue().toString());
            tooltip.setShowDelay(Duration.seconds(0.25));
            Node node = data.getNode();
            Tooltip.install(node, tooltip);
        }
    }

    @FXML
    private void handleDatePick() {
        LocalDate date = datePicker.getValue();
        updateMessagesChart(date);
    }

    private void updateMessagesChart(LocalDate date) {
        lineChart.setAnimated(false);
        String formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

        String query = "SELECT EXTRACT(HOUR FROM timestamp) AS hour, COUNT(*) AS message_count "
                + "FROM messages "
                + "WHERE DATE(timestamp) = ? "
                + "GROUP BY hour "
                + "ORDER BY hour;";

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nachrichten am " + formattedDate);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, formattedDate);
            ResultSet resultSet = preparedStatement.executeQuery();

            for (int i = 0; i < 24; i++) {
                series.getData().add(new XYChart.Data<>(String.format("%02d Uhr", i), 0));
            }
            while (resultSet.next()) {
                int hour = resultSet.getInt("hour");
                int messageCount = resultSet.getInt("message_count");
                series.getData().set(hour, new XYChart.Data<>(String.format("%02d Uhr", hour), messageCount));
            }
            lineChart.getData().clear();
            lineChart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}