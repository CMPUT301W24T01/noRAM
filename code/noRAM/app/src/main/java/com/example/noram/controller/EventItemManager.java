/*
This file is used to make it easier to setup event items in listviews.
Outstanding Issues:
- None
 */
package com.example.noram.controller;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.noram.MainActivity;
import com.example.noram.R;
import com.example.noram.model.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A class to provide services when initializing/updating event items in listviews.
 * @maintainer Gabriel
 * @author Gabriel
 */
public class EventItemManager {
    private Event event; // event that the list item is displaying
    private View view; // view used to display the item
    private Context context; // context from which the item is being initialized

    /**
     * A constructor to create an EventItemManager
     * @param context The context from which the item is being initialized
     * @param event The event who holds the data that the item is displaying
     * @param view View that the item is using to display the data
     */
    EventItemManager(Context context, Event event, View view){
        this.context = context;
        this.event = event;
        this.view = view;
    }

    /**
     * Modifies the item view to show the event's title
     */
    public void setTitle(){
        TextView eventTitle = view.findViewById(R.id.event_title);
        eventTitle.setText(event.getName());
    }

    /**
     * Modifies the item view to show the event's time
     */
    public void setTime(){
        TextView eventTime = view.findViewById(R.id.event_time);
        LocalDateTime startTime = event.getStartTime();
        LocalDateTime endTime = event.getEndTime();
        TextView happeningNowText = view.findViewById(R.id.event_happening_now);


        if (startTime.toLocalDate().equals(endTime.toLocalDate())) {
            eventTime.setText(String.format("%s \n%s to %s",
                    startTime.format(DateTimeFormatter.ofPattern("MMM dd")),
                    startTime.format(DateTimeFormatter.ofPattern("h:mma")),
                    endTime.format(DateTimeFormatter.ofPattern("h:mma"))
            ));
        } else {
            // not the same date: need to include both dates
            eventTime.setText(String.format("%s at %s to \n%s at %s",
                    startTime.format(DateTimeFormatter.ofPattern("MMM dd")),
                    startTime.format(DateTimeFormatter.ofPattern("h:mm a")),
                    endTime.format(DateTimeFormatter.ofPattern("MMM dd")),
                    endTime.format(DateTimeFormatter.ofPattern("h:mm a"))
            ));
        }

        // if event is currently happening, display "happening now!"
        LocalDateTime current = LocalDateTime.now();
        happeningNowText.setVisibility(startTime.isBefore(current) && endTime.isAfter(current) ? View.VISIBLE : View.GONE);

    }

    /**
     * Modifies the item view to show the event's location
     */
    public void setLocation(){
        TextView eventLocation = view.findViewById(R.id.event_location);
        eventLocation.setText(event.getLocation());
    }

    /**
     * Modifies the item view to show the event's signup count
     */
    public void setSignedUpCount(){
        TextView eventSignUpCapacity = view.findViewById(R.id.event_signUp_capacity);

        if (event.isLimitedSignUps()) {
            eventSignUpCapacity.setText(String.format(
                    context.getString(R.string.signup_limit_format),
                    event.getSignUpCount(),
                    event.getSignUpLimit())
            );
        }
        else {
            eventSignUpCapacity.setText(String.format(
                    context.getString(R.string.signup_count_format),
                    event.getSignUpCount())
            );
        }
    }

    /**
     * Modifies the item view to show the event's checked-in status
     */
    public void setCheckedInStatus(){
        TextView checkedInText = view.findViewById(R.id.event_checked_in_indicator);

        if (event.getCheckedInAttendees().contains(MainActivity.attendee.getIdentifier())) {
            checkedInText.setVisibility(View.VISIBLE);
        } else {
            checkedInText.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Modifies the item view to show the event's signed-up status
     */
    public void setSignedUpStatus(){
        TextView signedUpText = view.findViewById(R.id.event_signed_up_indicator);

        if (event.getSignedUpAttendees().contains(MainActivity.attendee.getIdentifier())) {
            signedUpText.setVisibility(View.VISIBLE);
        } else {
            signedUpText.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Modifies the item view to show the event's ID
     */
    public void setID(){
        TextView idText = view.findViewById(R.id.event_id);

        idText.setText(event.getId());
    }

    /**
     * Modifies the item view to show the event's organizer
     */
    public void setOrganizer(){
        TextView organizerText = view.findViewById(R.id.event_organizer);

        // get the event's organizer and display its name on success
        DocumentReference docRef = MainActivity.db.getOrganizerRef().document(event.getOrganizerId());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // get organizer name and ID and display it
                String organizer = documentSnapshot.getString("name");
                organizer += documentSnapshot.getId();
                organizerText.setText(organizer);
            }
        });
    }

}
