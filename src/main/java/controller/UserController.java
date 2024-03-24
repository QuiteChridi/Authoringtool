package controller;

import com.mysql.cj.xdevapi.UpdateResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.AmountJoker;
import model.Database;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserController {

    @FXML
    public Button saveBtn;
    @FXML
    public Button searchBtn;

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Integer> userIdColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> passwordColumn;

    @FXML
    private TableColumn<User,Integer> coinsColumn;

    @FXML
    public TextField password;

    @FXML
    public TextField email;

    @FXML
    public TextField username;

    @FXML
    public TextField userID;

    @FXML
    public TextField searchField;
    @FXML
    public TextField coins;
    @FXML
    public TextField doublePoints;
    @FXML
    public TextField timeStopTxt;
    @FXML
    public TextField fiftyFiftyTxt;
    @FXML
    public VBox vBox;


    public ObservableList<User> userData = FXCollections.observableArrayList();

    User currentSelectedUser;

    Connection connection = Database.getInstance().conn;


    @FXML
    private void initialize() {

        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        coinsColumn.setCellValueFactory(new PropertyValueFactory<>("coins"));

        vBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        userTable.setFocusTraversable(false);
        loadUserData();

    }

    private void loadUserData() {
        userData.clear();
        try {
            String query = "SELECT * FROM user";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int userId = resultSet.getInt("iduser");
                    String username = resultSet.getString("name");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    int coins=resultSet.getInt("coins");


                    userData.add(new User(userId, username, email, password,coins));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Fehler beim Laden der Benutzerdaten.");
        }
        userTable.setItems(userData);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void select(){
        userID.setStyle("-fx-text-fill: black");
        saveBtn.setVisible(false);
      try {
          currentSelectedUser = userTable.getSelectionModel().getSelectedItem();
          userID.setText(String.valueOf(currentSelectedUser.getUserId()));
          username.setText(currentSelectedUser.getUsername());
          email.setText(currentSelectedUser.getEmail());
          password.setText(currentSelectedUser.getPassword());
          coins.setText(String.valueOf(currentSelectedUser.getCoins()));
          loadUserJoker(currentSelectedUser.getUserId());
      }
      catch (RuntimeException v){

      }
    }

    @FXML
    private void newUser(){
        userTable.getSelectionModel().clearSelection();
        userID.setText("Entry Userdata");
        userID.setStyle("-fx-text-fill: black");
        username.clear();
        password.clear();
        email.clear();
        coins.clear();
        fiftyFiftyTxt.clear();
        timeStopTxt.clear();
        doublePoints.clear();
        saveBtn.setVisible(true);
        saveBtn.setStyle("-fx-text-fill: green");
        saveBtn.setText("Add");
    }

    @FXML
    private void edit(){
        userID.setStyle("-fx-text-fill: black");
        saveBtn.setVisible(true);
        saveBtn.setStyle("-fx-text-fill: green");
        saveBtn.setText("Save");
    }
    @FXML
    private void save(){
        if(currentSelectedUser!=null){
            editUserInDb();
        }
       else{
           createUser();
        }
    }

    @FXML
    private void delete(){
        if(userInMessages(currentSelectedUser.getUserId())){
            deleteUserInMessages();
        }
        if(userInJokerTable(currentSelectedUser.getUserId())){
            deleteUserIdInJoker();
        }
        saveBtn.setVisible(false);
        String query= "DELETE FROM user WHERE  iduser=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(userID.getText()));
            preparedStatement.executeUpdate();
            loadUserData();
            userID.setText("Deleted");
            userID.setStyle("-fx-text-fill: red");
            username.clear();
            password.clear();
            email.clear();
            coins.clear();
        } catch (SQLException e) {
            System.err.println("SQL-Fehler: " + e.getMessage());
            e.printStackTrace();
            userID.setText("SQL-Fehler: " + e.getMessage());
            userID.setStyle("-fx-text-fill: red");
        } catch (RuntimeException r){
            userID.setText("No User Selected!");
            userID.setStyle("-fx-text-fill: red");
        }

    }

    private void createUser(){
        String query= "INSERT INTO user (name, password, email,coins) VALUES (?, ?, ?,?)";
        try{
            PreparedStatement preparedStatement =connection.prepareStatement(query);
            preparedStatement.setString(1,username.getText());
            preparedStatement.setString(2,password.getText());
            preparedStatement.setString(3,email.getText());
            preparedStatement.setInt(4, Integer.parseInt(coins.getText()));
            preparedStatement.executeUpdate();
            loadUserData();
            userID.setText("User added");
            userID.setStyle("-fx-text-fill: green");
            saveBtn.setVisible(false);
            username.clear();
            password.clear();
            email.clear();
        }
         catch (SQLException | RuntimeException e) {
             userID.setText("Missing required Information!");
             userID.setStyle("-fx-text-fill: red");
        }
    }

    private void editUserInDb() {
        String query = "UPDATE user SET name=?, email=?, password=?, coins=? WHERE iduser = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username.getText());
            preparedStatement.setString(2, email.getText());
            preparedStatement.setString(3, password.getText());
            preparedStatement.setInt(4, Integer.parseInt(coins.getText()));
            preparedStatement.setInt(5, Integer.parseInt(userID.getText()));
            preparedStatement.executeUpdate();

            userID.setText("Data changed");
            userID.setStyle("-fx-text-fill: green");
            saveBtn.setVisible(false);
            username.clear();
            password.clear();
            email.clear();
            coins.clear();
            loadUserData();

        } catch (SQLException e) {
            userID.setText("Missing required Information!");
            userID.setStyle("-fx-text-fill: red");
        }
    }
    @FXML
    private void searchOrReset(){
        String btnText= searchBtn.getText();
        if(Objects.equals(btnText, "Reset")){
            resetUserTable();
        }
        else{
            search();
        }
    }

    private void  resetUserTable(){
        searchField.clear();
        searchBtn.setText("Search");
        userTable.getItems().clear();
        loadUserData();
    }

    private void search(){
        searchBtn.setText("Reset");
        String searchTerm = searchField.getText().toLowerCase();

        Predicate<User> filterByUsername = user -> user.getUsername().toLowerCase().contains(searchTerm);
        Predicate<User> filterById = user -> String.valueOf(user.getUserId()).contains(searchTerm);

        Predicate<User> combinedFilter = filterByUsername.or(filterById);

        List<User> filteredUsers = userTable.getItems().stream()
                .filter(combinedFilter)
                .collect(Collectors.toList());

        updateTableView(filteredUsers);
    }

    private void updateTableView(List<User> users) {
        userTable.getItems().clear();
        userTable.getItems().addAll(users);
    }

    private void loadUserJoker(int userId){
        String query= "SELECT * FROM joker_of_users WHERE user_id=?";
        try{
            PreparedStatement preparedStatement= connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet= preparedStatement.executeQuery();
            if(resultSet.next()) {
                do {
                    int jokerId = resultSet.getInt("joker_id");
                    int amount = resultSet.getInt("amount");
                    AmountJoker amountJoker = new AmountJoker(userId, jokerId, amount);
                   switch (amountJoker.getJokerIdId()){
                       case 1 -> fiftyFiftyTxt.setText(String.valueOf(amountJoker.getAmount()));
                       case 2 -> timeStopTxt.setText(String.valueOf(amountJoker.getAmount()));
                       case 3 -> doublePoints.setText(String.valueOf(amountJoker.getAmount()));
                   }
                }
                while(resultSet.next());
            }
            else{
                fiftyFiftyTxt.setText("0");
                timeStopTxt.setText("0");
                doublePoints.setText("0");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void changeBtn(){
        String query="SELECT * FROM joker_of_users WHERE user_id=?";
        int counter=0;
        try{
            PreparedStatement preparedStatement= connection.prepareStatement(query);
            preparedStatement.setInt(1, currentSelectedUser.getUserId());
            ResultSet resultSet= preparedStatement.executeQuery();

            if(resultSet.next()){
                do{
                    int jokerId=resultSet.getInt("joker_id");
                    switch (jokerId) {
                        case 1 -> {
                            updateJokerAmount(currentSelectedUser.getUserId(), 1, Integer.parseInt(fiftyFiftyTxt.getText()));
                            counter += 2;
                        }
                        case 2 -> {
                            updateJokerAmount(currentSelectedUser.getUserId(), 2, Integer.parseInt(timeStopTxt.getText()));
                            counter += 3;
                        }
                        case 3 -> {
                            updateJokerAmount(currentSelectedUser.getUserId(), 3, Integer.parseInt(doublePoints.getText()));
                            counter += 4;
                        }
                    }
                }
                while (resultSet.next());
                }
            switch(counter){
                case 2 -> {
                    loadUserIntoAmountOfJoker(currentSelectedUser.getUserId(), 2, Integer.parseInt(timeStopTxt.getText()));
                    loadUserIntoAmountOfJoker(currentSelectedUser.getUserId(), 3, Integer.parseInt(doublePoints.getText()));
                }
                case 3 ->{
                    loadUserIntoAmountOfJoker(currentSelectedUser.getUserId(), 1, Integer.parseInt(fiftyFiftyTxt.getText()));
                    loadUserIntoAmountOfJoker(currentSelectedUser.getUserId(), 3, Integer.parseInt(doublePoints.getText()));
                }
                case 4 ->{
                    loadUserIntoAmountOfJoker(currentSelectedUser.getUserId(), 1, Integer.parseInt(fiftyFiftyTxt.getText()));
                    loadUserIntoAmountOfJoker(currentSelectedUser.getUserId(), 2, Integer.parseInt(timeStopTxt.getText()));
                }
                case 5 -> loadUserIntoAmountOfJoker(currentSelectedUser.getUserId(), 3, Integer.parseInt(doublePoints.getText()));
                case 6-> loadUserIntoAmountOfJoker(currentSelectedUser.getUserId(), 2, Integer.parseInt(timeStopTxt.getText()));
                case 7-> loadUserIntoAmountOfJoker(currentSelectedUser.getUserId(), 1, Integer.parseInt(fiftyFiftyTxt.getText()));
                case 9-> System.out.println("eyyy");
                default -> {
                    loadUserIntoAmountOfJoker(currentSelectedUser.getUserId(), 1, Integer.parseInt(fiftyFiftyTxt.getText()));
                    loadUserIntoAmountOfJoker(currentSelectedUser.getUserId(), 2, Integer.parseInt(timeStopTxt.getText()));
                    loadUserIntoAmountOfJoker(currentSelectedUser.getUserId(), 3, Integer.parseInt(doublePoints.getText()));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        loadUserJoker(currentSelectedUser.getUserId());
    }

    private void updateJokerAmount(int userId, int jokerId, int amount) {
        String query = "UPDATE joker_of_users SET amount=? WHERE user_id=? AND joker_id=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, amount);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, jokerId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadUserIntoAmountOfJoker(int userId,int jokerId, int amount){
        String query="INSERT INTO joker_of_users(user_id, joker_id, amount)VALUES (?,?,?)";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, jokerId);
            preparedStatement.setInt(3, amount);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void deleteUserIdInJoker(){
        String query = "DELETE FROM joker_of_users WHERE user_id=?";
        try{
            PreparedStatement preparedStatement= connection.prepareStatement(query);
            preparedStatement.setInt(1, currentSelectedUser.getUserId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Problem in deleteUSerIdinJoker");
            throw new RuntimeException(e);
        }
    }
    private boolean userInJokerTable(int userId) {
        String query = "SELECT COUNT(*) AS count FROM joker_of_users WHERE user_id=?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            showAlert("Fehler beim Überprüfen des Benutzers in der Joker-Tabelle.");
        }

        return false;
    }
    private boolean userInMessages(int userId) {
        String query = "SELECT COUNT(*) AS count FROM messages WHERE sender_id=? OR receiver_id=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            showAlert("Fehler beim Überprüfen des Benutzers in der Message-Tabelle.");
        }

        return false;
    }
    private void deleteUserInMessages() {
        String query = "DELETE FROM messages WHERE sender_id=? OR receiver_id=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, currentSelectedUser.getUserId());
            preparedStatement.setInt(2, currentSelectedUser.getUserId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Problem in deleteUserInMessage");
            throw new RuntimeException(e);
        }
    }


}
