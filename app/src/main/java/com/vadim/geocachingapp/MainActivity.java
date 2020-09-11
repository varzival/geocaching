package com.vadim.geocachingapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_QUIZ
            = "com.example.android.twoactivities.extra.QUIZ";
    // Unique tag for the intent reply
    public static final int TEXT_REQUEST = 1;

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map;

    private final double clickableDistance = 10;

    private class GPSLocationChangeProvider extends GpsMyLocationProvider {
        private List<CustomMarker> markers;

        public GPSLocationChangeProvider(Context ctx, List<CustomMarker> markers) {
            super(ctx);
            this.markers = markers;
        }

        @Override
        public void onLocationChanged(Location location) {
            super.onLocationChanged(location);

            // update icons
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            GeoPoint my_point = new GeoPoint(lat, lon);

            for (CustomMarker marker : markers) {
                double distance = marker.getPosition().distanceToAsDouble(my_point);
                if (distance <= clickableDistance) {
                    marker.setClickable();
                } else {
                    marker.setPOI();
                }
            }
        }
    }

    private class CustomMarker extends Marker {

        public QuizInfo quiz;

        private final Marker.OnMarkerClickListener POIListener = new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                Toast.makeText(map.getContext()
                        , "Komm näher!"
                        , Toast.LENGTH_SHORT).show();
                return true;
            }
        };

        private final Marker.OnMarkerClickListener clickableListener = new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {

                Intent intent = new Intent(getApplicationContext(), QuizActivity.class);

                intent.putExtra(EXTRA_QUIZ, quiz);
                startActivityForResult(intent, TEXT_REQUEST);

                return true;
            }
        };


        public CustomMarker(MapView mapView, GeoPoint position, QuizInfo quiz) {
            super(mapView);

            this.quiz = quiz;

            this.setPosition(position);
            this.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            this.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_point_of_interest));
            this.setOnMarkerClickListener(POIListener);
        }

        public void setClickable() {
            this.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_point_clickable));
            this.setOnMarkerClickListener(clickableListener);
        }

        public void setPOI() {
            this.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_point_of_interest));
            this.setOnMarkerClickListener(POIListener);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Test for the right intent reply.
        if (requestCode == TEXT_REQUEST) {
            // Test to make sure the intent reply result was good.
            if (resultCode == RESULT_OK) {
                boolean reply = data.getBooleanExtra(QuizActivity.EXTRA_REPLY, false);
                String replyText = reply ? "Richtig" : "Falsch";
                Toast.makeText(map.getContext()
                        , replyText
                        , Toast.LENGTH_SHORT).show();
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

        // TODO remove for production
        GeoGame game = new GeoGame();

        String[] options = {"Korrekt", "Falsch", "Falsch"};

        QuizInfo quiz = new QuizInfo(
                "Quiz Text", options, 0
        );

        Random rnd = new Random();
        for (int i = 0; i < 10; i++) {
            game.addQuiz(37 + rnd.nextFloat() * 5, -8 + rnd.nextFloat() * 5, quiz);
        }
        // my loc 40.3808, -3.6777
        game.addQuiz(40.3808, -3.6777, quiz);

        GeoGames games = new GeoGames();
        games.addGame("test", game);
        String jsonStr = new Gson().toJson(games);
        File file = new File(ctx.getFilesDir(), getString(R.string.games_filename));
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(jsonStr);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO end

        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = ctx.openFileInput(getString(R.string.games_filename));
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new GsonBuilder()
                        .registerTypeAdapter(GeoGames.class, new GamesDeserializer())
                        .create();
        games = gson.fromJson(sb.toString(), GeoGames.class);
        game = games.getGame("test");

        List < CustomMarker > markers = new LinkedList<>();
        for (Pair<Double, Double> latLonPair : game.pointQuizDict.keySet()) {
            GeoPoint point = new GeoPoint(latLonPair.first, latLonPair.second);
            CustomMarker marker = new CustomMarker(map, point, quiz);
            map.getOverlays().add(marker);
            markers.add(marker);
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
                    map.getController().zoomTo(6);
                    map.zoomToBoundingBox(zoomToBox, true);
                }
            }
        });

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