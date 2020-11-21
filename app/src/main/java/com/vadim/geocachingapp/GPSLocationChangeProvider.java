package com.vadim.geocachingapp;

import android.content.Context;
import android.location.Location;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;

import java.util.List;

public class GPSLocationChangeProvider extends GpsMyLocationProvider {

    private final double clickableDistance = 20;

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
            if (marker.visited)
            {
                marker.setVisited();
                continue;
            }
            if (marker.getTimeLockLeft() > 0.0)
            {
                continue;
            }
            if (distance <= clickableDistance) {
                marker.setClickable();
            } else {
                marker.setPOI();
            }
        }
    }
}
