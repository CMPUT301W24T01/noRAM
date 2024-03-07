package com.example.noram;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for the QR code options when creating an event
 */
public class AddEventQROptionsActivity extends AppCompatActivity {

    /**
     * Create the event and set up button listeners
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_activity_create_event_p2);
        Bundle bundle = getIntent().getExtras();

        Button generateButton = findViewById(R.id.event_add_p2_gen_QR_button);
        Button uploadButton = findViewById(R.id.event_add_p2_upl_QR_button);


        // TODO: implement for US 01.01.02 - will need to pass info somehow
        uploadButton.setOnClickListener(v -> completeEventCreation(bundle));
        generateButton.setOnClickListener(v -> completeEventCreation(bundle));
    }

    /**
     * Complete the event creation by passing the final event bundle to the AddEventCompleteActivity
     * @param bundle
     */
    private void completeEventCreation(Bundle bundle) {
        // We can simply pass the received bundle through again - the next activity will construct the event
        Intent intent = new Intent(AddEventQROptionsActivity.this, AddEventCompleteActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

        // we won't need to go back so we can finish this activity.
        finish();
    }
}
