/* Class to Validate an Organizer
* Outstanding issues: none
* */
package com.example.noram;

import androidx.core.util.Pair;

import com.example.noram.model.Organizer;

/**
 * Class to validate organizer objects
 * @maintainer Cole
 * @author Cole
 */
public class OrganizerValidator {
    /**
     * Validate an organizer
     * @param organizer organizer object to validate
     * @return Pair where the first element is a boolean indicating validity, and the second is a string
     *         indicating the error message.
     */
    public static Pair<Boolean, String> validate(Organizer organizer) {
        return validateFromFields(organizer.getName(), organizer.getPhotoPath());
    }

    /**
     * Validate an organizer from individual field values
     * @param name organizer name
     * @param photoPath photo path in cloud storage
     * @return Pair where the first element is a boolean indicating validity, and the second is a string
     *         indicating the error message.
     */
    public static Pair<Boolean, String> validateFromFields(String name, String photoPath) {
        boolean valid = true;
        String errMsg = "";

        if (name.isEmpty()) {
            valid = false;
            errMsg = "Name is empty";
        }
        if (photoPath.isEmpty()) {
            valid = false;
            errMsg = "Photo path is missing";
        }
        return new Pair<>(valid, errMsg);
    }
}
