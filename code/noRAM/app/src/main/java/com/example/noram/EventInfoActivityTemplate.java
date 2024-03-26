/*
This file is used to provide a template for displaying event information, that other activities
can use.
Outstanding Issues:
- The activity assumes the IDs of the recurrent views on the XML page
 */

package com.example.noram;

import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.noram.model.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A template {@link AppCompatActivity} for event info pages.
 * Should be used to create pages that display events information.
 * @maintainer Gabriel
 * @author Gabriel
 */
public abstract class EventInfoActivityTemplate extends AppCompatActivity {

    // events in the database
    protected final CollectionReference eventsRef = MainActivity.db.getEventsRef();
    protected Event event; // current event being displayed

    /**
     * Hook that is called before updating the base page, used to do additional setup before
     * updating the basic information.
     */
    protected abstract void preSetup();

    /**
     * Update a page that display event information with the database's information
     * Note: it is assumed the page use the same views IDs as written below.
     */
    private void updateBasePage(){
        preSetup();

        // get all variables from page
        ImageButton backButton = findViewById(R.id.backButton);
        TextView eventTitle = findViewById(R.id.eventTitle);
        TextView organizerText = findViewById(R.id.organizerText);
        ImageView organizerImage = findViewById(R.id.organizerImage);
        TextView eventLocation = findViewById(R.id.eventLocation);
        ImageView eventImage = findViewById(R.id.eventImage);
        TextView eventDescription = findViewById(R.id.eventDescription);

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

        //download the event image from db and populate the screen
        String findImage = "event_banners/"+event.getId()+"-upload";
        // set imageview and update organizer image preview
        if (FirebaseStorage.getInstance().getReference().child(findImage) != null) {
            MainActivity.db.downloadPhoto(findImage,
                    t -> runOnUiThread(() -> eventImage.setImageBitmap(t)));
        }
        //Note for when we download organizer photo:
        //remove purple background, and android icon in xml
        //if you want image to format nicely.
        //use android:scaleType="fitCenter"
        //look at xml fpr eventImage

        //organizerText.setText(); // TODO: update organizer (not implemented in event yet)
        // TODO: update organizer image
        //eventLocation.setText(); // TODO: format LocalDateTime with current API lvl
        // TODO: update event image

        // connect back button
        backButton.setOnClickListener(v -> {finish();});
    }

    /**
     * Retrieves the event from the database and initialize the event information page on successful
     * retrieval
     * @param eventID The event whose information is being displayed
     */
    protected void initializePage(String eventID){
        eventsRef.document(eventID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            /**
             * Update the page's event with the document's info
             * @param documentSnapshot the document snapshot of the event
             */
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    // update event
                    event = new Event();
                    event.updateWithDocument(documentSnapshot);
                    // update page's info
                    updateBasePage();
                }
                else{
                    // doesn't exist
                    throw new RuntimeException("Given event ID does not exist in the database");
                }
            }
        });
    }
}
