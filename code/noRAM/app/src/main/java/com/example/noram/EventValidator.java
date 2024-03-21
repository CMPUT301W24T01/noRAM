package com.example.noram;

import androidx.core.util.Pair;

import com.example.noram.model.Event;

import java.time.LocalDateTime;

/**
 * Class to validate an event
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
        if (event.getName().isEmpty()) {
            valid = false;
            errMsg = "Name is empty";
        }
        if (event.getLocation().isEmpty()) {
            valid = false;
            errMsg = "Location is empty";
        }
        if (event.getStartTime() == null) {
            valid = false;
            errMsg = "Start Time is empty";
        }
        if (event.getEndTime() == null) {
            valid = false;
            errMsg = "End Time is empty";
        }
        if (event.getStartTime() != null && event.getEndTime() != null && event.getStartTime().isAfter(event.getEndTime())) {
            valid = false;
            errMsg = "Start time is after end time";
        }
        if (event.getMilestones() == null) {
            valid = false;
            errMsg = "Milestones is null";
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
     * @return Pair where the first element is a boolean indicating validity, and the second is a string
     *         indicating the error message.
     */
    public static Pair<Boolean, String> validateFromFields(String name, String location, LocalDateTime startDateTime, LocalDateTime endDateTime, String milestonesString) {
        boolean valid = true;
        String errMsg = "";
        if (name.isEmpty()) {
            valid = false;
            errMsg = "Name is empty";
        }
        if (location.isEmpty()) {
            valid = false;
            errMsg = "Location is empty";
        }
        if (startDateTime == null) {
            valid = false;
            errMsg = "Start Time is empty";
        }
        if (endDateTime == null) {
            valid = false;
            errMsg = "End Time is empty";
        }
        if (startDateTime != null && endDateTime != null && startDateTime.isAfter(endDateTime)) {
            valid = false;
            errMsg = "Start time is after end time";
        }
        
        for (char c : milestonesString.toCharArray()) {
            if (!(Character.isDigit(c) || c == ',')) {
                valid = false;
                errMsg = "Milestones are invalid";
            }
        }
        return new Pair<>(valid, errMsg);
    }
}
