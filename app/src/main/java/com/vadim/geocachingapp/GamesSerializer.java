package com.vadim.geocachingapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class GamesSerializer implements JsonSerializer<GeoGame> {

    @Override
    public JsonElement serialize(GeoGame src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject fullObject = new JsonObject();
        fullObject.addProperty("name", src.name);
        JsonArray quizArray = new JsonArray();
        for (GeoGame.QuizGameInfo qgi: src.quizList)
        {
            JsonObject obj = new JsonObject();
            obj.addProperty("lat", qgi.geoPoint.getLatitude());
            obj.addProperty("lon", qgi.geoPoint.getLongitude());
            obj.addProperty("won", qgi.won);

            QuizInfo info =  qgi.quizInfo;
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
