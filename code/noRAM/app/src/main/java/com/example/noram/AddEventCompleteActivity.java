/*
This file is used to display the completion screen when creating an event. It also generates and displays the QR codes for the event.
Outstanding Issues:
- None
 */

package com.example.noram;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.controller.EventManager;
import com.example.noram.model.Event;
import com.example.noram.model.QRCode;
import com.example.noram.model.QRType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Activity that appears as the completion screen when creating an event.
 * A {@link AppCompatActivity} subclass.
 * @maintainer Cole
 * @author Cole
 * @author Sandra
 */
public class AddEventCompleteActivity extends AppCompatActivity {

    Event event; // new event that is being created

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
        setContentView(R.layout.organizer_activity_create_event_completed);

        // extract event from bundle, add it to database
        Bundle eventBundle = getIntent().getExtras();
        String promoQRData = eventBundle.getString("promoQRData");
        String checkinQRData = eventBundle.getString("checkinQRData");

        //set unique id for this event
        UUID eventID = UUID.randomUUID();
        QRCode promoQRCode = promoQRData == null
            ? new QRCode(eventID + "-promo", eventID.toString(), QRType.PROMOTIONAL)
            : new QRCode(promoQRData, eventID.toString(), QRType.PROMOTIONAL);
        QRCode checkInQRCode = checkinQRData == null
            ? new QRCode(eventID + "-event", eventID.toString(), QRType.SIGN_IN)
            : new QRCode(checkinQRData, eventID.toString(), QRType.SIGN_IN);

        event = new Event(
            eventID.toString(),
            eventBundle.getString("name"),
            eventBundle.getString("location"),
            (LocalDateTime) eventBundle.getSerializable("startTime"),
            (LocalDateTime) eventBundle.getSerializable("endTime"),
            eventBundle.getString("details"),
            eventBundle.getIntegerArrayList("milestones"),
            checkInQRCode.getHashId(),
            promoQRCode.getHashId(),
            eventBundle.getBoolean("trackLocation"),
            new ArrayList<>(),
            MainActivity.organizer.getIdentifier(),
            new ArrayList<>(),
            eventBundle.getLong("signUpLimit")
        );
        event.updateDBEvent();
        promoQRCode.updateDBQRCode();
        checkInQRCode.updateDBQRCode();

        //upload photo to event poster cloud storage with matching event id
        //set db with event poster. Only perform this action if an image was passed in the bundle
        if (eventBundle.getParcelable("imageUri") != null) {
            Uri imageUri = eventBundle.getParcelable("imageUri");
            String uriString = "event_banners/" + eventID+ "-upload";
            // upload file to cloud storage
            MainActivity.db.uploadPhoto(imageUri, uriString);
        }

        // show QR codes
        ImageView promoQR = findViewById(R.id.qr_promo);
        ImageView checkinQR = findViewById(R.id.qr_checkin);
        promoQR.setImageBitmap(promoQRCode.getBitmap());
        checkinQR.setImageBitmap(checkInQRCode.getBitmap());

        // share buttons
        ImageView shareCheckInButton = findViewById(R.id.share_checkin);
        ImageView sharePromoButton = findViewById(R.id.share_promo);
        shareCheckInButton.setOnClickListener(v -> shareQRCode(checkInQRCode));
        sharePromoButton.setOnClickListener(v -> shareQRCode(promoQRCode));

        // goToEvent button
        Context context = this;
        Button goToEventButton = findViewById(R.id.event_details_button);
        goToEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: remove commented code
                boolean reset = true;
                EventManager.displayOrganizerEvent(
                    context, event, reset);
                /*
                Intent intent = new Intent(AddEventCompleteActivity.this, OrganizerActivity.class);

                // this goes back to the OrganizerActivity and clears all Activities above it in the stack
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                */

            }
        });
    }

    /**
     * Opens a UI to share a QR code
     * @param qrCode qr code to share
     */
    private void shareQRCode(QRCode qrCode) {
        ShareHelper shareHelper = new ShareHelper();
        Intent shareIntent = shareHelper.generateShareIntent(qrCode.getBitmap(), qrCode.getHashId(), getApplicationContext());
        startActivity(Intent.createChooser(shareIntent, null));
    }
}
