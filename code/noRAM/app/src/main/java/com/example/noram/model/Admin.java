package com.example.noram.model;

/**
 * A class representing an admin
 */
public class Admin extends UserProfile {
    private String identifier;

    /**
     * A constructor to create an admin
     * @param identifier the identifier of the admin
     */
    public Admin(String identifier) {
        super(identifier);
    }

}
