/*
This file is used to create an organizer object. This object is used to store the identifier of the organizer. This identifier is used to identify the organizer in the database.
Outstanding Issues:
- None
 */

package com.example.noram.model;

import com.example.noram.MainActivity;

/**
 * A class representing an organizer
 * @maintainer Christiaan
 * @author Christiaan
 * @author Ethan
 * @author Cole
 */
public class Organizer {
    private String identifier;
    private String name;
    private String photoPath;
    private boolean usingAttendeeProfilePicture = false;

    /**
     * Create a new organizer.
     */
    public Organizer() {}

    /**
     * A constructor to create an organizer
     * @param identifier the identifier of the organizer
     * @param name name for the organizer
     * @param photoPath path to the organizer's profile photo in the cloud.
     */
    public Organizer(String identifier, String name, String photoPath, boolean usingAttendeeProfilePicture) {
        this.identifier = identifier;
        this.name = name;
        this.photoPath = photoPath;
        this.usingAttendeeProfilePicture = usingAttendeeProfilePicture;
    }

    /**
     * Get the identifier for an organizer
     * @return string identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Set identifier for an organizer
     * @param identifier new identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Get the name of an organizer.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of an organizer
     * @param name new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the path in cloud storage to the organizer photo
     * @return path in cloud storage
     */
    public String getPhotoPath() {
        return photoPath;
    }

    /**
     * Set the path in cloud storage for the organizer photo
     * @param photoPath new path
     */
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    /**
     * Get whether or not the organizer is using an attendee profile picture
     * @return true if they are, false otherwise
     */
    public boolean isUsingAttendeeProfilePicture() {
        return usingAttendeeProfilePicture;
    }

    /**
     * Set whether the organizer is using an attendee profile picture
     * @param usingAttendeeProfilePicture new value
     */
    public void setUsingAttendeeProfilePicture(boolean usingAttendeeProfilePicture) {
        this.usingAttendeeProfilePicture = usingAttendeeProfilePicture;
    }

    /**
     * Update the organizer in the database
     */
    public void updateDBOrganizer() {
        MainActivity.db.getOrganizerRef().document(identifier).set(this);
    }

    /**
     * Syncs the data from the given attendee with this organizer. This can be used to fill in
     * default information about an organizer
     * @param attendee attendee object to sync with.
     */
    public void syncWithAttendee(Attendee attendee) {
        name = attendee.getFirstName() + " " + attendee.getLastName();
        photoPath = attendee.getProfilePhotoString();
        identifier = attendee.getIdentifier();
        usingAttendeeProfilePicture = attendee.getDefaultProfilePhoto();
    }
}