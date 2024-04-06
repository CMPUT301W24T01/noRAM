/*
This file is used to make it easier to display event-info pages.
Outstanding Issues:
- None
 */

package com.example.noram.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.example.noram.OrganizerEventInfoActivity;
import com.example.noram.model.Event;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Transaction;

import org.osmdroid.util.GeoPoint;

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
     * Check-in the current user to the event given by eventID into the db
     * @param eventId ID string of the event
     * @param userLocation the location of the user if they allowed location-tracking at the time
     * of check-in
     */
    @SuppressLint("MissingPermission")
    public static void checkInToEvent(String eventId, Location userLocation) {
        DocumentReference eventRef = MainActivity.db.getEventsRef().document(eventId);
        MainActivity.attendee.getEventsCheckedInto().add(eventId);
        MainActivity.attendee.updateDBAttendee();
        Log.i("we are inside CheckIN", "yay");
        GeoPoint point;
        //if the user allowed location tracking, also add it to the db
        if (userLocation != null ) {
            //unpack the location
            Double latitude = userLocation.getLatitude();
            Double longitude = userLocation.getLongitude();
            // create a new GeoPoint for the db
            point = new GeoPoint(latitude, longitude);
            //add GeoPoint on the transaction
            MainActivity.db.getDb().runTransaction((Transaction.Function<Void>) transaction -> {
                DocumentSnapshot snapshot = transaction.get(eventRef);
                //add GeoPoint to checkedInAttendeeLocations
                List<GeoPoint> checkedInAttendeesLocations =
                        (List<GeoPoint>) snapshot.get("checkedInAttendeesLocations");
                checkedInAttendeesLocations.add(point);
                transaction.update(eventRef, "checkedInAttendeesLocations",
                        checkedInAttendeesLocations);
                return null;
            });
        }
        //if user location is null, we only update checkedInAttendees
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
     * Unsign the current user from the event given by eventID
     * @param eventID ID string of the event
     */
    public static void unsignFromEvent(String eventID){
        DocumentReference eventRef = MainActivity.db.getEventsRef().document(eventID);
        MainActivity.attendee.getEventsCheckedInto().remove(eventID);
        MainActivity.attendee.updateDBAttendee();

        // run a transaction on the event to update checkedInAttendee list
        MainActivity.db.getDb().runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef);
            List<String> signedUpAttendees = (List<String>) snapshot.get("signedUpAttendees");
            signedUpAttendees.remove(MainActivity.attendee.getIdentifier());
            transaction.update(eventRef, "signedUpAttendees", signedUpAttendees);
            return null;
        });
    }

    /**
     * An event's information is displayed on the attendee side, showing all the attendee-event info
     * @param context The context of the activity calling this function, from where the new activity
     *                is started
     * @param event The event whose information need to be displayed
     */
    public static void displayAttendeeEvent(Context context, Event event){
        Intent intent = new Intent(context, AttendeeEventInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(eventIDLabel, event.getId());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * An event's information is displayed on the organizer side, showing all the organizer-event
     * info
     * @param context The context of the activity calling this function, from where the new activity
     *                is started
     * @param event The event whose information need to be displayed
     */
    public static void displayOrganizerEvent(Context context, Event event){
        Intent intent = new Intent(context, OrganizerEventInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(eventIDLabel, event.getId());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * Overloads displayOrganizerEvent() with extra 'reset' parameter to optionally let the user
     * clear the current activity stack when launching a new activity
     * launching the new activity
     * @param context The context of the activity calling this function, from where the new activity
     *                is started
     * @param event The event whose information need to be displayed
     * @param reset If true, all the activities on the stack are removed when the new
     *              activity is launched
     */
    public static void displayOrganizerEvent(Context context, Event event, boolean reset){
        Intent intent = new Intent(context, OrganizerEventInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(eventIDLabel, event.getId());
        intent.putExtras(bundle);
        // optionally remove all other stack activities
        if(reset){
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
            ((Activity) context).finish();
        }
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
