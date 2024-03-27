package com.example.noram.controller;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.noram.AttendeeEventInfoActivity;
import com.example.noram.MainActivity;
import com.example.noram.R;
import com.example.noram.model.Event;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.List;

/**
 * A class to provide services when interacting with events in the app, such as signing-in events.
 * @maintainer Gabriel
 * @author Gabriel
 */
public class EventManager {
    public static final String eventIDLabel = "eventID"; // Identifier for event's ID in bundles
    // Identifier for event's checked-in status
    public static final String checkedInLabel = "checkedIn";


    /**
     * Check-in the current user to the event given by eventID
     * @param eventId ID string of the event
     */
    @SuppressLint("MissingPermission")
    public static void checkInToEvent(String eventId, Location userLocation) {
        DocumentReference eventRef = MainActivity.db.getEventsRef().document(eventId);
        MainActivity.attendee.getEventsCheckedInto().add(eventId);
        MainActivity.attendee.updateDBAttendee();

        // run a transaction on the event to update attendee list
        MainActivity.db.getDb().runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef);
            List<String> checkedInAttendees = (List<String>) snapshot.get("checkedInAttendees");
            checkedInAttendees.add(MainActivity.attendee.getIdentifier());
            transaction.update(eventRef, "checkedInAttendees", checkedInAttendees);

            //add to checkedInAttendeeLocations
            if (userLocation != null) {
                List<Location> checkedInAttendeesLocations = (List<Location>) snapshot.get("checkedInAttendeesLocations");
                checkedInAttendeesLocations.add(userLocation);
                transaction.update(eventRef, "checkedInAttendeesLocations", checkedInAttendeesLocations);
            }
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
