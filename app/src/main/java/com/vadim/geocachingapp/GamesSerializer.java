package com.vadim.geocachingapp;

import android.util.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;

public class GamesSerializer implements JsonSerializer<GeoGame> {

    @Override
    public JsonElement serialize(GeoGame src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject fullObject = new JsonObject();
        fullObject.addProperty("name", src.name);
        JsonArray quizArray = new JsonArray();
        for (Map.Entry<Pair<Double, Double>, GeoGame.QuizGameInfo> entry: src.pointQuizDict.entrySet())
        {
            JsonObject obj = new JsonObject();
            obj.addProperty("lat", entry.getKey().first);
            obj.addProperty("lon", entry.getKey().second);
            obj.addProperty("won", entry.getValue().won);

            QuizInfo info =  entry.getValue().quizInfo;
            obj.addProperty("text", info.quizText);
            obj.addProperty("correct", info.correct);
            JsonArray optionsArray = new JsonArray();
            for (String option : info.options)
            {
                optionsArray.add(option);
            }
            obj.add("options", optionsArray);

            quizArray.add(obj);
        }
        fullObject.add("quizes", quizArray);
        return fullObject;
    }
}
