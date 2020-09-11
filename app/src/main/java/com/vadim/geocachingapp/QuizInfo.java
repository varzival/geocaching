package com.vadim.geocachingapp;

import com.google.gson.Gson;

import java.io.Serializable;

public class QuizInfo implements Serializable {

    public String quizText;
    public String[] options;
    public int correct;

    public QuizInfo(String quizText, String[] options, int correct)
    {
        this.quizText = quizText;
        this.options = options;
        this.correct = correct;
    }

    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }
}
