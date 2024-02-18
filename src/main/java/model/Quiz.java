package model;

public class Quiz {

    private int quizIdQuiz;

    private String quizName;

    public Quiz(int quizIdQuiz, String quizName){
        this.quizName=quizName;
        this.quizIdQuiz = quizIdQuiz;
    }


    public int getQuizIdQuiz() {
        return quizIdQuiz;
    }

    public String getQuizName() {
        return quizName;
    }


    public void setQuizIdQuiz(int quizIdQuiz) {
        this.quizIdQuiz = quizIdQuiz;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }


}
