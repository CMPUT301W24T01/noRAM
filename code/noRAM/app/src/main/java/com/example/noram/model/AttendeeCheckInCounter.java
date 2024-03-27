/*
This file is used to represent the number of times an attendee has checked into an event
and provide methods to interact with it.
Outstanding Issues:
- None
 */
package com.example.noram.model;
/**
 * A class to represent the number of times an attendee has checked into an event.
 * @maintainer Ethan
 * @author Ethan
 */
public class AttendeeCheckInCounter {
    private Attendee attendee;
    private int checkInCount;

    /**
     * A constructor to create an AttendeeCheckInCounter
     * @param checkInCount the number of times the attendee has checked in
     * @param attendee the attendee
     */
    public AttendeeCheckInCounter(Attendee attendee, int checkInCount) {
        this.attendee = attendee;
        this.checkInCount = checkInCount;
    }

    /**
     * A method to get the check in count
     * @return the check in count
     */
    public int getCheckInCount() {
        return checkInCount;
    }

    /**
     * A method to set the check in count
     * @param checkInCount the check in count
     */
    public void setCheckInCount(int checkInCount) {
        this.checkInCount = checkInCount;
    }

    /**
     * A method to get the attendee
     * @return the attendee
     */
    public Attendee getAttendee() {
        return attendee;
    }

    /**
     * A method to set the attendee
     * @param attendee the attendee
     */
    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }
}
