/*
This file is used to display the information about an event for the organizer.
Outstanding Issues:
- Not all fields on xml page are filled, needs to get organizer content and event posters
 */

package com.example.noram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.controller.EventManager;
import com.example.noram.model.QRCode;

/**
 * The OrganizerEventInfoActivity class displays the information about an event for the organizer.
 * It contains a menu to navigate to different pages for the event.
 * A {@link AppCompatActivity} subclass.
 * @maintainer Gabriel
 * @author Gabriel
 */
public class OrganizerEventInfoActivity extends EventInfoActivityTemplate {

    private ImageView checkinQRImage;
    private ImageView promoQRImage;
    private ImageView checkinQRShare;
    private ImageView promoQRShare;

    /**
     * Hook from EventInfoActivityTemplate that is called before updating the base page, used to do
     * additional setup before updating the basic information.
     */
    @Override
    protected void preSetup(){
        // Get and set event QR codes
        String checkinID = event.getCheckInQRID();
        String promoID = event.getPromoQRID();
        MainActivity.db.getQrRef().document(checkinID).get().addOnSuccessListener(documentSnapshot1 -> {
            QRCode checkinCode = new QRCode();
            checkinCode.updateWithMap(documentSnapshot1.getData());
            checkinQRShare.setOnClickListener(v -> shareQRCode(checkinCode));
            checkinQRImage.setImageBitmap(checkinCode.getBitmap());
        });
        MainActivity.db.getQrRef().document(promoID).get().addOnSuccessListener(documentSnapshot1 -> {
            QRCode promoCode = new QRCode();
            promoCode.updateWithMap(documentSnapshot1.getData());
            promoQRShare.setOnClickListener(v -> shareQRCode(promoCode));
            promoQRImage.setImageBitmap(promoCode.getBitmap());
        });
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

        if (itemId == R.id.organizer_event_attendees) {
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

        // manually assign recurrent views
        usingDefaultViewIDs = false;
        backButton = findViewById(R.id.organizer_event_back_button);
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

        // get special variables from page
        ImageButton menuButton = findViewById(R.id.organizer_event_menu_button);
        promoQRImage = findViewById(R.id.promo_qr_code_img);
        checkinQRImage = findViewById(R.id.checkin_qr_code_img);
        checkinQRShare = findViewById(R.id.share_checkin_qr);
        promoQRShare = findViewById(R.id.share_promo_qr);

        // set up menu button
        menuButton.setOnClickListener(v -> {showMenu();});

        // retrieve corresponding event in database
        String eventID = getIntent().getExtras().getString(EventManager.eventIDLabel);
        initializePage(eventID);

        // connect edit button to edit_event page
        ImageButton editButton = findViewById(R.id.editButton);
        Activity currentAct = this;
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(currentAct, OrganizerEditEventActivity.class);
                intent.putExtra("event", event.getId());
                startActivity(intent);
            }
        });
    }

    /**
     * Called when the activity is resumed. Updates the activity's event with the latest info from the database.
     */
    @Override
    protected void onResume() {
        super.onResume();
        String eventID = getIntent().getExtras().getString(EventManager.eventIDLabel);
        initializePage(eventID);
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