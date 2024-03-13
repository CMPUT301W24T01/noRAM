package com.example.noram;

import androidx.core.util.Pair;

import com.example.noram.model.Event;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Class to validate an event
 */
public class EventValidator implements Validator {

    /**
     * Validate an event object
     * @param event event object to validate
     * @return Pair where the first element is a boolean indicating validity, and the second is a string
     *         indicating the error message.
     */
    public static Pair<Boolean, String> validate(Event event) {
        boolean nameValid = !event.getName().isEmpty();
        boolean locationValid = !event.getLocation().isEmpty();
        boolean startValid = event.getStartTime() != null;
        boolean endValid = event.getEndTime() != null;
        boolean milestonesValid = event.getMilestones() != null;

        boolean valid = nameValid && locationValid && startValid && endValid && milestonesValid;
        return new Pair<Boolean, String>(valid, "");
    }

    /**
     * Validate an event from individual fields
     * @param name name of the event
     * @param location event location string
     * @param startDateTime start date
     * @param endDateTime end date
     * @param milestonesString string of milestones, comma separated.
     * @return true if valid, false otherwise
     */
    public static boolean validateFromFields(String name, String location, LocalDateTime startDateTime, LocalDateTime endDateTime, String milestonesString) {
        boolean nameValid = !name.isEmpty();
        boolean locationValid = !location.isEmpty();
        boolean startValid = startDateTime != null;
        boolean endValid = endDateTime != null;

        boolean milestonesValid = !milestonesString.isEmpty();
        for (char c : milestonesString.toCharArray()) {
            if (!(Character.isDigit(c) || c == ',')) {
                milestonesValid = false;
            }
        }
        return nameValid && locationValid && startValid && endValid && milestonesValid;
    }
}
