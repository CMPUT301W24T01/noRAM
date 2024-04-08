/* File for validating Event objects - provides multiple methods to validate an event object
* Outstanding Issues:
*  - None.
*/

package com.example.noram;

import androidx.core.util.Pair;

import com.example.noram.model.Event;

import java.time.LocalDateTime;

/**
 * Class to validate an event
 * @maintainer Cole
 * @author Cole
 */
public class EventValidator {
    /**
     * Validate an event object
     * @param event event object to validate
     * @return Pair where the first element is a boolean indicating validity, and the second is a string
     *         indicating the error message.
     */
    public static Pair<Boolean, String> validate(Event event) {
        boolean valid = true;
        String errMsg = "";

        // We check fields in opposite order they appear in the UI so that the first missing field
        // gives the error message.
        if (event.getSignUpLimit() >= 0 && event.getSignUpLimit() < event.getSignUpCount()) {
            valid = false;
            errMsg = "Sign-up limit is set below current number of signed-up attendees";
        }
        if (event.getMilestones() == null) {
            valid = false;
            errMsg = "Milestones is null";
        }
        if (event.getStartTime() != null && event.getEndTime() != null && event.getStartTime().isAfter(event.getEndTime())) {
            valid = false;
            errMsg = "Start time is after end time";
        }
        if (event.getEndTime() == null) {
            valid = false;
            errMsg = "End Time is empty";
        }
        if (event.getStartTime() == null) {
            valid = false;
            errMsg = "Start Time is empty";
        }
        if (event.getLocation().isEmpty()) {
            valid = false;
            errMsg = "Location is empty";
        }
        if (event.getName().isEmpty()) {
            valid = false;
            errMsg = "Name is empty";
        }

        return new Pair<>(valid, errMsg);
    }

    /**
     * Validate an event from individual fields
     * @param name name of the event
     * @param location event location string
     * @param startDateTime start date
     * @param endDateTime end date
     * @param milestonesString string of milestones, comma separated.
     * @param signUpLimit sign-up limit
     * @param currentSignUps current number of sign-ups
     * @return Pair where the first element is a boolean indicating validity, and the second is a string
     *         indicating the error message.
     */
    public static Pair<Boolean, String> validateFromFields(String name, String location, LocalDateTime startDateTime, LocalDateTime endDateTime, String milestonesString, Long signUpLimit, int currentSignUps) {
        boolean valid = true;
        String errMsg = "";
        if (signUpLimit >= 0 && signUpLimit < currentSignUps) {
            valid = false;
            errMsg = "Sign-up limit is set below current number of signed-up attendees";
        }
        for (char c : milestonesString.toCharArray()) {
            if (!(Character.isDigit(c) || c == ',' || Character.isWhitespace(c))) {
                valid = false;
                errMsg = "Milestones are invalid";
            }
        }
        if (startDateTime != null && endDateTime != null && startDateTime.isAfter(endDateTime)) {
            valid = false;
            errMsg = "Start time is after end time";
        }
        if (endDateTime == null) {
            valid = false;
            errMsg = "End Time is empty";
        }
        if (startDateTime == null) {
            valid = false;
            errMsg = "Start Time is empty";
        }
        if (location.isEmpty()) {
            valid = false;
            errMsg = "Location is empty";
        }
        if (name.isEmpty()) {
            valid = false;
            errMsg = "Name is empty";
        }

        return new Pair<>(valid, errMsg);
    }
}
