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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuestionController {

    @FXML
    public TableView<Question> questionTable;
    @FXML
    private TableColumn<Question, Integer> quizIdColumn;

    @FXML
    private TableColumn<Question, String> questionColumn;

    @FXML
    private TableColumn<Question, String> correctAnswerColumn;

    @FXML
    private TableColumn<Question, String> wrongAnswer1Column;

    @FXML
    private TableColumn<Question, String> wrongAnswer2Column;

    @FXML
    private TableColumn<Question, String> wrongAnswer3Column;

    @FXML
    private Button edit;
    @FXML
    private Button user;

    @FXML
    private void onEditButtonClick(){
        try {
            Stage stage = (Stage) edit.getScene().getWindow();
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
    private void onUserButtonClick(){
        try {
            Stage stage = (Stage) user.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/view/user.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/css/stylesheet.css");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void initialize() {


        quizIdColumn.setCellValueFactory(new PropertyValueFactory<>("quizId"));
        questionColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        correctAnswerColumn.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));
        wrongAnswer1Column.setCellValueFactory(new PropertyValueFactory<>("wrongAnswer1"));
        wrongAnswer2Column.setCellValueFactory(new PropertyValueFactory<>("wrongAnswer2"));
        wrongAnswer3Column.setCellValueFactory(new PropertyValueFactory<>("wrongAnswer3"));


        loadQuestionData();
    }

    private void loadQuestionData(){
        ObservableList<Question> questionData = FXCollections.observableArrayList();
        try {
            Connection connection= Database.getInstance().conn;
            String query= "SELECT * FROM questions";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {


                while (resultSet.next()) {
                    int quizId = resultSet.getInt("quiz_idQuiz");
                    String question = resultSet.getString("question");
                    String correctAnswer = resultSet.getString("correctAnswer");
                    String wrongAnswer1 = resultSet.getString("wrongAnswer1");
                    String wrongAnswer2 = resultSet.getString("wrongAnswer2");
                    String wrongAnswer3 = resultSet.getString("wrongAnswer3");

                    Question results= new Question(quizId, question,correctAnswer,wrongAnswer1,wrongAnswer2,wrongAnswer3);
                    System.out.println(results);


                    questionData.add(results);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            showAlert();
        }
        questionTable.setItems(questionData);
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(null);
        alert.setContentText("Fehler beim Laden der QuizDaten.");
        alert.showAndWait();
    }
}
