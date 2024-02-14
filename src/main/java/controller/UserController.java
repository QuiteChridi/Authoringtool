package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Database;
import model.Question;
import model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {



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
    private Button edit;
    @FXML
    private Button question;




    @FXML
    private void initialize() {

        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        loadUserData();

    }
    @FXML
    private void onEditButtonClick(){
        try {
            Stage stage = (Stage) question.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/view/UserEditDialog.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/css/stylesheet.css");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onQuestionButtonClick(){
        try {
            Stage stage = (Stage) edit.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/view/question.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/css/stylesheet.css");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserData() {
        ObservableList<User> userData = FXCollections.observableArrayList();

        try {
            Connection connection = Database.getInstance().conn;
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

}
