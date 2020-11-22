package com.vadim.geocachingapp;

import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GeoGame implements Serializable {

    public static class QuizGameInfo implements Serializable {
        public QuizInfo quizInfo;
        public boolean won;

        public QuizGameInfo(QuizInfo quizInfo, boolean won) {
            this.quizInfo = quizInfo;
            this.won = won;
        }
    }

    public Map<Pair<Double, Double>, QuizGameInfo> pointQuizDict;
    public String name;

    public Set<GeoPoint> getPoints()
    {
        Set<GeoPoint> points = new HashSet<GeoPoint>();
        for (Pair<Double, Double> latLonPair: pointQuizDict.keySet())
        {
            points.add(new GeoPoint(latLonPair.first, latLonPair.second));
        }
        return points;
    }

    public GeoGame() {
        this.pointQuizDict = new HashMap<>();
        this.name = "";
    }

    public GeoGame(String name) {
        this.pointQuizDict = new HashMap<>();
        this.name = name;
    }

    public QuizInfo getQuiz(double lat, double lon)
    {
        QuizGameInfo quiz = pointQuizDict.get(new Pair<Double, Double>(lat, lon));
        if (quiz == null)
        {
            return null;
        }
        else{
            return quiz.quizInfo;
        }
    }

    public boolean getWon(double lat, double lon)
    {
        QuizGameInfo quiz = pointQuizDict.get(new Pair<Double, Double>(lat, lon));
        if (quiz == null)
        {
            return false;
        }
        else{
            return quiz.won;
        }
    }

    public void setWon(double lat, double lon)
    {
        QuizGameInfo quiz = pointQuizDict.get(new Pair<Double, Double>(lat, lon));
        if (quiz != null)
        {
            quiz.won = true;
        }
    }

    public QuizGameInfo addQuiz(double lat, double lon, QuizInfo quiz, boolean won)
    {
        QuizGameInfo qgi = new QuizGameInfo(quiz, won);
        pointQuizDict.put(new Pair<Double, Double>(lat, lon), qgi);
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

