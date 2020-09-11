package com.vadim.geocachingapp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GeoGames implements Serializable {
    public Map<String, GeoGame> idToGame;

    public GeoGames(Map<String, GeoGame> idToGame) {
        this.idToGame = idToGame;
    }

    public GeoGames() {
        idToGame = new HashMap<String, GeoGame>();
    }

    public GeoGame getGame(String id) {
        return idToGame.get(id);
    }

    public void addGame(String id, GeoGame game) {
        idToGame.put(id, game);
    }

}