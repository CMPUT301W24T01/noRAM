/*
This file is used to create an organizer object. This object is used to store the identifier of the organizer.
Outstanding Issues:
- None
 */

package com.example.noram.model;

/**
 * A class representing an organizer
 * @maintainer Christiaan
 * @author Christiaan
 * @author Ethan
 */
public class Organizer {
    private String identifier;

    /**
     * A constructor to create an organizer
     * @param identifier the identifier of the organizer
     */
    public Organizer(String identifier) {
        this.identifier = identifier;
    }

}