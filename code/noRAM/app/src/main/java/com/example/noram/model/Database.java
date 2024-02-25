package com.example.noram.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A class to represent the database
 */
public class Database {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference attendeeRef = db.collection("Attendees");
    private final CollectionReference organizerRef = db.collection("Organizers");
    private final CollectionReference adminRef = db.collection("Admins");
    private final CollectionReference photoRef = db.collection("Photos");
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();


    /**
     * A method to get the authentication
     * @return the authentication
     */
    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    /**
     * A method to get the database
     * @return the database
     */
    public FirebaseFirestore getDb() {
        return db;
    }

    /**
     * A method to get the attendee reference
     * @return the attendee reference
     */
    public CollectionReference getAttendeeRef() {
        return attendeeRef;
    }

    /**
     * A method to get the organizer reference
     * @return the organizer reference
     */
    public CollectionReference getOrganizerRef() {
        return organizerRef;
    }

    /**
     * A method to get the admin reference
     * @return the admin reference
     */
    public CollectionReference getAdminRef() {
        return adminRef;
    }

    /**
     * A method to add an attendee
     * @param identifier the identifier of the attendee
     * @return true if the attendee is added, false otherwise
     */
    public boolean addAttendee(String identifier) {
        try {
            attendeeRef.add(new Attendee(identifier)); // TODO: add the attendee properly
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * A method to add an organizer
     * @param identifier the identifier of the organizer
     * @return true if the organizer is added, false otherwise
     */
    public boolean addOrganizer(String identifier) {
        try {
            organizerRef.add(new Organizer(identifier)); // TODO: add the organizer properly
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * A method to add an admin
     * @param identifier the identifier of the admin
     * @return true if the admin is added, false otherwise
     */
    public boolean addAdmin(String identifier) {
        try {
            adminRef.add(new Admin(identifier)); // TODO: add the admin properly
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * A method to add a photo
     * @param identifier // TODO: PLACE HOLDER FOR PHOTO's
     * @return true if the photo is added, false otherwise
     */
    public boolean addPhoto(int identifier) {
        try {
            photoRef.add(new Photo()); // TODO: add the photo properly

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

