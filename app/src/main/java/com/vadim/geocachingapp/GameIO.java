package com.vadim.geocachingapp;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;

public class GameIO {

    public static HashMap<String, GeoGame> getGames(Context ctx)
    {
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = ctx.openFileInput(ctx.getResources().getString(R.string.games_filename));
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeoGame.class, new GamesDeserializer())
                .registerTypeAdapter(GeoGame.class, new GamesSerializer())
                .create();
        Type type = new TypeToken<HashMap<String, GeoGame>>(){}.getType();
        return gson.fromJson(sb.toString(), type);
    }

    public static void writeGames(Context ctx, HashMap<String, GeoGame> games)
    {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GeoGame.class, new GamesDeserializer())
                .registerTypeAdapter(GeoGame.class, new GamesSerializer())
                .create();
        String jsonStr = gson.toJson(games);
        File file = new File(ctx.getFilesDir(), ctx.getResources().getString(R.string.games_filename));
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(jsonStr);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeGame(Context ctx, String code, GeoGame game)
    {
        HashMap<String, GeoGame> games = getGames(ctx);
        games.put(code, game);
        writeGames(ctx, games);
    }
}
