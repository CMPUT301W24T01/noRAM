package com.example.noram;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The OrganizerEventMapActivity class displays the map of an event for the organizer and check in locations.
 * A {@link AppCompatActivity} subclass.
 */
public class OrganizerEventMapActivity extends AppCompatActivity {
    /**
     * Setup the activity when it is created.
     * @param savedInstanceState If the activity is being re-initialized after
     *    previously being shut down then this Bundle contains the data
     */
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_event_map);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //inflate and create the map
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getController().setZoom(18.0);

        //set map properties
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        //put a compass on the screen
        CompassOverlay compassOverlay = new CompassOverlay(this, map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        //check if organizer has
        if (hasLocationPermissions()) {
            getLastLocationUser();
        }
        else{
            //Tell user problem
            Toast.makeText(getApplicationContext(), "Must allow Location to use maps.", Toast.LENGTH_SHORT).show();
            //exit maps activity somehow
            finish();
            }

    }
    @SuppressLint("MissingPermission")
    private void getLastLocationUser() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    //only the organizer gets a special call to handleLocation with drawable id
                    handleLocation(location, R.drawable.attendee_map_icon);
                } else {
                    // Location is null, handle the case accordingly
                    Toast.makeText(getApplicationContext(), "Unable to retrieve location", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to get location
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to get location", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void handleLocation(Location location, int drawID) {
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();

        GeoPoint point = new GeoPoint(latitude, longitude);
        Marker startMarker = new Marker(map);
        startMarker.setPosition(point);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        startMarker.setIcon(getResources().getDrawable(drawID));
        map.getOverlays().add(startMarker);
        map.getController().setCenter(point);
    }
    private void handleLocation(Location location) {
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();

        GeoPoint point = new GeoPoint(latitude, longitude);
        Marker startMarker = new Marker(map);
        startMarker.setPosition(point);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        map.getOverlays().add(startMarker);
        map.getController().setCenter(point);
    }
    private boolean hasLocationPermissions() {
        return (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onResume() {
        super.onResume();
            if (map != null) {
                map.onResume();
            }
    }
    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        super.onPause();
        if (map != null) {
            map.onPause();
        }
    }
}