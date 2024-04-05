/*
This file is used to compare events by checking if they are happening right now.
Outstanding Issues:
- None
 */

package com.example.noram;

import android.util.Log;

import com.example.noram.model.Event;

import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Comparator that compares two event based on if they are happening right now
 */
public class EventTimeComparator implements Comparator<Event> {

    /**
     * Check if an event is happening right now
     * @param event Event who is being checked
     * @return Returns true if the event is happening right now, false otherwise
     */
    private boolean happeningNow(Event event){
        LocalDateTime currentTime = LocalDateTime.now();
        return event.getStartTime().isBefore(currentTime) && event.getEndTime().isAfter(currentTime);
    }

    /**
     * Compare two events based by checking if they are happening right now or not.
     * To have "NOW" event in the first positions of a given list, an event is considered lower in
     * value if it is happening right now
     * @param event1 the first object to be compared.
     * @param event2 the second object to be compared.
     * @return Integer indicating if event1 was bigger, lower or equal to event2
     */
    @Override
    public int compare(Event event1, Event event2){
        boolean event1Now = happeningNow(event1);
        boolean event2Now = happeningNow(event2);

        // if same result, use UID instead to sort
        if(event1Now==event2Now){
            return event1.getId().compareTo(event2.getId());
        } else if(event1Now){
            // event1 was happening now
            return -1;
        }
        else{
            // event2 was happening now
            return 1;
        }
    }
}
