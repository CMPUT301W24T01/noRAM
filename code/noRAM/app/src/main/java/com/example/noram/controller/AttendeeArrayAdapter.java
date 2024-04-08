/*
This file is used to create ArrayList Adapters that adapt array list of Attendees.
It is mainly used in AdminImagesFragment.
Outstanding Issues:
- None
 */

package com.example.noram.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.noram.R;
import com.example.noram.model.AdminPhoto;
import com.example.noram.model.Attendee;
import java.util.ArrayList;

/**
 * An adapter that connects a ListView of Attendees with an ArrayList of Attendees.
 * A {@link AttendeeArrayAdapter} object is used to display a list of Attendees  in a ListView.
 * @maintainer Gabriel
 * @author Gabriel
 */
public class AttendeeArrayAdapter extends ArrayAdapter<Attendee> {
    private ArrayList<Attendee> attendees;
    private Context context;
    public enum Format{FULL, IDHIDDEN}
    private Format format;

    /**
     * A constructor to create an AttendeeArrayAdapter
     * @param context the context of the adapter
     * @param attendees the attendees to be displayed
     */
    public AttendeeArrayAdapter(Context context, ArrayList<Attendee> attendees){
        super(context,0, attendees);
        this.attendees = attendees;
        this.context = context;
        this.format = Format.FULL;
    }

    /**
     * A constructor to create an AttendeeArrayAdapter with a specified format (default is 0)
     * @param context the context of the adapter
     * @param attendees the attendees to be displayed
     * @param format designates which format to use
     */
    public AttendeeArrayAdapter(Context context, ArrayList<Attendee> attendees, Format format) {
        super(context, 0, attendees);
        this.attendees = attendees;
        this.context = context;
        this.format = format;
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
            view = LayoutInflater.from(context).inflate(R.layout.attendee_list_item, parent,false);
        }

        // get attendee data
        Attendee attendee = attendees.get(position);

        // create attendee's photo
        AdminPhoto photo = new AdminPhoto();
        photo.setPhotoPath(attendee.getProfilePhotoString());
        photo.setBitmapFromDB(context);

        // get item's fields (UI)
        TextView attendeeName = view.findViewById(R.id.attendee_name);
        TextView attendeeID = view.findViewById(R.id.attendee_id);
        TextView attendeeHomepage = view.findViewById(R.id.attendee_homepage);
        TextView attendeeEmail = view.findViewById(R.id.attendee_email);
        ImageView attendeePhoto = view.findViewById(R.id.attendee_photo);

        // update fields and return view
        String attendeeNameString = attendee.getFirstName() + " " + attendee.getLastName();
        attendeeID.setText(attendee.getIdentifier());
        attendeeName.setText(attendeeNameString);
        attendeeHomepage.setText(attendee.getHomePage());
        attendeeEmail.setText(attendee.getEmail());
        photo.updateWithBitmap(attendeePhoto);

        // Hide views based on format
        if (format == Format.IDHIDDEN) {
            attendeeID.setVisibility(View.GONE);
        }

        return view;
    }
}
