/*
This file is used to display the information about an event for the organizer.
Outstanding Issues:
- Not all fields on xml page are filled, needs to get organizer content and event posters
 */

package com.example.noram;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.model.Event;
import com.example.noram.model.QRCode;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The OrganizerEventInfoActivity class displays the information about an event for the organizer.
 * It contains a menu to navigate to different pages for the event.
 * A {@link AppCompatActivity} subclass.
 */
public class OrganizerEventInfoActivity extends AppCompatActivity {

    private Event event;
    private TextView eventTitle;
    private TextView organizerText;
    private ImageView organizerImage;
    private TextView eventLocation;
    private ImageView eventImage;
    private TextView eventDescription;
    private ImageView checkinQRImage;
    private ImageView promoQRImage;
    private ImageView checkinQRShare;
    private ImageView promoQRShare;
    private TextView eventSignUps;

    /**
     * Update page's event ("event") with database's info
     * @param eventId the id of the event to be updated
     *                (the event must be in the database)
     */
    private void baseSetup(String eventId) {
        // TODO: This will likely need to be changed
        // Get event from database
        event = new Event();
        Task<DocumentSnapshot> task = MainActivity.db.getEventsRef().document(eventId).get();
        task.addOnSuccessListener(documentSnapshot -> {
            // update event
            event.updateWithDocument(documentSnapshot);
            // update page's info
            eventTitle.setText(event.getName());
            eventDescription.setText(event.getDetails());
            LocalDateTime startTime = event.getStartTime();
            eventLocation.setText(String.format("%s from %s - %s @ %s",
                    startTime.format(DateTimeFormatter.ofPattern("MMMM dd")),
                    startTime.format(DateTimeFormatter.ofPattern("HH:mma")),
                    event.getEndTime().format(DateTimeFormatter.ofPattern("HH:mma")),
                    event.getLocation()
            ));
            // Get event image
            String eventImagePath = "event_banners/"+event.getId()+"-upload";
            MainActivity.db.downloadPhoto(eventImagePath,
                    t -> runOnUiThread(() -> eventImage.setImageBitmap(t)));

            updateSignUpText();

            // Get event QR codes
            String checkinID = event.getCheckInQRID();
            String promoID = event.getPromoQRID();
            MainActivity.db.getQrRef().document(checkinID).get().addOnSuccessListener(documentSnapshot1 -> {
                QRCode checkinCode = new QRCode();
                checkinCode.updateWithMap(documentSnapshot1.getData());
                checkinQRShare.setOnClickListener(v -> shareQRCode(checkinCode));
                checkinQRImage.setImageBitmap(checkinCode.getBitmap());
            });
            MainActivity.db.getQrRef().document(checkinID).get().addOnSuccessListener(documentSnapshot1 -> {
                QRCode promoCode = new QRCode();
                promoCode.updateWithMap(documentSnapshot1.getData());
                promoQRShare.setOnClickListener(v -> shareQRCode(promoCode));
                promoQRImage.setImageBitmap(promoCode.getBitmap());
            });
        });

        // Since we only display events the organizer has, we can just use the current user's organizer details
        organizerText.setText("Organized by " + MainActivity.organizer.getName() + (" (You)"));
        MainActivity.db.downloadPhoto(MainActivity.organizer.getPhotoPath(),
                t -> runOnUiThread(() -> organizerImage.setImageBitmap(t)));

    }

    /**
     * Show the popup menu for the event
     */
    private void showMenu() {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.organizer_event_menu_button));
        popup.setOnMenuItemClickListener(item -> {
            switchMenu(item);
            return true;
        });
        popup.getMenuInflater().inflate(R.menu.organizer_event_info_menu, popup.getMenu());
        popup.show();
    }

    /**
     * Switch to the correct activity based on the menu item clicked
     * @param item the menu item that was clicked
     */
    private void switchMenu(MenuItem item) {
        Class<?> newActivity = null;
        int itemId = item.getItemId();

        if (itemId == R.id.organizer_event_edit_details) {
            newActivity = OrganizerEditEventActivity.class;
        } else if (itemId == R.id.organizer_event_attendees) {
            newActivity = OrganizerEventAttendeeListActivity.class;
        } else if (itemId == R.id.organizer_event_map) {
            newActivity = OrganizerEventMapActivity.class;
        } else if (itemId == R.id.organizer_event_milestones) {
            newActivity = OrganizerEventMilestonesActivity.class;
        } else if (itemId == R.id.organizer_event_notifications) {
            newActivity = OrganizerEventNotificationsActivity.class;
        }

        Intent intent = new Intent(this, newActivity);
        intent.putExtra("event", event.getId());
        startActivity(intent);
    }

    /**
     * Called when the activity is first created. Initializes the activity's variables and views.
     * @param savedInstanceState If the activity is being re-constructed from a previous saved state,
     *                           this is the state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_info);

        // get all variables from page
        ImageButton backButton = findViewById(R.id.organizer_event_back_button);
        ImageButton menuButton = findViewById(R.id.organizer_event_menu_button);
        eventTitle = findViewById(R.id.organizer_event_title);
        organizerText = findViewById(R.id.organizer_event_organizer_text);
        organizerImage = findViewById(R.id.organizer_event_organizer_image);
        eventLocation = findViewById(R.id.organizer_event_location);
        eventImage = findViewById(R.id.organizer_event_image);
        eventDescription = findViewById(R.id.organizer_event_description);
        promoQRImage = findViewById(R.id.promo_qr_code_img);
        checkinQRImage = findViewById(R.id.checkin_qr_code_img);
        checkinQRShare = findViewById(R.id.share_checkin_qr);
        promoQRShare = findViewById(R.id.share_promo_qr);
        eventSignUps = findViewById(R.id.eventSignUps);

        // connect back button
        backButton.setOnClickListener(v -> {finish();});

        // set up menu button
        menuButton.setOnClickListener(v -> {showMenu();});

        // retrieve corresponding event in database
        String eventID = getIntent().getExtras().getString("event");
        baseSetup(eventID);
    }

    /**
     * Called when the activity is resumed. Updates the activity's event with the latest info from the database.
     */
    @Override
    protected void onResume() {
        super.onResume();
        String eventID = getIntent().getExtras().getString("event");
        baseSetup(eventID);
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

    /**
     * Updates displayed count of signed-up attendees
     */
    private void updateSignUpText() {
        if (event.isLimitedSignUps()) {
            eventSignUps.setText(String.format(
                    getBaseContext().getString(R.string.signup_limit_format),
                    event.getSignUpCount(),
                    event.getSignUpLimit())
            );
        }
        else {
            eventSignUps.setText(String.format(
                    getBaseContext().getString(R.string.signup_count_format),
                    event.getSignUpCount())
            );
        }
    }
}