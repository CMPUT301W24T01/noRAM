/*
This file is used to create an interface for a class that listens to GoToEvent requests. This is used to allow the application to go to the event's page.
Outstanding Issues:
- None
 */

package com.example.noram;

/**
 * Interface for a class that listens to GoToEvent requests
 * @maintainer Cole
 * @author Cole
 */
public interface GoToEventListener {
    /**
     * Request that the application goes to the event's page
     * @param eventId id of the event to go to
     */
    void goToEvent(String eventId);

    /**
     * Request that the application goes to the event's confetti check-in page
     * @param eventId id of the event to go to
     */
    void goToConfetti(String eventId);
}
