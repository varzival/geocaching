package com.vadim.geocachingapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;

import java.util.HashMap;

public class GameSelectActivity extends AppCompatActivity {

    private ListView listView;
    private HashMap<String, GeoGame> games;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_select);

        ApiConnector apiConnector = new ApiConnector(getApplicationContext());
        apiConnector.requestGame(new ApiConnector.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                games = GameIO.getGames(getApplicationContext());
                fillList();
            }

            @Override
            public void onError(VolleyError error) {
                Log.d("apicall", "Error");
            }
        }, "test");
    }

    private void fillList() {
        this.listView = findViewById(R.id.listview_game_select);
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        games.keySet().toArray(new String[0]));
        this.listView.setAdapter(arrayAdapter);
    }
}
