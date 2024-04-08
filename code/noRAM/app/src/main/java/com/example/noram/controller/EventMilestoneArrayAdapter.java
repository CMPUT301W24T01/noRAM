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
import com.example.noram.model.Milestone;

import java.util.ArrayList;

/**
 * An adapter that connects a ListView of event Milestones with an ArrayList of event milestone integers.
 * A {@link EventAttendeeArrayAdapter} object is used to display a list of milestones in a ListView.
 * @maintainer Ethan
 * @author Ethan
 */
public class EventMilestoneArrayAdapter extends ArrayAdapter<Milestone> {
    private ArrayList<Milestone> milestones;
    private Context context;

    /**
     * A constructor to create an EventMilestoneArrayAdapter
     * @param context the context of the adapter
     * @param milestones the milestones to be displayed
     */
    public EventMilestoneArrayAdapter(Context context, ArrayList<Milestone> milestones) {
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
        Integer milestone = Integer.valueOf(String.valueOf(milestones.get(position).getMilestone()));
        Integer attendeeCount = milestones.get(position).getProgress();

        // get item's fields (UI)
        TextView milestoneField = view.findViewById(R.id.event_milestone_list_item_title);
        TextView attendeeCountField = view.findViewById(R.id.event_milestone_progress_text);
        TextView milestoneAchievedField = view.findViewById(R.id.event_milestone_achieved_text);

        // set the milestone string
        String milestoneString;
        if (milestone == 1) {
            milestoneString = milestone + " Attendee";
        } else {
            milestoneString = milestone + " Attendees";
        }
        milestoneField.setText(milestoneString);

        // set the attendee progress count string
        if (attendeeCount >= milestone) {
            attendeeCountField.setVisibility(View.INVISIBLE);
            milestoneAchievedField.setVisibility(View.VISIBLE);
        } else {
            attendeeCountField.setVisibility(View.VISIBLE);
            milestoneAchievedField.setVisibility(View.INVISIBLE);
            String attendeeCountString;
            if (attendeeCount == 1) {
                attendeeCountString = attendeeCount + "/" + milestone + " Attendee";
            } else {
                attendeeCountString = attendeeCount + "/" + milestone + " Attendees";
            }
            attendeeCountField.setText(attendeeCountString);
        }
        return view;
    }
}
