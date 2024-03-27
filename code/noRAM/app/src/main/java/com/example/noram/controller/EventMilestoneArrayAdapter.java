/*
This file is used to create an adapter that connects a ListView of event Milestones
with an ArrayList of milestone integers.
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

import java.util.ArrayList;

/**
 * An adapter that connects a ListView of event Milestones with an ArrayList of event milestone integers.
 * A {@link EventAttendeeArrayAdapter} object is used to display a list of milestones in a ListView.
 * @maintainer Ethan
 * @author Ethan
 */
public class EventMilestoneArrayAdapter extends ArrayAdapter<Integer> {
    private ArrayList<Integer> milestones;
    private Context context;

    /**
     * A constructor to create an EventMilestoneArrayAdapter
     * @param context the context of the adapter
     * @param milestones the milestones to be displayed
     */
    public EventMilestoneArrayAdapter(Context context, ArrayList<Integer> milestones) {
        super(context, 0, milestones);
        this.context = context;
        this.milestones = milestones;
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
            view = LayoutInflater.from(context).inflate(R.layout.event_milestone_list_item, parent,false);
        }

        // get the milestone
        String milestone = String.valueOf(milestones.get(position));

        // get item's fields (UI)
        TextView milestoneField = view.findViewById(R.id.event_milestone_list_item_title);

        // set the milestone string
        String milestoneString;
        if (milestone.equals("1")) {
            milestoneString = milestone + " Attendee";
        } else {
            milestoneString = milestone + " Attendees";
        }
        milestoneField.setText(milestoneString);

        return view;
    }
}
