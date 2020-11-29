package com.vadim.geocachingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;

import java.util.HashMap;

public class GameSelectActivity extends AppCompatActivity {

    public static final String EXTRA_GAME =
            "gameselectactivity.EXTRA_GAME";
    public static final String EXTRA_CODE =
            "gameselectactivity.EXTRA_CODE";

    private ListView listView;
    private HashMap<String, GeoGame> games;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        games = GameIO.getGames(getApplicationContext());
        setContentView(R.layout.activity_game_select);
        fillList();
    }

    private void downloadGame(String name) {
        ApiConnector apiConnector = new ApiConnector(getApplicationContext());
        apiConnector.requestGame(new ApiConnector.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                games = GameIO.getGames(getApplicationContext());
                fillList();
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getApplicationContext()
                        ,"Spiel nicht gefunden"
                        ,Toast.LENGTH_SHORT).show();
            }
        }, name);
    }

    private void fillList() {
        this.listView = findViewById(R.id.listview_game_select);
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        games.keySet().toArray(new String[0]));
        this.listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                games = GameIO.getGames(getApplicationContext());
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);

                // TODO: read correct code
                intent.putExtra(EXTRA_GAME, games.get("test").toSting());
                intent.putExtra(EXTRA_CODE, "test");
                startActivity(intent);
            }
        });
    }

    public void showGameDialog(@Nullable View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Spiel hinzufügen");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadGame(input.getText().toString());
            }
        });
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
