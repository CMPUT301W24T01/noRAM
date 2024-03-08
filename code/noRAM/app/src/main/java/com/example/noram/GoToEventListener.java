/*
This file is used to create an interface for a class that listens to GoToEvent requests. This is used to allow the application to go to the event's page.
Outstanding Issues:
- None
 */

package com.example.noram;

import com.example.noram.model.Event;

/**
 * Interface for a class that listens to GoToEvent requests
 * @maintainer Cole
 * @author Cole
 */
public interface GoToEventListener {
    /**
     * Request that the application goes to the event's page
     * @param event event to go to
     */
    void goToEvent(Event event);
}
