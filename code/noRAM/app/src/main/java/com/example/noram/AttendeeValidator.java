package com.example.noram;

import androidx.core.util.Pair;
import androidx.core.util.PatternsCompat;

import com.example.noram.model.Attendee;

/**
 * Class to validate an attendee
 */
public class AttendeeValidator {
    /**
     * Validate an attendee
     * @param attendee attendee object to validate
     * @return Pair where the first element is a boolean indicating validity, and the second is a string
     *         indicating the error message.
     */
    public static Pair<Boolean, String> validate(Attendee attendee) {
        return validateFromFields(attendee.getFirstName(), attendee.getLastName(), attendee.getEmail());
    }

    /**
     * Validate an attendee from individual field values
     * @param firstName first name for attendee
     * @param lastName last name for attendee
     * @param email email for attendee
     * @return Pair where the first element is a boolean indicating validity, and the second is a string
     *         indicating the error message.
     */
    public static Pair<Boolean, String> validateFromFields(String firstName, String lastName, String email) {
        boolean valid = true;
        String errMsg = "";
        if (firstName.isEmpty()) {
            valid = false;
            errMsg = "First name is empty";
        }
        if (lastName.isEmpty()) {
            valid = false;
            errMsg = "Last name is empty";
        }
        if (email.isEmpty() || !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            valid = false;
            errMsg = "Email is invalid";
        }
        return new Pair<>(valid, errMsg);
    }
}
