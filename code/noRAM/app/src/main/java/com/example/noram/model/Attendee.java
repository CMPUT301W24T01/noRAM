package com.example.noram.model;

/**
 * A class representing an attendee
 */
public class Attendee extends UserProfile {
    private int identifier;

    /**
     * A constructor to create an attendee
     * @param identifier the identifier of the attendee
     */
    public Attendee(int identifier) {
        super(identifier);
    }

}
