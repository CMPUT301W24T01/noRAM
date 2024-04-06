/*
This file is used to create the map feature for an organizer while they view their event.
An organizer is able to see their location and the location from where all their attendees checked-in from.
Outstanding Issues:
- Maybe: Properly handle the map application to prompt for location access if it does not have it
- Maybe: if event object says no tracking, continue as usual, just remove the map from the menu.
 */
package com.example.noram;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.noram.controller.EventManager;
import com.example.noram.model.Event;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Transaction;

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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * The OrganizerEventMapActivity class displays the map of an event for the organizer and check in locations.
 * A {@link AppCompatActivity} subclass.
 * @author Sandra Taskovic
 *         https://github.com/osmdroid
 *         CHATGPT for adding onSuccesListener and onFailureListner when acessing the user location
 *         this video on how to use googlePlay API https://www.youtube.com/watch?v=M0kUd2dpxo4&t=854s
 * @maintainer Sandra Taskovic
 */

public class OrganizerEventMapActivity extends AppCompatActivity {
    /**
     * Setup the map activity.
     * A {@link AppCompatActivity} subclass.
     */

    private MapView map = null;
    private ImageButton backButton;
    private String eventID;
    private List<GeoPoint> checkedInAttendeesLocations;

    /**
     * Create the map activity.
     * @param savedInstanceState If the fragment is being re-created from
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up the XML view
        setContentView(R.layout.activity_organizer_event_map);
        backButton = findViewById(R.id.organizer_event_back_button_map);

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
        } else {
            //request user location
            requestLocationPermission();
            // TODO: Cole, are we leaving this code as is? You mentioned you might want to change
            // TODO:        the marker to represent the event location instead of the  organizer.
            //Tell user they can not user maps unless they allow location services
            Toast.makeText(getApplicationContext(), "Must allow location tracking to use maps.", Toast.LENGTH_LONG).show();
            //exit maps activity somehow
            finish();
            return;
        }
        // after placing the organizer's location, add of the location of its attendees
        eventID = Objects.requireNonNull(getIntent().getExtras()).getString("event");
        assert (eventID != null);
        MainActivity.db.getEventsRef().document(eventID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            /**
             On successful acquisition of event attributes from database, grab
             checkedInAttendeesLocations and send it to be handled by the map
             @param documentSnapshot database object from which object is initialized
             */
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    // Make event
                    Event event = new Event();
                    event.updateWithDocument(documentSnapshot);
                    checkedInAttendeesLocations = event.getCheckedInAttendeesLocations();
                    handleLocation2(checkedInAttendeesLocations);
                }
            });
    };
    /**
     * get the last location of the user for use in the map activity.
     * uses the FusedLocationProviderClient provided by Google API to access location.
     * @suppress the permission check because I check it in onCreate()
     */
    @SuppressLint("MissingPermission")
    private void getLastLocationUser() {
        //grab the FusedLocationProviderClient builder
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //attempt to get the location
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            // if the API can get a location
            /**
             On successful acquisition of user's location, pass it to draw onto the map
             @param location the location to be added to the database
             */
            @Override
            public void onSuccess(Location location) {
                //and the location is not null
                if (location != null) {
                    //draw location on the map
                    handleLocation1(location, R.drawable.attendee_map_icon);
                } else {
                    //Exit maps. User provided permission to use location but the location was not properly received.
                    Toast.makeText(getApplicationContext(), "Unable to retrieve location", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            /**
             On failed acquisition of user's location, report error
             @param e the exception that occurred.
             */
            @Override
            public void onFailure(@NonNull Exception e) {
                //Exit maps. User did not provide access to locations.
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to get location", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Once the organizer's location is accessed, draw it on the map using the passed drawable
     * @param location the location of the user
     * @param drawID the drawable ID location used to draw location on the map
     */
    private void handleLocation1(Location location, int drawID) {
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
     * Once the list of attendee location is accessed, draw it on the map.
     * This function uses the default marker icon provided by the map.
     * @param checkedInAttendeesLocations the location of the user
     */
    private void handleLocation2(List<GeoPoint> checkedInAttendeesLocations) {
        int myNum = checkedInAttendeesLocations.size();
        for (int i = 0; i < myNum; i++) {
            GeoPoint point = checkedInAttendeesLocations.get(i);
            Marker startMarker = new Marker(map);
            startMarker.setPosition(point);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            map.getOverlays().add(startMarker);
            map.getController().setCenter(point);
        }
    }

    /**
     * Confirms if the app has access to the user location
     */
    private boolean hasLocationPermissions() {
        return (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Asks the user to allow location tracking
     */
    public void requestLocationPermission(){
        //Note, the thrid invokation of the method, after two denies, does not pop up.
        String fineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
        String coarseLocation = Manifest.permission.ACCESS_FINE_LOCATION;
        // Initialize an ArrayList to hold the permissions
        ArrayList<String> permissionsList = new ArrayList<>();
        // Add the permission to the ArrayList
        permissionsList.add(fineLocation);
        // Add the permission to the ArrayList
        permissionsList.add(coarseLocation);
        // Convert the ArrayList to an array
        String[] permissions = permissionsList.toArray(new String[permissionsList.size()]);
        // Request permissions, 99 requestCode doesn't mean anything
        ActivityCompat.requestPermissions(this, permissions, 99);
        //stackOverflow used as reference https://stackoverflow.com/questions/40142331/how-to-request-location-permission-at-runtime
    }

    /**
     * Allows the map to resume once put on pause
     */
    @Override
    public void onResume() {
        super.onResume();
            if (map != null) {
                // connect back button to the previous screen
                backButton.setOnClickListener(v -> {finish();});
                if (!hasLocationPermissions()) {
                    //get their location to run map
                    getLastLocationUser();
                }
                map.onResume();
            }
    }

    /**
     * Allows the map to be put on pause
     */
    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.;
        if (map != null) {
            // connect back button to the previous screen
            backButton.setOnClickListener(v -> {finish();});
            if (!hasLocationPermissions()) {
                //get their location to run map
                getLastLocationUser();
            }
            map.onPause();
        }
    }
}