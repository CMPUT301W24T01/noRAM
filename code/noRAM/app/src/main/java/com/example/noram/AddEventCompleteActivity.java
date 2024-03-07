package com.example.noram;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.model.Event;
import com.example.noram.model.QRCode;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Activity that appears as the completion screen when creating an event.
 */
public class AddEventCompleteActivity extends AppCompatActivity {

    /**
     * Create the Activity and setup listeners
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_created);

        // extract event from bundle, add it to database
        Bundle eventBundle = getIntent().getExtras();
        //set unique id for this event
        UUID myRand = UUID.randomUUID();
        Event event = new Event(
                myRand.toString(),
                eventBundle.getString("name"),
                eventBundle.getString("location"),
                (LocalDateTime) eventBundle.getSerializable("startTime"),
                (LocalDateTime) eventBundle.getSerializable("endTime"),
                eventBundle.getString("details"),
                eventBundle.getIntegerArrayList("milestones"),
                eventBundle.getBoolean("trackLocation")
        );
        event.updateDBEvent();

        //upload photo to event poster cloud storage with matching event id
        //set db with event poster. Only perform this action if an image was passed in the bundle
        if (eventBundle.getParcelable("imageUri") != null) {
            Uri imageUri = eventBundle.getParcelable("imageUri");
            //String jpg = imageUri.getLastPathSegment();
            //String uriString = "event_banners/" + jpg+ "-upload";
            String uriString = "event_banners/" + myRand+ "-upload";
            // upload file to cloud storage
            MainActivity.db.uploadPhoto(imageUri, uriString);
        }

        // show QR codes
        ImageView promoQR = findViewById(R.id.qr_promo);
        ImageView checkinQR = findViewById(R.id.qr_checkin);
        promoQR.setImageBitmap(event.getPromoQR().getBitmap());
        checkinQR.setImageBitmap(event.getCheckInQR().getBitmap());

        // share buttons
        ImageView shareCheckInButton = findViewById(R.id.share_checkin);
        ImageView sharePromoButton = findViewById(R.id.share_promo);
        shareCheckInButton.setOnClickListener(v -> shareQRCode(event.getCheckInQR()));
        sharePromoButton.setOnClickListener(v -> shareQRCode(event.getPromoQR()));

        Button goToEventButton = findViewById(R.id.event_details_button);
        goToEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: this should bring you to the event's page, once its viewable+integrated
                Intent intent = new Intent(AddEventCompleteActivity.this, OrganizerActivity.class);

                // this goes back to the OrganizerActivity and clears all Activities above it in the stack
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    /**
     * Opens a UI to share a QR code
     * @param qrCode qr code to share
     */
    private void shareQRCode(QRCode qrCode) {
        ShareHelper shareHelper = new ShareHelper();
        Intent shareIntent = shareHelper.generateShareIntent(qrCode.getBitmap(), qrCode.getEncodedData(), getApplicationContext());
        startActivity(Intent.createChooser(shareIntent, null));
    }
}
