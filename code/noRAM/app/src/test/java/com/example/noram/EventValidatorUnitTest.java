/* Class containing unit tests for the EventValidator object. */
package com.example.noram;

import static org.junit.jupiter.api.Assertions.assertEquals;

import androidx.core.util.Pair;

import com.example.noram.model.Event;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Unit tests for the EventValidator class
 * @maintainer Cole
 * @author Cole
 */
public class EventValidatorUnitTest {
    /**
     * Tests that the eventValidatorObject properly validates an Event object
     * @param event event object being validated
     * @param expectedResult expected validation result
     */
    @ParameterizedTest
    @MethodSource("objectProvideParameter")
    public void eventValidatorObjectTest(Event event, boolean expectedResult) {
        Pair<Boolean, String> result = EventValidator.validate(event);
        assertEquals(expectedResult, result.first);
    }

    /**
     * Provides parameters for the eventValidatorObjectTest
     * @return
     */
    private static Stream<Arguments> objectProvideParameter() {
        return Stream.of(
                Arguments.of(new Event("id", "My Event", "My House", LocalDateTime.now(), LocalDateTime.now(), "cool details", new ArrayList<>(), null, null, false, new ArrayList<>(), "organizerId", new ArrayList<>(), -1L, -1L), true),
                Arguments.of(new Event("id", "", "My House", LocalDateTime.now(), LocalDateTime.now(), "cool details", new ArrayList<>(), null, null, false, new ArrayList<>(), "organizerId", new ArrayList<>(), -1L, -1L), false),
                Arguments.of(new Event("id", "My Event", "", LocalDateTime.now(), LocalDateTime.now(), "cool details", new ArrayList<>(), null, null, false, new ArrayList<>(), "organizerId", new ArrayList<>(), -1L, -1L), false),
                Arguments.of(new Event("id", "My Event", "My House", null, LocalDateTime.now(), "cool details", new ArrayList<>(), null, null, false, new ArrayList<>(), "organizerId", new ArrayList<>(), -1L, -1L), false),
                Arguments.of(new Event("id", "My Event", "My House", LocalDateTime.now(), null, "cool details", new ArrayList<>(), null, null, false, new ArrayList<>(), "organizerId", new ArrayList<>(), -1L, -1L), false),
                Arguments.of(new Event("id", "My Event", "My House", LocalDateTime.now(), LocalDateTime.now(), "cool details", null, null, null, false, new ArrayList<>(), "organizerId", new ArrayList<>(), -1L, -1L), false),
                Arguments.of(new Event("id", "My Event", "My House", LocalDateTime.now(), LocalDateTime.of(2002, 2, 2, 2, 2), "cool details", new ArrayList<>(), null, null, false, new ArrayList<>(), "organizerId", new ArrayList<>(), -1L, -1L), false),
                Arguments.of(new Event("id", "My Event", "My House", LocalDateTime.now(), LocalDateTime.of(2002, 2, 2, 2, 2), "cool details", new ArrayList<>(), null, null, false, new ArrayList<>(), "organizerId", new ArrayList<>(Arrays.asList("a", "b","c")), 0L, -1L), false)
        );
    }

    /**
     * Tests the event validtor validateFromFields method
     * @param name event name
     * @param location event location
     * @param start start for event
     * @param end end of event
     * @param milestones string to be parsed as a milestones list
     * @param expectedResult the expected result of the test.
     */
    @ParameterizedTest
    @MethodSource("fieldsProvideParameter")
    public void eventValidatorFieldsTest(String name, String location, LocalDateTime start, LocalDateTime end, String milestones, Long signUpLimit, int currentSignUps, boolean expectedResult) {
        Pair<Boolean, String> result = EventValidator.validateFromFields(name, location, start, end, milestones, signUpLimit, currentSignUps);
        assertEquals(expectedResult, result.first);
    }

    /**
     * Argument provider for the eventValidatorFieldsTest
     * @return Stream of arguments for the parameterized test
     */
    private static Stream<Arguments> fieldsProvideParameter() {
        return Stream.of(
                Arguments.of("Bill", "My House", LocalDateTime.now(), LocalDateTime.now(), "1,2,10,50,100", -1L, 0, true),
                Arguments.of("", "My House", LocalDateTime.now(), LocalDateTime.now(), "1,2,10,50,100", -1L, 0, false),
                Arguments.of("Bill", "", LocalDateTime.now(), LocalDateTime.now(), "1,2,10,50,100", -1L, 0, false),
                Arguments.of("Bill", "My House", null, LocalDateTime.now(), "1,2,10,50,100", -1L, 0, false),
                Arguments.of("Bill", "My House", LocalDateTime.now(), null, "1,2,10,50,100", -1L, 0, false),
                Arguments.of("Bill", "My House", LocalDateTime.now(), LocalDateTime.now(), "apple", -1L, 0, false),
                Arguments.of("Bill", "My House", LocalDateTime.now(), LocalDateTime.of(2002, 2, 2, 2, 2), "1,2,3,4", -1L, 0, false),
                Arguments.of("Bill", "My house", LocalDateTime.now(), LocalDateTime.now(), "1,2,3,4", 0L, 1, false)
        );
    }


}
