/*
This file is used to make it easier to display event-info pages.
Outstanding Issues:
- None
 */

package com.example.noram.controller;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.noram.AttendeeEventInfoActivity;
import com.example.noram.MainActivity;
import com.example.noram.model.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.List;

/**
 * A class to provide services when interacting with events in the app, such as signing-in events.
 * @maintainer Gabriel
 * @author Gabriel
 * @author Carlin
 */
public class EventManager {
    public static final String eventIDLabel = "eventID"; // Identifier for event's ID in bundles
    // Identifier for event's checked-in status
    public static final String checkedInLabel = "checkedIn";


    /**
     * Check-in the current user to the event given by eventID
     * @param eventId ID string of the event
     */
    public static void checkInToEvent(String eventId) {
        DocumentReference eventRef = MainActivity.db.getEventsRef().document(eventId);
        MainActivity.attendee.getEventsCheckedInto().add(eventId);
        MainActivity.attendee.updateDBAttendee();

        // run a transaction on the event to update checkedInAttendee list
        MainActivity.db.getDb().runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef);
            List<String> checkedInAttendees = (List<String>) snapshot.get("checkedInAttendees");
            checkedInAttendees.add(MainActivity.attendee.getIdentifier());
            transaction.update(eventRef, "checkedInAttendees", checkedInAttendees);
            return null;
        });
    }

    /**
     * Sign the current user up for the event given by eventID
     * @param eventID ID string of the event
     */
    public static void signUpForEvent(String eventID) {
        DocumentReference eventRef = MainActivity.db.getEventsRef().document(eventID);
        MainActivity.attendee.getEventsCheckedInto().add(eventID);
        MainActivity.attendee.updateDBAttendee();

        // run a transaction on the event to update checkedInAttendee list
        MainActivity.db.getDb().runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef);
            List<String> signedUpAttendees = (List<String>) snapshot.get("signedUpAttendees");
            signedUpAttendees.add(MainActivity.attendee.getIdentifier());
            transaction.update(eventRef, "signedUpAttendees", signedUpAttendees);
            return null;
        });
    }

    /**
     * An event's information is displayed by calling a new activity with all of its information
     * @param context The context of the activity calling this function, from where the new activity
     *                is started
     * @param event The event whose information need to be displayed
     */
    public static void displayEvent(Context context, Event event){
        Intent intent = new Intent(context, AttendeeEventInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(eventIDLabel, event.getId());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * An event's information is displayed by calling a new activity with all of its information.
     * Additional information is passed so that CheckedIn display is guaranteed to be displayed (in
     * case of database latency)
     * @param context The context of the activity calling this function, from where the new activity
     *                is started
     * @param event The event whose information need to be displayed
     */
    public static void displayCheckedInEvent(Context context, Event event){
        Intent intent = new Intent(context, AttendeeEventInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(eventIDLabel, event.getId());
        bundle.putBoolean(checkedInLabel, true);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
