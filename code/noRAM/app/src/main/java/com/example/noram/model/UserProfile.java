package com.example.noram.model;

/**
 * A class representing a user profile
 */
public abstract class UserProfile {
    private String identifier;

    /**
     * A constructor to create a user profile
     * @param identifier the identifier of the user
     */
    public UserProfile(String identifier) {
        this.identifier = identifier;
    }

}
