package com.vadim.geocachingapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    public static final int TEXT_REQUEST = 1;

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map;
    private GeoGame game;
    private List <CustomMarker> markers;
    private ApiConnector connector;

    //TODO remove for production
    private boolean gameCreated = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Test for the right intent reply.
        if (requestCode == TEXT_REQUEST) {
            // Test to make sure the intent reply result was good.
            if (resultCode == RESULT_OK) {
                boolean correct = data.getBooleanExtra(QuizActivity.EXTRA_CORRECT, false);
                GeoPoint point = (GeoPoint) data.getSerializableExtra(QuizActivity.EXTRA_GEOPOINT);
                String replyText = correct ? "Richtig" : "Falsch";
                Toast.makeText(map.getContext()
                        , replyText
                        , Toast.LENGTH_SHORT).show();
                if (correct && point != null)
                {
                    game.setWon(point.getLatitude(), point.getLongitude());
                    for (CustomMarker marker : markers)
                    {
                        if (marker.position.getLatitude() == point.getLatitude()
                                && marker.position.getLongitude() == point.getLongitude())
                            {
                                marker.setVisited();
                                break;
                            }
                    }
                }
                else if (!correct && point != null)
                {
                    for (CustomMarker marker : markers)
                    {
                        if (marker.position.getLatitude() == point.getLatitude()
                                && marker.position.getLongitude() == point.getLongitude())
                        {
                            marker.setLocked();
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        connector = new ApiConnector(ctx);

        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.osm_main);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        if (!gameCreated)
        {
            // TODO remove for production
            GeoGame game = new GeoGame();

            String[] options = {"Korrekt", "Falsch", "Falsch"};

            QuizInfo quiz = new QuizInfo(
                    "Quiz Text", options, 0
            );

            /*
            Random rnd = new Random();
            for (int i = 0; i < 10; i++) {
                game.addQuiz(37 + rnd.nextFloat() * 5, -8 + rnd.nextFloat() * 5, quiz);
            }
            */
            // my loc 49.374907, 8.682536
            game.addQuiz(49.374907, 8.682536, quiz, false);
            game.addQuiz(49.371920, 8.682804, quiz, false);
            game.addQuiz(49.369059, 8.683040, quiz, false);
            game.addQuiz(49.371992, 8.684476, quiz, false);

            HashMap<String, GeoGame> games = new HashMap<>();
            games.put("test", game);
            GameIO.writeGames(ctx, games);
            gameCreated = true;
            //TODO end
        }

        HashMap<String, GeoGame> games = GameIO.getGames(ctx);
        game = games.get("test");

        markers = new LinkedList<>();
        for (Pair<Double, Double> latLonPair : game.pointQuizDict.keySet()) {
            GeoPoint point = new GeoPoint(latLonPair.first, latLonPair.second);
            QuizInfo quiz = game.getQuiz(latLonPair.first, latLonPair.second);
            boolean won = game.getWon(latLonPair.first, latLonPair.second);
            CustomMarker marker = new CustomMarker(this, map, point, quiz);
            map.getOverlays().add(marker);
            markers.add(marker);
            if (won)
            {
                marker.setVisited();
            }
        }

        GpsMyLocationProvider locationProvider = new GPSLocationChangeProvider(ctx, markers);
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(locationProvider, map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        List<GeoPoint> points = new LinkedList<>(game.getPoints());
        // zoom to its bounding box
        final BoundingBox zoomToBox = BoundingBox.fromGeoPoints(points).increaseByScale(1.5f);
        map.addOnFirstLayoutListener(new MapView.OnFirstLayoutListener() {

            @Override
            public void onFirstLayout(View v, int left, int top, int right, int bottom) {
                if (map != null && map.getController() != null) {
                    map.getController().zoomTo(19);
                    map.zoomToBoundingBox(zoomToBox, true);
                }
            }
        });

        map.setMultiTouchControls(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest
                = new ArrayList<>(Arrays.asList(permissions).subList(0, grantResults.length));
        if (permissionsToRequest.size() > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            // TODO display failure
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}