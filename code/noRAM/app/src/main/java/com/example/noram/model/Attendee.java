package com.example.noram.model;

import com.example.noram.MainActivity;

/**
 * A class representing an attendee
 */
public class Attendee {
    private String identifier;
    private String firstName = "";
    private String lastName = "";
    private String homePage = "";
    private String email = "";
    private String profilePicture = "";

    private Boolean allowLocation = false;

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
     * @param profilePicture the profile picture of the attendee
     * @param allowLocation the location allowance of the attendee
     */
    public Attendee(String identifier, String firstName, String lastName, String homePage, String email, String profilePicture, Boolean allowLocation) {
        this.identifier = identifier;
        this.firstName = firstName;
        this.lastName = lastName;
        this.homePage = homePage;
        this.email = email;
        this.profilePicture = profilePicture;
        this.allowLocation = allowLocation;
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
     * A method to get the profile picture of the attendee
     * @return the profile picture of the attendee
     */
    public String getProfilePicture() {
        return profilePicture;
    }

    /**
     * A method to set the profile picture of the attendee
     * @param profilePicture the profile picture of the attendee
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
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
     * A method to update the database with the new attendee information
     */
    public void updateDBAttendee() {
        MainActivity.db.getAttendeeRef().document(identifier).set(this);
    }

}
