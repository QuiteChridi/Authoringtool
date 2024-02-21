package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    public TextField password;

    @FXML
    public TextField email;

    @FXML
    public TextField username;

    @FXML
    public TextField userID;

    @FXML
    public TextField searchField;

    public ObservableList<User> userData = FXCollections.observableArrayList();

    User currentSelectedUser;

    Connection connection = Database.getInstance().conn;


    @FXML
    private void initialize() {

        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        userTable.setFocusTraversable(false);
        loadUserData();

    }

    private void loadUserData() {
        try {
            String query = "SELECT * FROM user";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int userId = resultSet.getInt("iduser");
                    String username = resultSet.getString("name");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");


                    userData.add(new User(userId, username, email, password));
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
      }
      catch (RuntimeException v){
          userID.setText("No User Selected!");
          userID.setStyle("-fx-text-fill: red");
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
        } catch (SQLException e) {
            System.out.println("Catched aber nicht catched");
            userID.setText("No User Selected!");
            userID.setStyle("-fx-text-fill: red");
        } catch (RuntimeException r){
            userID.setText("No User Selected!");
            userID.setStyle("-fx-text-fill: red");
        }

    }

    private void createUser(){
        String query= "INSERT INTO user (name, password, email) VALUES (?, ?, ?)";
        try{
            PreparedStatement preparedStatement =connection.prepareStatement(query);
            preparedStatement.setString(1,username.getText());
            preparedStatement.setString(2,password.getText());
            preparedStatement.setString(3,email.getText());
            preparedStatement.executeUpdate();
            loadUserData();
            userID.setText("User added");
            userID.setStyle("-fx-text-fill: green");
            saveBtn.setVisible(false);
            username.clear();
            password.clear();
            email.clear();
        }
         catch (SQLException e) {
             userID.setText("Missing required Information!");
             userID.setStyle("-fx-text-fill: red");
        }
        catch(RuntimeException r){
            userID.setText("Missing required Information!");
            userID.setStyle("-fx-text-fill: red");
        }
    }

    private void editUserInDb() {
        String query = "UPDATE user SET name=?, email=?, password=? WHERE iduser = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username.getText());
            preparedStatement.setString(2, email.getText());
            preparedStatement.setString(3, password.getText());
            preparedStatement.setInt(4, Integer.parseInt(userID.getText()));
            preparedStatement.executeUpdate();

            userID.setText("Data changed");
            userID.setStyle("-fx-text-fill: green");
            saveBtn.setVisible(false);
            username.clear();
            password.clear();
            email.clear();
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
}
