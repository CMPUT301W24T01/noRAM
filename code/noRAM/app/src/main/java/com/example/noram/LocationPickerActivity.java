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

/**
 * Activity for picking a location on a map when making events
 * @maintainer Cole
 * @author Cole
 */
public class LocationPickerActivity extends AppCompatActivity {
    private TextView currentLocation;
    private RequestQueue requestQueue;
    private String selectedLocation = "";
    private ImageButton backButton;

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

        // setup back button
        backButton = findViewById(R.id.organizer_location_picker_back);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("location", selectedLocation);
            setResult(RESULT_OK, intent);
            finish();
        } );

        // TODO: dynamic from map
        double lon = -113.520338;
        double lat = 53.526293;
        String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" +
                lat + "&lon=" + lon + "&zoom=18&addressdetails=1";

        // create http request queue
        requestQueue = Volley.newRequestQueue(this);

        // Request a string response from the JsonObjectRequest URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            jsonObject -> {
            try {
                String locName = jsonObject.getString("name");
                currentLocation.setText(locName);
                selectedLocation = locName;
            } catch (JSONException e) {
                Log.d("DEBUG", "couldn't get location name from json");
            }
            Log.d("DEBUG", jsonObject.toString());
        }, volleyError -> {
            Log.d("DEBUG", volleyError.toString());
        });
        requestQueue.add(request);
    }
}