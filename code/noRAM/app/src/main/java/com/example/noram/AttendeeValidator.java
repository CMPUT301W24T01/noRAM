package com.example.noram;

import android.util.Patterns;

import com.example.noram.model.Attendee;

/**
 * Class to validate an attendee
 */
public class AttendeeValidator implements Validator{
    /**
     * Validate an attendee
     * @param attendee attendee object to validate
     * @return true if valid, false otherwise
     */
    public static boolean validate(Attendee attendee) {
        return validateFromFields(attendee.getFirstName(), attendee.getLastName(), attendee.getEmail());
    }

    /**
     * Validate an attendee from individual field values
     * @param firstName first name for attendee
     * @param lastName last name for attendee
     * @param email email for attendee
     * @return true if valid, false otherwise
     */
    public static boolean validateFromFields(String firstName, String lastName, String email) {
        boolean firstNameValid = !firstName.isEmpty();
        boolean lastNameValid = !lastName.isEmpty();
        boolean emailValid = !email.isEmpty()
                && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        return firstNameValid && lastNameValid && emailValid;
    }


}
