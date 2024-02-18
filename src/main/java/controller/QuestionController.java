package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Database;
import model.Question;
import model.Quiz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class QuestionController {

    @FXML
    public TableView<Question> questionTable;
    @FXML
    public MenuButton quizCategory;
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
    private TableView<Quiz> quizTable;

    @FXML
    private TableColumn<Quiz, Integer> quizIdColumnQuiz;

    @FXML
    private TableColumn<Quiz, String> quizNameColumn;

    int selectedQuiz =1;

    Connection connection = Database.getInstance().conn;

    ObservableList<String> quizNames= FXCollections.observableArrayList();

    ObservableList<Question> questionData = FXCollections.observableArrayList();






    @FXML
    private void initialize() {
        quizIdColumn.setCellValueFactory(new PropertyValueFactory<>("quizId"));
        questionColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        correctAnswerColumn.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));
        wrongAnswer1Column.setCellValueFactory(new PropertyValueFactory<>("wrongAnswer1"));
        wrongAnswer2Column.setCellValueFactory(new PropertyValueFactory<>("wrongAnswer2"));
        wrongAnswer3Column.setCellValueFactory(new PropertyValueFactory<>("wrongAnswer3"));

        quizNameColumn.setCellValueFactory(new PropertyValueFactory<>("quizName"));
        quizIdColumnQuiz.setCellValueFactory(new PropertyValueFactory<>("quizIdQuiz"));

        loadQuestionData();
        loadQuizData();
        addMenuItem();
    }

    private void loadQuestionData(){
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

                    questionData.add(results);
                }
            }
            catch (SQLException e) {
            e.printStackTrace();
            showAlert();
            }
        questionTable.setItems(questionData);
    }

    private void loadQuizData(){
        ObservableList<Quiz> quizData= FXCollections.observableArrayList();
        String query="SELECT * FROM quiz";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet =preparedStatement.executeQuery()) {
            while (resultSet.next()){
                int quizId= resultSet.getInt("idQuiz");
                String quizName= resultSet.getString("name");

                quizNames.add(quizName);
                Quiz results = new Quiz(quizId,quizName);
                quizData.add(results);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        quizTable.setItems(quizData);
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(null);
        alert.setContentText("Fehler beim Laden der QuizDaten.");
        alert.showAndWait();
    }

    private void addMenuItem(){
        MenuItem selectAllMenuItem = new MenuItem("Select All");
        selectAllMenuItem.setOnAction(event -> filterQuestionsByQuiz(0));
        quizCategory.getItems().add(selectAllMenuItem);
        for(int i=0; i<quizNames.size();i++){
            MenuItem menuItem= new MenuItem();
            menuItem.setText(quizNames.get(i));
            quizCategory.getItems().add(menuItem);
            int quizId = i + 1;

            menuItem.setOnAction(event -> filterQuestionsByQuiz(quizId));
        }

    }

    private void filterQuestionsByQuiz(int selectedId) {
        ObservableList<Question> filteredQuestions = FXCollections.observableArrayList();
        if (selectedId == 0) {
            questionTable.setItems(questionData);
        } else {
            for (Question question : questionData) {
                if (question.getQuizId() == selectedId) {
                    filteredQuestions.add(question);
                }
            }
            questionTable.setItems(filteredQuestions);
        }
    }
}
