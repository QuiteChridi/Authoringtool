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
    public TextField categoryField;
    @FXML
    public TextField questionField;
    @FXML
    public TextField correctField;
    @FXML
    public TextField wrong1Field;
    @FXML
    public TextField wrong2Field;
    @FXML
    public TextField wrong3Field;
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

    Connection connection = Database.getInstance().conn;

    ObservableList<String> quizNames= FXCollections.observableArrayList();

    ObservableList<Question> questionData = FXCollections.observableArrayList();

    int selectedQuizId;

    int categoryAmount;






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

        quizTable.setFocusTraversable(false);
        questionTable.setFocusTraversable(false);

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
        selectAllMenuItem.setOnAction(event -> {
            filterQuestionsByQuiz(0);
            quizCategory.setText("Quiz Category");
            categoryField.setText("");
        }
        );
        quizCategory.getItems().add(selectAllMenuItem);
        for(int i=0; i<quizNames.size();i++){
            String quizName=quizNames.get(i);
            MenuItem menuItem= new MenuItem();
            menuItem.setText(quizName);
            quizCategory.getItems().add(menuItem);
            int quizId = i + 1;
            categoryAmount =i;

            menuItem.setOnAction(
                    event -> {
                        selectedQuizId=quizId;
                        filterQuestionsByQuiz(quizId);
                        quizCategory.setText(quizName);
                        categoryField.setText(quizName);
                    }

            );
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

    @FXML
    private void newQuestionInDb(){
        if(selectedQuizId==0){
            newCategoryInDb();
        }
        String query = "INSERT INTO questions (quiz_idQuiz, question, correctAnswer, wrongAnswer1, wrongAnswer2, wrongAnswer3) VALUES (?,?,?,?,?,?)";
        try{
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setInt(1, selectedQuizId);
            preparedStatement.setString(2, questionField.getText());
            preparedStatement.setString(3, correctField.getText());
            preparedStatement.setString(4, wrong1Field.getText());
            preparedStatement.setString(5, wrong2Field.getText());
            preparedStatement.setString(6, wrong3Field.getText());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void newCategoryInDb(){
        String query= "INSERT INTO quiz (name) VALUES (?)";
        try {
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1, categoryField.getText());
            preparedStatement.executeUpdate();

            //Test!! muss um die echte ID ergänzt werden
            selectedQuizId=categoryAmount;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void deleteCategoryAndQuestions(){
        String query="DELETE FROM questions WHERE quiz_IdQuiz=?";
        String query2="DELETE FROM quiz WHERE idQuiz=?";
        try{
            PreparedStatement preparedStatement= connection.prepareStatement(query);
            preparedStatement.setInt(1, selectedQuizId);
            PreparedStatement preparedStatement1=connection.prepareStatement(query2);
            preparedStatement1.setInt(1,selectedQuizId);
            preparedStatement.executeUpdate();
            preparedStatement1.executeUpdate();
            categoryField.setText("DELETED!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
