package com.vadim.geocachingapp;

import android.util.Pair;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GeoGame implements Serializable {

    public static class QuizGameInfo{
        public QuizInfo quizInfo;
        public boolean won;

        public QuizGameInfo(QuizInfo quizInfo, boolean won) {
            this.quizInfo = quizInfo;
            this.won = won;
        }
    }

    public Map<Pair<Double, Double>, QuizGameInfo> pointQuizDict;

    public GeoGame(Map<Pair<Double, Double>, QuizGameInfo> pointQuizDict) {
        this.pointQuizDict = pointQuizDict;
    }

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
}

