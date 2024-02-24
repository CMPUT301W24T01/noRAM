package com.example.noram.model;

/**
 * A class representing an attendee
 */
public class Attendee extends UserProfile {
    private int identifier;
    private String firstName;
    private String lastName;
    // Phone number is stored as a string to avoid overflow and to deal with any character if necessary
    private String phoneNumber;

    /**
     * A constructor to create an attendee
     * @param identifier the identifier of the attendee
     * @param firstName the first name of the attendee
     * @param lastName the last name of the attendee
     * @param phoneNumber the phone number of the attendee
     *
     */
    public Attendee(int identifier, String firstName, String lastName, String phoneNumber) {
        super(identifier);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
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
}
