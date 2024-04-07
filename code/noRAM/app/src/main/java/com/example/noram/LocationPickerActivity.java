/* Class for a location picker, to be used for selecting event locations. */
package com.example.noram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.util.Arrays;

/**
 * Activity for picking a location on a map when making events
 * @maintainer Cole
 * @author Cole
 */
public class LocationPickerActivity extends AppCompatActivity {
    private TextView currentLocation;
    private RequestQueue requestQueue;
    private String selectedLocation = "";
    private GeoPoint selectedLocationCoordinates;
    private ImageButton backButton;
    private MapView map;
    private Marker selectedPosMarker;

    /**
     * Create the activity
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
        currentLocation = findViewById(R.id.location_picker_content);

        map = findViewById(R.id.location_picker_map);

        // setup map
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getController().setZoom(18.0);

        //set map properties
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        // Start location
        double lon = -113.520338;
        double lat = 53.526293;

        // put a compass on the screen
        CompassOverlay compassOverlay = new CompassOverlay(this, map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);
        GeoPoint point = new GeoPoint(lat, lon);
        selectedLocationCoordinates = point;
        map.getController().setCenter(point);

        selectedPosMarker = new Marker(map);
        selectedPosMarker.setInfoWindow(null);
        selectedPosMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

        MapEventsReceiver receiver = new MapEventsReceiver() {
            /**
             * Listener for a tap on the map. Updates the selected position
             * @param p the position where the event occurred.
             * @return true always
             */
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                map.getOverlays().remove(selectedPosMarker);
                selectedPosMarker.setPosition(p);
                map.getOverlays().add(selectedPosMarker);
                requestLocationName(p);
                return true;
            }

            /**
             * Listener for long press - unused here.
             * @param p the position where the event occurred.
             * @return
             */
            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        MapEventsOverlay overlays = new MapEventsOverlay(receiver);
        map.getOverlays().add(overlays);

        // setup back button
        backButton = findViewById(R.id.organizer_location_picker_back);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("location", selectedLocation);
            intent.putExtra("lon", selectedLocationCoordinates.getLongitude());
            intent.putExtra("lat", selectedLocationCoordinates.getLatitude());
            setResult(RESULT_OK, intent);
            finish();
        } );

        // create http request queue
        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * Creates an HTTP request to update the selected location
     * @param point geopoint of the selected location
     */
    private void requestLocationName(GeoPoint point) {
        String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" +
                point.getLatitude() + "&lon=" + point.getLongitude() + "&zoom=18&addressdetails=1";
        // Request a string response from the JsonObjectRequest URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                jsonObject -> requestCompleteCallback(jsonObject), volleyError -> Log.d("DEBUG", volleyError.toString()));
        selectedLocation = "";
        selectedLocationCoordinates = point;
        requestQueue.add(request);
    }

    /**
     * Called when an HTTP request is completed. Updates most recently selected location
     * @param object JSON result
     */
    private void requestCompleteCallback(JSONObject object) {
        try {
            String locName = object.getString("name");

            // if location name is empty, we take info from a full display name instead
            if (locName.isEmpty()) {
                String displayName = object.getString("display_name");
                String[] addr = Arrays.copyOfRange(displayName.split(","), 0, 5);
                locName =  String.join("", addr);
            }
            currentLocation.setText(locName);
            selectedLocation = locName;
        } catch (JSONException e) {
            Log.d("DEBUG", "couldn't get location name from json");
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