/*
This file is used to create an adapter that connects a ListView of attendees of an event
with an ArrayList of the attendees and the number of times they've checked in.
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

import com.example.noram.R;
import com.example.noram.model.AttendeeCheckInCounter;

import java.util.ArrayList;

/**
 * An adapter that connects a ListView of attendees and their check-in counts to an event with
 * an ArrayList of the attendees and their check-in counts.
 * A {@link EventAttendeeArrayAdapter} object is used to display a list of attendees in a ListView.
 * @maintainer Ethan
 * @author Ethan
 */
public class EventAttendeeArrayAdapter extends ArrayAdapter<AttendeeCheckInCounter> {
    private ArrayList<AttendeeCheckInCounter> attendeesAndCounts;
    private Context context;

    /**
     * A constructor to create an EventAttendeeArrayAdapter
     * @param context the context of the adapter
     * @param attendeesAndCounts an ArrayList of attendees and their check-in counts AttendeeCheckInCounter objects
     */
    public EventAttendeeArrayAdapter(Context context, ArrayList<AttendeeCheckInCounter> attendeesAndCounts) {
        super(context, 0, attendeesAndCounts);
        this.context = context;
        this.attendeesAndCounts = attendeesAndCounts;
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
            view = LayoutInflater.from(context).inflate(R.layout.event_attendee_list_item, parent,false);
        }

        // get attendee data
        AttendeeCheckInCounter attendeeAndCount = attendeesAndCounts.get(position);

        // get item's fields (UI)
        TextView attendeeName = view.findViewById(R.id.attendee_list_item_name);
        TextView checkInCountView = view.findViewById(R.id.attendee_list_check_in_count);

        // update fields and return view
        String nameString = attendeeAndCount.getAttendee().getFirstName() + " " + attendeeAndCount.getAttendee().getLastName();

        String checkInString;
        if (attendeeAndCount.getCheckInCount() == 1) {
            checkInString = "Checked-In: " + attendeeAndCount.getCheckInCount() + " Time";
        } else {
            checkInString = "Checked-In: " + attendeeAndCount.getCheckInCount() + " Times";
        }
        attendeeName.setText(nameString);
        checkInCountView.setText(checkInString);

        return view;
    }
}
