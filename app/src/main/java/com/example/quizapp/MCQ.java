package com.example.quizapp;

public class MCQ {
    String question;
    String[] options;
    int correctAns;
    int optAnswered;
    boolean correctAnsBtnSelection;

    public MCQ(String question, String[] options, int correctAns) {
        this.question = question;
        this.options = options;
        this.correctAns = correctAns;
        this.optAnswered = -1;
        this.correctAnsBtnSelection = false;
    }
}
