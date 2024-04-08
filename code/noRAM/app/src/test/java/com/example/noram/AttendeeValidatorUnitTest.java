/*
* Contains unit tests for the Attendee Validator Class.
* Outstanding Issues:
* - None
*/

package com.example.noram;

import static org.junit.jupiter.api.Assertions.assertEquals;

import androidx.core.util.Pair;

import com.example.noram.model.Attendee;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Unit tests for the AttendeeValidator
 * @maintainer Cole
 * @author Cole
 */
public class AttendeeValidatorUnitTest {
    /**
     * Tests that the AttendeeValidator properly validates an attendee object
     * @param attendee attendee object being validated
     * @param expectedResult expected validation result
     */
    @ParameterizedTest
    @MethodSource("objectProvideParameter")
    public void eventValidatorObjectTest(Attendee attendee, boolean expectedResult) {
        Pair<Boolean, String> result = AttendeeValidator.validate(attendee);
        assertEquals(expectedResult, result.first);
    }

    /**
     * Provides parameters for the eventValidatorObjectTest
     * @return stream of arguments for the test
     */
    private static Stream<Arguments> objectProvideParameter() {
        return Stream.of(
                Arguments.of(new Attendee("id", "Bill", "Smith", "homepage", "bill@smith.com", false, true, null), true),
                Arguments.of(new Attendee("id", "", "Smith", "homepage", "bill@smith.com", false, true, null), false),
                Arguments.of(new Attendee("id", "Bill", "", "homepage", "bill@smith.com", false, true, null), false),
                Arguments.of(new Attendee("id", "Bill", "Smith", "homepage", "", false, true, null), false),
                Arguments.of(new Attendee("id", "Bill", "Smith", "homepage", "william:DL:D", false, true, null), false)
        );
    }

    /**
     * Validate the AttendeeValidator validateFromFields method.
     * @param first first name
     * @param last last name
     * @param email email
     * @param expectedResult expected result of test
     */
    @ParameterizedTest
    @MethodSource("fieldsProvideParameter")
    public void attendeeValidatorFieldsTest(String first, String last, String email, boolean expectedResult) {
        Pair<Boolean, String> result = AttendeeValidator.validateFromFields(first, last, email);
        assertEquals(expectedResult, result.first);
    }

    /**
     * Argument provider for the eventValidatorFieldsTest
     * @return Stream of arguments for the parameterized test
     */
    private static Stream<Arguments> fieldsProvideParameter() {
        return Stream.of(
                Arguments.of("Bill", "Smith", "bill@smith.com", true),
                Arguments.of("Bill", "", "bill@smith.com", false),
                Arguments.of("", "Smith", "bill@smith.com", false),
                Arguments.of("Bill", "Smith", "", false),
                Arguments.of("Bill", "Smith", "william!!!!", false)
        );
    }
}
