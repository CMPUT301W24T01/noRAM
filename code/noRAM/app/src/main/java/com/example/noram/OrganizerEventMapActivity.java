/*
This file is used to create the map feature for an organizer while they view their event.
An organizer is able to see their location and the location from where all their attendees checked-in from.
Outstanding Issues:
- Get all attendees to show up as dots on the map.
- Properly handle the map application to prompt for location access if it does not have it
- must function if users check 'allow geolocation tracking' <- maybe that wording should be changed to 'allow location tracking?'
- create a back button that returns to the previous menu screen
 */
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
 * @author Sandra Taskovic
 *         https://github.com/osmdroid
 *         CHATGPT for adding onSuccesListener and onFailureListner when acessing user location
 *         this video for help on how to use googlePlay API https://www.youtube.com/watch?v=M0kUd2dpxo4&t=854s
 */
public class OrganizerEventMapActivity extends AppCompatActivity {
    /**
     * Setup the map activity.
     * A {@link AppCompatActivity} subclass.
     */

    //TODO: Sandra removes this line when she is sure we don't need it
    //private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;

    /**
     * Create the map activity.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down.
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up the XML view
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

        //check if organizer has allowed location permissions
        if (hasLocationPermissions()) {
            //if yes, get their location and show it on the map
            getLastLocationUser();
        }
        else{
            //Tell user they can not user maps unless they allow location services
            //TODO: ask the user for location permissions again.
            //TODO: if they allow location change the db to reflect this
            Toast.makeText(getApplicationContext(), "Must allow Location to use maps.", Toast.LENGTH_SHORT).show();
            //exit maps activity somehow
            finish();
            }

    }
    /**
     * get the last location of the user for use in the map activity.
     * uses the FusedLocationProviderClient provided by Google API to access location.
     * If location is accessed, goto handleLocation() to draw on map.
     * If location access fails, exit map.
     * @suppress the permission check because I check it in onCreate()
     */
    @SuppressLint("MissingPermission")
    private void getLastLocationUser() {
        //grab the FusedLocationProviderClient builder
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //attempt to get the location
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            // if the API can get a location
            @Override
            public void onSuccess(Location location) {
                //and the location is not null
                if (location != null) {
                    //draw location on the map
                    //TODO: create different drawables for attendees and organizers
                    handleLocation(location, R.drawable.attendee_map_icon);
                } else {
                    //TODO: exit maps. User provided permission to use location but the location was not properly received.
                    //TODO: create an internal error message
                    Toast.makeText(getApplicationContext(), "Unable to retrieve location", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        // FusedLocationProviderClient unable to get location
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // exit map
                //TODO: create an internal error message
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to get location", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    /**
     * Once the location is acessed, draw it on the map
     * @param location the location of the user
     * @param drawID the drawable ID location used to draw location on the map
     */
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
    /**
     * Once the location is acessed, draw it on the map
     * another version of this function but instead uses the default market icon provided by the map
     * @param location the location of the user
     */
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
    /**
     * Confirms if the app has access to the user location
     */
    private boolean hasLocationPermissions() {
        return (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }
    /**
     * Allows the map to resume once put on pause
     */
    //TODO: Sandra figures out if we actually need this method
    @Override
    public void onResume() {
        super.onResume();
            if (map != null) {
                map.onResume();
            }
    }
    /**
     * Allows the map to be put on pause
     */
    //TODO: Sandra figures out if we actually need this method
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