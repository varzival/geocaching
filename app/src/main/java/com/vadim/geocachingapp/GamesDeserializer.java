package com.vadim.geocachingapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.LinkedList;

public class GamesDeserializer implements JsonDeserializer<GeoGame>
{
    @Override
    public GeoGame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException
    {
        GeoGame game = new GeoGame();
        JsonArray arr = json.getAsJsonArray();
        for (JsonElement el : arr)
        {
            JsonObject fullInfo = el.getAsJsonObject();
            double lat = fullInfo.get("lat").getAsDouble();
            double lon = fullInfo.get("lon").getAsDouble();
            boolean won = fullInfo.get("won").getAsBoolean();
            String text = fullInfo.get("text").getAsString();
            int correct = fullInfo.get("correct").getAsInt();
            JsonArray optionsArray = fullInfo.get("options").getAsJsonArray();
            LinkedList<String> options = new LinkedList<String>();
            for (JsonElement optionEl : optionsArray)
            {
                options.add(optionEl.getAsString());
            }
            QuizInfo quiz = new QuizInfo(text, options.toArray(new String[0]), correct);
            game.addQuiz(lat, lon, quiz, won);
        }
        return game;
    }
}
