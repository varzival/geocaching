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
        JsonObject fullInfo = json.getAsJsonObject();
        game.name = fullInfo.get("name").getAsString();

        for (JsonElement quizEl: fullInfo.get("quizes").getAsJsonArray())
        {
            JsonObject quizObj = quizEl.getAsJsonObject();
            double lat = quizObj.get("lat").getAsDouble();
            double lon = quizObj.get("lon").getAsDouble();
            boolean won;
            if (fullInfo.has("won"))
                won = fullInfo.get("won").getAsBoolean();
            else
                won = false;
            String text = quizObj.get("text").getAsString();
            int correct = quizObj.get("correct").getAsInt();
            JsonArray optionsArray = quizObj.get("options").getAsJsonArray();
            LinkedList<String> options = new LinkedList<>();
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
