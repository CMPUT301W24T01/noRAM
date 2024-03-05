package com.example.noram;

import com.example.noram.model.Event;

/**
 * Interface for a class that listens to GoToEvent requests
 */
public interface GoToEventListener {
    /**
     * Request that the application goes to the event's page
     * @param event event to go to
     */
    void goToEvent(Event event);
}
