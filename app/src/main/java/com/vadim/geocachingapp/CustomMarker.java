package com.vadim.geocachingapp;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.Serializable;
import java.util.Date;

public class CustomMarker extends Marker {

    public static final String EXTRA_QUIZ
            = "mapactivity.QUIZ";
    public static final String EXTRA_GEOPOINT
            = "mapactivity.GEOPOINT";
    public static final int TEXT_REQUEST = 1;

    public QuizInfo quiz;
    public GeoPoint position;
    final double waitTime = 10;
    private double lockTime = 0.0;
    public boolean visited = false;
    private Activity activity;

    private final Marker.OnMarkerClickListener POIListener = new Marker.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker, MapView mapView) {
            Toast.makeText(activity.getApplicationContext()
                    , "Komm näher!"
                    , Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    private final Marker.OnMarkerClickListener clickableListener = new Marker.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker, MapView mapView) {

            Intent intent = new Intent(activity.getApplicationContext(), QuizActivity.class);

            intent.putExtra(EXTRA_QUIZ, quiz);
            intent.putExtra(EXTRA_GEOPOINT, (Serializable) position);
            activity.startActivityForResult(intent, TEXT_REQUEST);

            return true;
        }
    };

    private final Marker.OnMarkerClickListener VisitedListener = new Marker.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker, MapView mapView) {
            Toast.makeText(activity.getApplicationContext()
                    , "Du hast diesen Punkt bereits besucht."
                    , Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    private final Marker.OnMarkerClickListener LockedListener = new Marker.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker, MapView mapView) {
            double timeLeft = getTimeLockLeft();
            int timeLeftInt = (int)Math.floor(timeLeft);
            String sec = timeLeftInt == 1 ? "Sekunde" : "Sekunden";
            Toast.makeText(activity.getApplicationContext()
                    , "Punkt gesperrt. Warte noch "+ timeLeftInt + " " + sec + "."
                    , Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    Thread lockResetThread = new Thread(){
        public void run(){
            while(getTimeLockLeft() > 0.0)
            { }
            setPOI();
        }
    };


    public CustomMarker(Activity activity, MapView mapView, GeoPoint position, QuizInfo quiz) {
        super(mapView);

        this.activity = activity;
        this.quiz = quiz;
        this.position = position;

        this.setPosition(position);
        this.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        setPOI();
    }

    public double getTimeLockLeft()
    {
        if (lockTime == 0.0)
        {
            return 0.0;
        }
        double ret = (waitTime * 1000 - ((new Date()).getTime() - lockTime))/1000.0;
        return ret;
    }

    public void setClickable() {
        this.setIcon(ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_point_clickable));
        this.setOnMarkerClickListener(clickableListener);
    }

    public void setPOI() {
        this.setIcon(ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_point_of_interest));
        this.setOnMarkerClickListener(POIListener);
        this.lockTime = 0.0;
    }

    public void setVisited() {
        this.setIcon(ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_point_visited));
        this.setOnMarkerClickListener(VisitedListener);
        visited = true;
    }

    public void setLocked() {
        this.setIcon(ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_point_locked));
        this.setOnMarkerClickListener(LockedListener);
        lockTime = System.currentTimeMillis();
        lockResetThread.start();
    }
}
