/*
This file is used to create an adapter that connects a ListView of events with an ArrayList of events.
Outstanding Issues:
- None
 */

package com.example.noram.controller;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.noram.R;
import com.example.noram.model.Event;

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

        // update fields and return view
        eventTitle.setText(event.getName());
        LocalDateTime startTime = event.getStartTime();
        eventTime.setText(String.format("%s from %s - %s",
                startTime.format(DateTimeFormatter.ofPattern("MMMM dd")),
                startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                event.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        ));
        eventLocation.setText(event.getLocation());

        return view;
    }
}
