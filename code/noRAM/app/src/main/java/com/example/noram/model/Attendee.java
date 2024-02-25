package com.example.noram.model;

import java.net.URI;

/**
 * A class representing an attendee
 */
public class Attendee extends UserProfile {
    private int identifier;

    private String profilePic;

    /**
     * A constructor to create an attendee
     * @param identifier the identifier of the attendee
     */
    public Attendee(int identifier) {
        super(identifier);
    }

    public void setIdentifier(int idt) {
        this.identifier = idt;
    }
    public void setProfilePic(String uriStr) {
        this.profilePic = uriStr;
    }
    public int getIdentifier() {
        return this.identifier;
    }
    public String getProfilePic() {
        return this.profilePic;
    }

}
