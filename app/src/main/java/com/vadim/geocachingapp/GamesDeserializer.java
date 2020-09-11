package com.vadim.geocachingapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GamesDeserializer implements JsonDeserializer<GeoGames>
{
    @Override
    public GeoGames deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException
    {
        JsonObject obj = json.getAsJsonObject();

        GeoGames games = new GeoGames();
        JsonObject idToGame = obj.get("idToGame").getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = idToGame.entrySet();
        for(Map.Entry<String, JsonElement> entry: entries)
        {
            GeoGame game = new GeoGame();
            Set<Map.Entry<String, JsonElement>> quizes =
                    entry.getValue().getAsJsonObject().get("pointQuizDict").getAsJsonObject().entrySet();
            for(Map.Entry<String, JsonElement> quizEntry: quizes)
            {
                String pairStr = quizEntry.getKey();
                String[] spl = pairStr.split("[{} ]");
                boolean won = quizEntry.getValue().getAsJsonObject().get("won").getAsBoolean();
                JsonObject quizInfoJsonObject = quizEntry.getValue().getAsJsonObject().get("quizInfo").getAsJsonObject();
                int correct = quizInfoJsonObject.get("correct").getAsInt();
                JsonArray arr = quizInfoJsonObject.get("options").getAsJsonArray();
                List<String> list = new ArrayList<String>();
                for(int i = 0; i < arr.size(); i++){
                    list.add(arr.get(i).getAsString());
                }
                String[] options = list.toArray(new String[0]);
                String quizText = quizInfoJsonObject.get("quizText").getAsString();
                QuizInfo info = new QuizInfo(quizText, options, correct);
                GeoGame.QuizGameInfo quizGameInfo = game.addQuiz(Double.parseDouble(spl[1]), Double.parseDouble(spl[2]), info);
                quizGameInfo.won = won;
                quizEntry.getValue();
            }
            games.addGame(entry.getKey(), game);
        }
        return games;
    }
}
