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

public class UserController {

    @FXML
    public Button saveBtn;
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

    User currentSelectedUser;

    Connection connection = Database.getInstance().conn;





    @FXML
    private void initialize() {

        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        loadUserData();

    }

    private void loadUserData() {
        ObservableList<User> userData = FXCollections.observableArrayList();

        try {
            connection= Database.getInstance().conn;
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
          System.out.println("No User Selected");
      }
    }
    @FXML
    private void newUser(){
        userTable.getSelectionModel().clearSelection();
        userID.setText("Entry Userdata");
        username.setText("");
        email.setText("");
        password.setText("");
        saveBtn.setVisible(true);
        saveBtn.setStyle("-fx-text-fill: green");
        saveBtn.setText("add");
    }

    @FXML
    private void edit(){
        saveBtn.setVisible(true);
        saveBtn.setStyle("-fx-text-fill: green");
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

    }

    private void createUser(){
        String query= "INSERT INTO user (name, password, email) VALUES (?, ?, ?)";
        try{PreparedStatement preparedStatement =connection.prepareStatement(query);
            preparedStatement.setString(1,username.getText());
            preparedStatement.setString(3,email.getText());
            preparedStatement.setString(2,password.getText());
            preparedStatement.executeUpdate();
            userID.setText("Success User added");
        }
         catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void editUserInDb(){

    }

}
