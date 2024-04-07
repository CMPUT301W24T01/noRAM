/*
This file is used to create an Attendee object. This object is used to store information about an attendee.
Outstanding Issues:
- Generate proflie picture duplicated for some functionality, may want to decouple database from setters
 */

package com.example.noram.model;

import android.util.Log;

import com.example.noram.MainActivity;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A class representing an attendee
 * @maintainer Christiaan
 * @author Christiaan
 * @author Ethan
 * @author Cole
 */
public class Attendee {
    private String identifier;
    private String firstName = "";
    private String lastName = "";
    private String homePage = "";
    private String email = "";
    private Boolean allowLocation = false;
    private List<String> eventsCheckedInto = new ArrayList<>();
    private Boolean usingDefaultProfilePhoto = true;
    private String FCMToken = "";

    /**
     * A constructor to create an attendee with just an identifier
     * @param identifier the identifier of the attendee
     */
    public Attendee(String identifier) {
        this.identifier = identifier;
    }

    /**
     * A constructor to create an attendee with all field filled
     * @param identifier the identifier of the attendee
     * @param firstName the first name of the attendee
     * @param lastName the last name of the attendee
     * @param homePage the home page of the attendee
     * @param email the email of the attendee
     * @param allowLocation the location allowance of the attendee
     * @param defaultPhoto whether or not the attendee is using the default profile photo
     * @param eventsCheckedInto the events the attendee is checked into
     */
    public Attendee(String identifier, String firstName, String lastName, String homePage, String email, Boolean allowLocation, Boolean defaultPhoto, List<String> eventsCheckedInto) {
        this.identifier = identifier;
        this.firstName = firstName;
        this.lastName = lastName;
        this.homePage = homePage;
        this.email = email;
        this.allowLocation = allowLocation;
        this.eventsCheckedInto = eventsCheckedInto;
        this.usingDefaultProfilePhoto = defaultPhoto;
    }

    /**
     * A method to get the identifier of the attendee
     * @return the identifier of the attendee
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * A method to set the identifier of the attendee
     * @param identifier the identifier of the attendee
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        updateDBAttendee();
    }

    /**
     * A method to get the first name of the attendee
     * @return the first name of the attendee
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * A method to set the first name of the attendee
     * @param firstName the first name of the attendee
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateDBAttendee();
    }

    /**
     * A method to get the last name of the attendee
     * @return the last name of the attendee
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * A method to set the last name of the attendee
     * @param lastName the last name of the attendee
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateDBAttendee();
    }

    /**
     * A method to get the home page of the attendee
     * @return the home page of the attendee
     */
    public String getHomePage() {
        return homePage;
    }

    /**
     * A method to set the home page of the attendee
     * @param homePage the home page of the attendee
     */
    public void setHomePage(String homePage) {
        this.homePage = homePage;
        updateDBAttendee();
    }

    /**
     * A method to get the email of the attendee
     * @return the email of the attendee
     */
    public String getEmail() {
        return email;
    }

    /**
     * A method to set the email of the attendee
     * @param email the email of the attendee
     */
    public void setEmail(String email) {
        this.email = email;
        updateDBAttendee();
    }

    /**
     * A method to get the location allowance of the attendee
     * @return the location allowance of the attendee
     */
    public Boolean getAllowLocation() {
        return allowLocation;
    }

    /**
     * A method to set the location allowance of the attendee
     * @param allowLocation the location allowance of the attendee
     */
    public void setAllowLocation(Boolean allowLocation) {
        this.allowLocation = allowLocation;
        updateDBAttendee();
    }

    /**
     * Get the list of events an attendee is checked into
     * @return List of event ids
     */
    public List<String> getEventsCheckedInto() {
        return eventsCheckedInto;
    }

    /**
     * Set the list of events an attendee is checked into
     * @param eventsCheckedInto new list
     */
    public void setEventsCheckedInto(List<String> eventsCheckedInto) {
        this.eventsCheckedInto = eventsCheckedInto;
        updateDBAttendee();
    }

    /**
     * A method to update the database with the new attendee information
     */
    public void updateDBAttendee() {
        MainActivity.db.getAttendeeRef().document(identifier).set(this);
    }

    /**
     * Returns whether or not we are currently using a default profile photo.
     * @return True if yes, false otherwise.
     */
    public Boolean getDefaultProfilePhoto() {
        return usingDefaultProfilePhoto;
    }

    /**
     * Set whether we are currently using a default profile photo
     * @param defaultProfilePhoto new value.
     */
    public void setDefaultProfilePhoto(Boolean defaultProfilePhoto) {
        this.usingDefaultProfilePhoto = defaultProfilePhoto;
        updateDBAttendee();
    }

    /**
     * Gets the string for the attendee's profile photo in cloud storage
     * @return string filepath in cloud storage.
     */
    public String getProfilePhotoString() {
        if (usingDefaultProfilePhoto) {
            return "profile_photos/" + getIdentifier() + "-default";
        } else {
            return "profile_photos/" + getIdentifier() + "-upload";
        }
    }

    /**
     * Generate a default name for the attendee if they do not have one
     */
    public void generateDefaultName() {
        if (firstName.isEmpty() && lastName.isEmpty()) {
            int hashIdentifier = identifier.hashCode();
            String name = "User#" + hashIdentifier;
            if (name.length() > 10) {
                name = name.substring(0, 10);
            }
            firstName = name;
        }
    }

    /**
     * Set the FCM token for the attendee
     * @param FCMToken new token
     */
    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
        updateDBAttendee();
    }

    /**
     * Get the FCM token for the attendee
     * @return the FCM token
     */
    public String getFCMToken() {
        return FCMToken;
    }

    /**
     * A method to generate the FCM token for the attendee
     */
    public void generateAttendeeFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                return;
            }
            // Get new FCM registration token
            String token = task.getResult();
            setFCMToken(token);
            Log.d("FCM", token);
        });
    }

    /**
     * A method to update the attendee with a Map object
     * @param map the Map with information about the attendee attributes
     */
    public void updateWithMap(Map<String, Object> map) {
        this.firstName = (String) map.get("firstName");
        this.lastName = (String) map.get("lastName");
        this.homePage = (String) map.get("homePage");
        this.email = (String) map.get("email");
        this.allowLocation = (Boolean) map.get("allowLocation");
        this.usingDefaultProfilePhoto = (Boolean) map.get("defaultProfilePhoto");
        this.eventsCheckedInto = (List<String>) map.get("eventsCheckedInto");
        this.FCMToken = (String) map.get("FCMToken");
    }
}