/*
This file is used to create the QR code options when creating an event. It allows the user to choose between generating a QR code or uploading one.
Outstanding Issues:
- None
 */

package com.example.noram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.model.HashHelper;
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
    private TextView checkinUriText;
    private Button checkinUploadButton;
    private ImageView checkinPreview;
    private TextView promoUriText;
    private Button promoUploadButton;
    private ImageView promoPreview;
    private String promoQRCodeData = null;
    private String checkinQRCodeData = null;
    private boolean isUploadingPromo = false;
    private boolean isUploadingCheckin = false;

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

        Button finishButton = findViewById(R.id.organizer_create_event_complete_button);
        finishButton.setOnClickListener(v -> completeEventCreation(bundle));

        // setup listeners + view for checkin section
        checkinUriText = findViewById(R.id.checkin_uri_placeholder_text);
        checkinUriText.setVisibility(View.INVISIBLE);
        checkinUriText.setText("");
        checkinUploadButton = findViewById(R.id.upload_checkin_qr_button);
        checkinUploadButton.setVisibility(View.INVISIBLE);
        checkinPreview = findViewById(R.id.checkin_preview);
        checkinPreview.setVisibility(View.INVISIBLE);
        checkinUploadButton.setOnClickListener(v -> {
            lastQRTypeSelected = QRType.SIGN_IN;
            showImagePicker();
        });
        RadioGroup checkinGroup = findViewById(R.id.checkin_radio_group);
        checkinGroup.check(R.id.autogenerate_button_checkin);
        checkinGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int visibility = checkedId == R.id.autogenerate_button_checkin
                    ? View.INVISIBLE
                    : View.VISIBLE;
            checkinUriText.setVisibility(visibility);
            checkinUploadButton.setVisibility(visibility);
            checkinPreview.setVisibility(visibility);
            isUploadingCheckin = checkedId == R.id.upload_button_checkin;
        });

        // setup listeners + view for promo section
        promoUriText = findViewById(R.id.promo_uri_placeholder_text);
        promoUriText.setText("");
        promoUriText.setVisibility(View.INVISIBLE);
        promoUploadButton = findViewById(R.id.upload_promo_qr_button);
        promoUploadButton.setVisibility(View.INVISIBLE);
        promoPreview = findViewById(R.id.promo_preview);
        promoPreview.setVisibility(View.INVISIBLE);
        promoUploadButton.setOnClickListener(v -> {
            lastQRTypeSelected = QRType.PROMOTIONAL;
            showImagePicker();
        });
        RadioGroup promoGroup = findViewById(R.id.promo_radio_group);
        promoGroup.check(R.id.autogenerate_button_promo);
        promoGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int visibility = checkedId == R.id.autogenerate_button_promo
                    ? View.INVISIBLE
                    : View.VISIBLE;
            promoUploadButton.setVisibility(visibility);
            promoUriText.setVisibility(visibility);
            promoPreview.setVisibility(visibility);
            isUploadingPromo = checkedId == R.id.upload_button_promo;
        });
    }

    /**
     * Complete the event creation by passing the final event bundle to the AddEventCompleteActivity
     * @param bundle The bundle containing the event information
     */
    private void completeEventCreation(Bundle bundle) {
        // verify that valid QR codes are setup for both checkin and promo
        boolean promoQRValid = !isUploadingPromo || promoQRCodeData != null;
        boolean checkinQRValid = !isUploadingCheckin || checkinQRCodeData != null;

        if (promoQRValid && checkinQRValid) {
            // add QR data to the bundle
            bundle.putString("promoQRData", promoQRCodeData);
            bundle.putString("checkinQRData", checkinQRCodeData);

            // We can simply pass the received bundle through again - the next activity will construct the event
            Intent intent = new Intent(AddEventQROptionsActivity.this, AddEventCompleteActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);

            // we won't need to go back so we can finish this activity.
            finish();
        } else {
            Toast.makeText(this, "Upload selected, but you haven't uploaded a code!", Toast.LENGTH_SHORT).show();
        }
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
            String qrHash = HashHelper.hashSHA256(qrData);
            checkDatabaseQRCollision(uri, qrHash, qrData);
        } else {
            // show error message...
            reuseFailure("Couldn't find QR Code in Image");
        }
    }

    /**
     * Checks if a qr code collides with an event already in the database
     * @param uri uri that contains the QR Code
     * @param qrData encoded data for the QR code
     */
    private void checkDatabaseQRCollision(Uri uri, String qrHash, String qrData) {
        OnSuccessListener<DocumentSnapshot> eventLookupListener = documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String dateTime = documentSnapshot.getString("endTime");
                LocalDateTime eventEndTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                LocalDateTime currentTime = LocalDateTime.now();

                if (currentTime.isAfter(eventEndTime)) {
                    reuseSuccess(uri, qrHash, qrData);
                } else {
                    // BAD: the added QR code is already in use
                    reuseFailure("QR Already in Use");
                }
            } else {
                reuseSuccess(uri, qrHash, qrData);
            }
        };

        // Listener for a qrLookup event.
        OnSuccessListener<DocumentSnapshot> qrLookupListener = documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String eventId = documentSnapshot.getString("event");
                // lookup event
                MainActivity.db.getEventsRef().document(eventId).get().addOnSuccessListener(eventLookupListener);
            } else {
                reuseSuccess(uri, qrHash, qrData);
            }
        };

        MainActivity.db.getQrRef().document(qrHash).get().addOnSuccessListener(qrLookupListener);
    }

    /**
     * Called when we confirm that a QR code is OK for reuse
     * @param uri uri that contains the QR Code
     * @param qrData encoded data for the QR code
     */
    private void reuseSuccess(Uri uri, String qrHash, String qrData) {
        // make sure we aren't already using this QR code
        boolean alreadyUsedForCheckin = lastQRTypeSelected == QRType.PROMOTIONAL && qrData.equals(checkinQRCodeData);
        boolean alreadyUsedForPromo = lastQRTypeSelected == QRType.SIGN_IN && qrData.equals(promoQRCodeData);
        if (alreadyUsedForPromo || alreadyUsedForCheckin) {
            reuseFailure("You are already using this QR Code");
            return;
        }

        if (lastQRTypeSelected == QRType.PROMOTIONAL) {
            promoUriText.setTextColor(Color.BLACK);
            promoUriText.setText("Uploaded: " + uri.getLastPathSegment());
            promoPreview.setImageURI(uri);
            promoQRCodeData = qrData;
        } else if (lastQRTypeSelected == QRType.SIGN_IN) {
            checkinUriText.setTextColor(Color.BLACK);
            checkinUriText.setText("Uploaded: " + uri.getLastPathSegment());
            checkinPreview.setImageURI(uri);
            checkinQRCodeData = qrData;
        }
    }

    /**
     * Called when we find a QR code is NOT ok for reuse - shows failure dialog.
     * @param errMsg error msg to include in the popup
     */
    private void reuseFailure(String errMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Couldn't re-use QR code");
        builder.setMessage(String.format("Could not reuse the qr code: %s", errMsg));
        builder.setPositiveButton("OK", null);
        builder.create().show();

        if (lastQRTypeSelected == QRType.PROMOTIONAL) {
            promoUriText.setText("Upload failed, please try again.");
            promoUriText.setTextColor(Color.RED);
            promoPreview.setImageURI(null);
            promoQRCodeData = null;
        } else {
            checkinUriText.setText("Upload failed, please try again.");
            checkinUriText.setTextColor(Color.RED);
            checkinPreview.setImageURI(null);
            checkinQRCodeData = null;
        }
    }
 }
