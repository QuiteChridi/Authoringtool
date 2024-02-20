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
    public Button saveBtn;
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


    Connection connection = Database.getInstance().conn;

    ObservableList<Question> questionData = FXCollections.observableArrayList();

    ObservableList<Quiz> quizData= FXCollections.observableArrayList();

    Question currentSelectedQuestion;

    int selectedQuizId;

    String primaryQuestion;

    Quiz selectedQuiz;



    @FXML
    private void initialize() {
        quizIdColumn.setCellValueFactory(new PropertyValueFactory<>("quizId"));
        questionColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        correctAnswerColumn.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));
        wrongAnswer1Column.setCellValueFactory(new PropertyValueFactory<>("wrongAnswer1"));
        wrongAnswer2Column.setCellValueFactory(new PropertyValueFactory<>("wrongAnswer2"));
        wrongAnswer3Column.setCellValueFactory(new PropertyValueFactory<>("wrongAnswer3"));

        questionTable.setFocusTraversable(false);

        loadQuestionData();
        loadQuizData();
    }

    private void loadQuestionData(){
        questionData.clear();
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
        quizData.clear();
        String query="SELECT * FROM quiz";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet =preparedStatement.executeQuery()) {
            while (resultSet.next()){
                int quizId= resultSet.getInt("idQuiz");
                String quizName= resultSet.getString("name");

                Quiz results = new Quiz(quizId,quizName);
                quizData.add(results);
            }
            addMenuItem();
        } catch (SQLException e) {
            System.out.println("Fehler in Load Quizdata");
        }
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(null);
        alert.setContentText("Fehler beim Laden der QuizDaten.");
        alert.showAndWait();
    }

    private void addMenuItem(){
        quizCategory.getItems().clear();
        MenuItem selectAllMenuItem = new MenuItem("New Categorie");
        selectAllMenuItem.setOnAction(event -> {
            filterQuestionsByQuiz(0);
            quizCategory.setText("New Categorie");
            categoryField.setText("");
            categoryField.setEditable(true);
        }
        );
        quizCategory.getItems().add(selectAllMenuItem);
        for (Quiz quizDatum : quizData) {
            String quizName = quizDatum.getQuizName();
            MenuItem menuItem = new MenuItem();
            menuItem.setText(quizName);
            quizCategory.getItems().add(menuItem);
            int quizId = quizDatum.getQuizIdQuiz();

            menuItem.setOnAction(
                    event -> {
                        categoryField.setStyle("-fx-text-fill: black");
                        selectedQuizId = quizId;
                        filterQuestionsByQuiz(quizId);
                        quizCategory.setText(quizName);
                        categoryField.setText(quizName);
                        categoryField.setEditable(false);
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

    private int newCategoryInDb() {
        String query = "INSERT INTO quiz (name) VALUES (?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, categoryField.getText());
            preparedStatement.executeUpdate();
            categoryField.setEditable(false);
            loadQuizData();
            return getNewestQuizId();
        } catch (SQLException e) {
            System.out.println("newCategoryDidntWork");
            return -1;
        }
    }

    private void newQuestionInDb() {
        if (selectedQuizId == 0) {
            selectedQuizId = newCategoryInDb();
        }
        String query = "INSERT INTO questions (quiz_idQuiz, question, correctAnswer, wrongAnswer1, wrongAnswer2, wrongAnswer3) VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, selectedQuizId);
            preparedStatement.setString(2, questionField.getText());
            preparedStatement.setString(3, correctField.getText());
            preparedStatement.setString(4, wrong1Field.getText());
            preparedStatement.setString(5, wrong2Field.getText());
            preparedStatement.setString(6, wrong3Field.getText());
            preparedStatement.executeUpdate();
            loadQuestionData();

            categoryField.setText("New User!");
            categoryField.setStyle("-fx-text-fill: green");
            saveBtn.setVisible(false);
            questionField.clear();
            correctField.clear();
            wrong1Field.clear();
            wrong2Field.clear();
            wrong3Field.clear();

        } catch (SQLException e) {
            System.out.println("new Question Problem");
        }
    }


    @FXML
    private void deleteQuestions(){
        saveBtn.setVisible(false);
        String query="DELETE FROM questions WHERE quiz_IdQuiz=? AND question=?";
        try{
            int quizId=selectedQuizId;
            PreparedStatement preparedStatement= connection.prepareStatement(query);
            preparedStatement.setInt(1, selectedQuizId);
            preparedStatement.setString(2,questionField.getText());
            preparedStatement.executeUpdate();
            loadQuestionData();
            categoryField.setText("DELETED!");
            categoryField.setStyle("-fx-text-fill: red");
            questionField.clear();
            correctField.clear();
            wrong1Field.clear();
            wrong2Field.clear();
            wrong3Field.clear();
            if(!checkIfCategoryHasQuestion(quizId)){
                deleteQuizCategory();
            }
        } catch (SQLException e) {
            System.out.println("Problem Delete");
        }
    }

    private boolean checkIfCategoryHasQuestion(int quizId){
        String query = "SELECT COUNT(*) AS count FROM questions WHERE quiz_idQuiz=?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, quizId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void deleteQuizCategory(){
        String query2="DELETE FROM quiz WHERE idQuiz=?";
        try{
            PreparedStatement preparedStatement1=connection.prepareStatement(query2);
            preparedStatement1.setInt(1,selectedQuizId);
            preparedStatement1.executeUpdate();
            loadQuizData();
        } catch (SQLException e) {
            System.out.println("Problem Delete QUizCAtegroy");
        }
    }

    @FXML
    private void  select(){
            categoryField.setStyle("-fx-text-fill: black");
            currentSelectedQuestion = questionTable.getSelectionModel().getSelectedItem();
            selectedQuizId=currentSelectedQuestion.getQuizId();
            selectQuiz();
            categoryField.setText(selectedQuiz.getQuizName());
            questionField.setText(currentSelectedQuestion.getQuestion());
            correctField.setText(currentSelectedQuestion.getCorrectAnswer());
            wrong1Field.setText(currentSelectedQuestion.getWrongAnswer1());
            wrong2Field.setText(currentSelectedQuestion.getWrongAnswer2());
            wrong3Field.setText(currentSelectedQuestion.getWrongAnswer3());
            primaryQuestion=currentSelectedQuestion.getQuestion();
    }

    @FXML
    private void edit(){
        categoryField.setStyle("-fx-text-fill: black");
        saveBtn.setVisible(true);
        saveBtn.setStyle("-fx-text-fill: green");
        saveBtn.setText("Save");
    }

    @FXML
    private void save(){
        if(currentSelectedQuestion!=null){
            editQuestionInDb();
        }
        else{
            newQuestionInDb();
        }
    }

    private void  editQuestionInDb(){
        String query="UPDATE questions SET question=?, correctAnswer=?, wrongAnswer1=?, wrongAnswer2=?, wrongAnswer3=? WHERE quiz_idQuiz =? AND question=?";
        try{
            PreparedStatement preparedStatement= connection.prepareStatement(query);
            preparedStatement.setString(1,questionField.getText());
            preparedStatement.setString(2,correctField.getText());
            preparedStatement.setString(3,wrong1Field.getText());
            preparedStatement.setString(4,wrong2Field.getText());
            preparedStatement.setString(5,wrong3Field.getText());

            preparedStatement.setInt(6,selectedQuizId);
            preparedStatement.setString(7,primaryQuestion);
            preparedStatement.executeUpdate();
            loadQuestionData();

            categoryField.setText("Edited!");
            categoryField.setStyle("-fx-text-fill: green");
            saveBtn.setVisible(false);
            questionField.clear();
            correctField.clear();
            wrong1Field.clear();
            wrong2Field.clear();
            wrong3Field.clear();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    private void newQuestion(){
        categoryField.setStyle("-fx-text-fill: black");
        questionTable.getSelectionModel().clearSelection();
        saveBtn.setVisible(true);
        saveBtn.setText("Add");
        saveBtn.setStyle("-fx-text-fill: green");
    }

    private int getNewestQuizId(){
        int highestQuizID=0;
        for(Quiz quiz: quizData){
            if(highestQuizID<quiz.getQuizIdQuiz()){
                highestQuizID=quiz.getQuizIdQuiz();
            }
        }
        return highestQuizID;
    }

    private void selectQuiz(){
        for (Quiz quiz : quizData) {
            if (quiz.getQuizIdQuiz()==(selectedQuizId)) {
                selectedQuiz = quiz;
            }
        }

    }
}
