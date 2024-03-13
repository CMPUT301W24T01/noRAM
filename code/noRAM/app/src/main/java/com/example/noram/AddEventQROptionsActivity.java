/*
This file is used to create the QR code options when creating an event. It allows the user to choose between generating a QR code or uploading one.
Outstanding Issues:
- Need to implement re-use of QR codes
 */

package com.example.noram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.model.QRCode;
import com.example.noram.model.QRType;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.Result;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Activity for the QR code options when creating an event
 * A {@link AppCompatActivity} subclass.
 * @maintainer Cole
 * @author Cole
 */
public class AddEventQROptionsActivity extends AppCompatActivity {

    private Bundle bundle;

    private QRType lastQRTypeSelected;

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
        bundle = getIntent().getExtras();

        Button generateButton = findViewById(R.id.event_add_p2_gen_QR_button);
        Button uploadButton = findViewById(R.id.event_add_p2_upl_QR_button);

        // TODO: implement for US 01.01.02 - will need to pass info somehow
        uploadButton.setOnClickListener(v -> showQRReuseDialog());
        generateButton.setOnClickListener(v -> completeEventCreation(bundle));
    }

    /**
     * Complete the event creation by passing the final event bundle to the AddEventCompleteActivity
     * @param bundle The bundle containing the event information
     */
    private void completeEventCreation(Bundle bundle) {
        // We can simply pass the received bundle through again - the next activity will construct the event
        Intent intent = new Intent(AddEventQROptionsActivity.this, AddEventCompleteActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

        // we won't need to go back so we can finish this activity.
        finish();
    }

    /**
     * Show a dialog to select the type of QR code we want to upload for reuse
     */
    private void showQRReuseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select QR Type");
        builder.setMessage("Select whether you want to upload a QR code for promotion or check-in");

        // Check in selection button
        builder.setPositiveButton("Check-in", (dialog, which) -> {
            lastQRTypeSelected = QRType.SIGN_IN;
            showImagePicker();
        });

        // Promotion selection button
        builder.setNeutralButton("Promotional", (dialog, which) -> {
            lastQRTypeSelected = QRType.PROMOTIONAL;
            showImagePicker();
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    /**
     * Start the image picker to select a QR code for reuse
     */
    private void showImagePicker() {
        ImagePicker.with(AddEventQROptionsActivity.this)
                .galleryOnly()
                .crop(1,1)
                .compress(1024)
                .start();
    }

    /**
     * Tries to get a qr code from an image once it is selected from a phone
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // get image uri and file name from it
        Uri uri =  data.getData();

        // if there's no uri, we didn't get a new photo, so return.
        if (uri == null) {
            return;
        }

        // try to reuse the qr code
        Bitmap bmp;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Result checkResult = QRCode.checkImageForQRCode(bmp);
        if (checkResult != null) {
            // use the bitmap, move on to next activity
            String qrData = checkResult.getText();
            checkDatabaseQRCollision(qrData);
        } else {
            // show error message...
            reuseFailure();
        }
    }

    /**
     * Checks if a qr code collides with an event already in the database
     */
    private void checkDatabaseQRCollision(String qrData) {

        OnSuccessListener<DocumentSnapshot> eventLookupListener = documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String dateTime = documentSnapshot.getString("endTime");
                LocalDateTime eventEndTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                LocalDateTime currentTime = LocalDateTime.now();

                // TODO: is there a good way to decouple any of this logic from the database, for
                // unit testing? would be nice to do so.
                if (currentTime.isAfter(eventEndTime)) {
                    reuseSuccess();
                } else {
                    // BAD: the added QR code is already in use
                    reuseFailure();
                }
            } else {
                reuseSuccess();
            }
        };

        // Listener for a qrLookup event.
        OnSuccessListener<DocumentSnapshot> qrLookupListener = documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String eventId = documentSnapshot.getString("event");
                // lookup event
                MainActivity.db.getEventsRef().document(eventId).get().addOnSuccessListener(eventLookupListener);
            } else {
                reuseSuccess();
            }
        };

        MainActivity.db.getQrRef().document(qrData).get().addOnSuccessListener(qrLookupListener);
    }

    /**
     * Called when we confirm that a QR code is OK for reuse
     */
    private void reuseSuccess() {
        // TODO: how to best update UI?
    }

    /**
     * Called when we find a QR code is NOT ok for reuse
     */
    private void reuseFailure(String errMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Couldn't re-use QR code");
        builder.setMessage(String.format("Could not reuse the qr code: %s", errMsg));
        builder.setPositiveButton("OK", null);
    }
 }
