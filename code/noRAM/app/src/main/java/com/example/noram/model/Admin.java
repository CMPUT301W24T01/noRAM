/*
This file is used to create an admin object and store the admin's identifier. This is used to check if the user is an admin or not.
Outstanding Issues:
- None
 */

package com.example.noram.model;

/**
 * A class representing an admin
 * @maintainer Christiaan
 * @author Christiaan
 */
public class Admin {
    private String identifier;

    /**
     * A constructor to create an admin
     * @param identifier the identifier of the admin
     */
    public Admin(String identifier) {
        this.identifier = identifier;
    }

}
