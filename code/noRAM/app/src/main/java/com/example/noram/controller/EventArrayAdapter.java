package com.example.noram.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.noram.R;
import com.example.noram.model.Event;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * An adapter that connects a ListView of events with an ArrayList of events.
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;
    public EventArrayAdapter(Context context, ArrayList<Event> events){
        super(context,0, events);
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.event_list_item, parent,false);
        }

        // event data
        Event event = events.get(position);

        // item's fields (UI)
        TextView eventTitle = view.findViewById(R.id.event_title);
        TextView eventTime = view.findViewById(R.id.event_time);
        TextView eventLocation = view.findViewById(R.id.event_location);

        // update fields and return view
        eventTitle.setText(event.getName());
        LocalDateTime startTime = event.getStartTime();
        eventTime.setText(String.format("%s from %s - %s",
                startTime.format(DateTimeFormatter.ofPattern("MMMM dd")),
                startTime.format(DateTimeFormatter.ofPattern("HH:mma")),
                event.getEndTime().format(DateTimeFormatter.ofPattern("HH:mma"))
        ));
        eventLocation.setText(event.getLocation());

        return view;
    }
}
