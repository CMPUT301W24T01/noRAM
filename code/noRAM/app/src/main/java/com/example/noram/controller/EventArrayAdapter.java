/*
This file is used to create an adapter that connects a ListView of events with an ArrayList of events.
Outstanding Issues:
- None
 */

package com.example.noram.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.noram.MainActivity;
import com.example.noram.R;
import com.example.noram.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * An adapter that connects a ListView of events with an ArrayList of events.
 * A {@link EventArrayAdapter} object is used to display a list of events in a ListView.
 * @maintainer Gabriel
 * @author Gabriel
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    /**
     * A constructor to create an EventArrayAdapter
     * @param context the context of the adapter
     * @param events the events to be displayed
     */
    public EventArrayAdapter(Context context, ArrayList<Event> events){
        super(context,0, events);
        this.events = events;
        this.context = context;
    }

    /**
     * A method to get the view of the adapter
     * @param position the position of the view
     * @param convertView the view to be converted
     * @param parent the parent of the view
     * @return the view of the adapter
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.event_list_item, parent,false);
        }

        // get event data
        Event event = events.get(position);

        // get item's fields (UI)
        TextView eventTitle = view.findViewById(R.id.event_title);
        TextView eventTime = view.findViewById(R.id.event_time);
        TextView eventLocation = view.findViewById(R.id.event_location);
        TextView eventSignUpCapacity = view.findViewById(R.id.event_signUp_capacity);
        TextView signedUpText = view.findViewById(R.id.event_signed_up_indicator);
        TextView checkedInText = view.findViewById(R.id.event_checked_in_indicator);

        // update fields and return view
        eventTitle.setText(event.getName());
        LocalDateTime startTime = event.getStartTime();
        LocalDateTime endTime = event.getEndTime();
        if (startTime.toLocalDate().equals(endTime.toLocalDate())) {
            eventTime.setText(String.format("%s \n%s to %s",
                    startTime.format(DateTimeFormatter.ofPattern("MMM dd")),
                    startTime.format(DateTimeFormatter.ofPattern("h:mma")),
                    endTime.format(DateTimeFormatter.ofPattern("h:mma"))
            ));
        } else {
            // not the same date: need to include both dates
            eventTime.setText(String.format("%s at %s to \n%s at %s",
                    startTime.format(DateTimeFormatter.ofPattern("MMM dd")),
                    startTime.format(DateTimeFormatter.ofPattern("h:mm a")),
                    endTime.format(DateTimeFormatter.ofPattern("MMM dd")),
                    endTime.format(DateTimeFormatter.ofPattern("h:mm a"))
            ));
        }

        eventLocation.setText(event.getLocation());
        if (event.isLimitedSignUps()) {
            eventSignUpCapacity.setText(String.format(
                    getContext().getString(R.string.signup_limit_format),
                    event.getSignUpCount(),
                    event.getSignUpLimit())
            );
        }
        else {
            eventSignUpCapacity.setText(String.format(
                    getContext().getString(R.string.signup_count_format),
                    event.getSignUpCount())
            );
        }

        if (event.getCheckedInAttendees().contains(MainActivity.attendee.getIdentifier())) {
            checkedInText.setVisibility(View.VISIBLE);
        } else {
            checkedInText.setVisibility(View.INVISIBLE);
        }
        if (event.getSignedUpAttendees().contains(MainActivity.attendee.getIdentifier())) {
            signedUpText.setVisibility(View.VISIBLE);
        } else {
            signedUpText.setVisibility(View.INVISIBLE);
        }

        return view;
    }
}
