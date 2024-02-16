package com.example.noram.model;

/**
 * A class representing a user profile
 */
public abstract class UserProfile {
    private int identifier;

    /**
     * A constructor to create a user profile
     * @param identifier the identifier of the user
     */
    public UserProfile(int identifier) {
        this.identifier = identifier;
    }

}
