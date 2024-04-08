/*
This file is used to create the map feature for an organizer while they view their event.
An organizer is able to see their location and the location from where all their attendees checked-in from.
Outstanding Issues:
- Maybe: Properly handle the map application to prompt for location access if it does not have it
- Maybe: if event object says no tracking, continue as usual, just remove the map from the menu.
 */

package com.example.noram;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.noram.model.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;
import java.util.Objects;

/**
 * The OrganizerEventMapActivity class displays the map of an event for the organizer and check in locations.
 * A {@link AppCompatActivity} subclass.
 * @author Sandra Taskovic
 *         https://github.com/osmdroid
 *         CHATGPT for adding onSuccesListener and onFailureListner when acessing the user location
 *         this video on how to use googlePlay API <a href="https://www.youtube.com/watch?v=M0kUd2dpxo4&t=854s">...</a>
 * @maintainer Sandra Taskovic
 */

public class OrganizerEventMapActivity extends AppCompatActivity {
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
        backButton.setOnClickListener(v -> finish());

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //inflate and create the map
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getController().setZoom(18.0);

        //set map properties
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

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

                    if (event.getLocationIsRealLocation()) {
                        drawEventLocation(event.getLocationCoordinates(), R.drawable.attendee_map_icon);
                    } else if (event.isTrackLocation() && event.getCheckedInAttendeesLocations().size() > 0) {
                        map.getController().setCenter(event.getCheckedInAttendeesLocations().get(0));
                    } else {
                        // default location
                        double lon = -113.520338;
                        double lat = 53.526293;
                        GeoPoint pt = new GeoPoint(lat, lon);
                        map.getController().setCenter(pt);
                    }

                    if (event.isTrackLocation()) {
                        checkedInAttendeesLocations = event.getCheckedInAttendeesLocations();
                        drawCheckinLocations(checkedInAttendeesLocations);
                    }
                }
            });
    };

    /**
     * Once the organizer's location is accessed, draw it on the map using the passed drawable
     * @param location the location of the user
     * @param drawID the drawable ID location used to draw location on the map
     */
    private void drawEventLocation(GeoPoint location, int drawID) {
        Marker startMarker = new Marker(map);
        startMarker.setPosition(location);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        startMarker.setIcon(ResourcesCompat.getDrawable(getResources(), drawID, null));
        map.getOverlays().add(startMarker);
        map.getController().setCenter(location);
    }

    /**
     * Once the list of attendee location is accessed, draw it on the map.
     * This function uses the default marker icon provided by the map.
     * @param checkedInAttendeesLocations the location of the user
     */
    private void drawCheckinLocations(List<GeoPoint> checkedInAttendeesLocations) {
        int myNum = checkedInAttendeesLocations.size();
        for (int i = 0; i < myNum; i++) {
            GeoPoint point = checkedInAttendeesLocations.get(i);
            Marker startMarker = new Marker(map);
            startMarker.setPosition(point);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            map.getOverlays().add(startMarker);
        }
    }

    /**
     * Allows the map to resume once put on pause
     */
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
    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.;
        if (map != null) {
            map.onPause();
        }
    }
}