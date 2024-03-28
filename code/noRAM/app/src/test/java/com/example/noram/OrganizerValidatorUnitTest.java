/* Class to hold all unit tests for the Organizer validator.
* Outstanding Issues: None
*  */
package com.example.noram;

import static org.junit.jupiter.api.Assertions.assertEquals;

import androidx.core.util.Pair;

import com.example.noram.model.Organizer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Unit tests for the OrganizerValidator class
 * @maintainer Cole
 * @author Cole
 */
public class OrganizerValidatorUnitTest {
    /**
     * Tests that the OrganizerValidator properly validates an attendee object
     * @param organizer attendee object being validated
     * @param expectedResult expected validation result
     */
    @ParameterizedTest
    @MethodSource("objectProvideParameter")
    public void organizerValidatorObjectTest(Organizer organizer, boolean expectedResult) {
        Pair<Boolean, String> result = OrganizerValidator.validate(organizer);
        assertEquals(expectedResult, result.first);
    }

    /**
     * Provides parameters for the eventValidatorObjectTest
     * @return stream of arguments for the test
     */
    private static Stream<Arguments> objectProvideParameter() {
        return Stream.of(
                Arguments.of(new Organizer("id", "John Johnathons", "myCoolPhoto"), true),
                Arguments.of(new Organizer("id", "", "myCoolPhoto"), false),
                Arguments.of(new Organizer("id", "John Johnathons", ""), false)

        );
    }

    /**
     * Validate the OrganizerValidator validateFromFields method.
     * @param name organizer name
     * @param photoPath last name
     * @param expectedResult expected result of test
     */
    @ParameterizedTest
    @MethodSource("fieldsProvideParameter")
    public void organizerValidatorFieldsTest(String name, String photoPath, boolean expectedResult) {
        Pair<Boolean, String> result = OrganizerValidator.validateFromFields(name, photoPath);
        assertEquals(expectedResult, result.first);
    }

    /**
     * Argument provider for the organizerValidatorFieldsTest
     * @return Stream of arguments for the parameterized test
     */
    private static Stream<Arguments> fieldsProvideParameter() {
        return Stream.of(
                Arguments.of("Bill Smith", "myphoto", true),
                Arguments.of("Bill", "", false),
                Arguments.of("", "myphoto", false)
        );
    }
}
