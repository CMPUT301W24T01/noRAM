package com.example.noram.controller;

import android.content.Context;
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
import java.util.ArrayList;

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
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.event_list_item, parent,false);
        }

        Event event = events.get(position);

        TextView eventTitle = view.findViewById(R.id.event_title);
        TextView eventTime = view.findViewById(R.id.event_time);
        TextView eventLocation = view.findViewById(R.id.event_location);

        eventTitle.setText(event.getName());
        // TODO: Edit time for correct format
        eventTime.setText(event.getStartTime() + " to " + event.getEndTime());
        eventLocation.setText(event.getLocation());

        return view;
    }
}
