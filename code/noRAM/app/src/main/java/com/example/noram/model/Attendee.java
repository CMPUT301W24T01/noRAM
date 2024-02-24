package com.example.noram.model;

import android.media.Image;

/**
 * A class representing an attendee
 */
public class Attendee extends UserProfile {
    private int identifier;
    private String firstName;
    private String lastName;
    private String homePage;
    // Phone number is stored as a string to avoid overflow and to deal with any character if necessary
    private String phoneNumber;
    // TODO: ensure that Image is the correct type to use
    private Image profilePicture;
    private Boolean allowLocation;

    /**
     * A constructor to create an attendee
     * @param identifier the identifier of the attendee
     * @param firstName the first name of the attendee
     * @param lastName the last name of the attendee
     * @param phoneNumber the phone number of the attendee
     *
     */
    public Attendee(int identifier, String firstName, String lastName, String homePage, String phoneNumber, Image profilePicture, Boolean allowLocation) {
        super(identifier);
        this.firstName = firstName;
        this.lastName = lastName;
        this.homePage = homePage;
        this.phoneNumber = phoneNumber;
        this.profilePicture = profilePicture;
        this.allowLocation = allowLocation;
    }

    /**
     * A method to get the identifier of the attendee
     * @return the identifier of the attendee
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * A method to set the identifier of the attendee
     * @param identifier the identifier of the attendee
     */
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
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
    }

    /**
     * A method to get the phone number of the attendee
     * @return the phone number of the attendee
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * A method to set the phone number of the attendee
     * @param phoneNumber the phone number of the attendee
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * A method to get the profile picture of the attendee
     * @return the profile picture of the attendee
     */
    public Image getProfilePicture() {
        return profilePicture;
    }

    /**
     * A method to set the profile picture of the attendee
     * @param profilePicture the profile picture of the attendee
     */
    public void setProfilePicture(Image profilePicture) {
        this.profilePicture = profilePicture;
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
    }
}
