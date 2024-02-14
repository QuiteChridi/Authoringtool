package model;

public class Question {

    private int quizId;

    private String question;

    private String correctAnswer;

    private String wrongAnswer1;

    private String wrongAnswer2;

    private String wrongAnswer3;

    public Question(int quizId, String question, String correctAnswer, String wrongAnswer1, String wrongAnswer2, String wrongAnswer3){
        this.quizId= quizId;
        this.question=question;
        this.correctAnswer=correctAnswer;
        this.wrongAnswer1=wrongAnswer1;
        this.wrongAnswer2=wrongAnswer2;
        this.wrongAnswer3=wrongAnswer3;
    }


    public int getQuizId() {
        return quizId;
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getWrongAnswer1() {
        return wrongAnswer1;
    }

    public String getWrongAnswer2() {
        return wrongAnswer2;
    }

    public String getWrongAnswer3() {
        return wrongAnswer3;
    }


    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setWrongAnswer1(String wrongAnswer1) {
        this.wrongAnswer1 = wrongAnswer1;
    }

    public void setWrongAnswer2(String wrongAnswer2) {
        this.wrongAnswer2 = wrongAnswer2;
    }

    public void setWrongAnswer3(String wrongAnswer3) {
        this.wrongAnswer3 = wrongAnswer3;
    }
}
