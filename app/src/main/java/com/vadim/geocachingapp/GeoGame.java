package com.vadim.geocachingapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class GeoGame implements Serializable {

    public static class QuizGameInfo implements Serializable {
        public QuizInfo quizInfo;
        public boolean won;
        GeoPoint geoPoint;

        public QuizGameInfo(QuizInfo quizInfo, boolean won, GeoPoint geoPoint) {
            this.quizInfo = quizInfo;
            this.won = won;
            this.geoPoint = geoPoint;
        }
    }

    public LinkedList<QuizGameInfo> quizList;
    public String name;

    public List<GeoPoint> getPoints()
    {
        List<GeoPoint> points = new LinkedList<>();
        for (QuizGameInfo qgi: quizList)
        {
            points.add(qgi.geoPoint);
        }
        return points;
    }

    public GeoGame() {
        this.quizList = new LinkedList<>();
        this.name = "";
    }

    public GeoGame(String name) {
        this.quizList = new LinkedList<>();
        this.name = name;
    }

    public QuizInfo getQuiz(int quizIndex)
    {
        QuizGameInfo quiz = quizList.get(quizIndex);
        return quiz.quizInfo;
    }

    public boolean getWon(int quizIndex)
    {
        QuizGameInfo quiz = quizList.get(quizIndex);
        return quiz.won;
    }

    public void setWon(int quizIndex)
    {
        QuizGameInfo quiz = quizList.get(quizIndex);
        quiz.won = true;
    }

    public QuizGameInfo addQuiz(double lat, double lon, QuizInfo quiz, boolean won)
    {
        QuizGameInfo qgi = new QuizGameInfo(quiz, won, new GeoPoint(lat, lon));
        quizList.add(qgi);
        return qgi;
    }

    public String toSting() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeoGame.class, new GamesDeserializer())
                .registerTypeAdapter(GeoGame.class, new GamesSerializer())
                .create();
        return gson.toJson(this);
    }

    public static GeoGame fromString(String str) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeoGame.class, new GamesDeserializer())
                .registerTypeAdapter(GeoGame.class, new GamesSerializer())
                .create();
        return gson.fromJson(str, GeoGame.class);
    }
}

