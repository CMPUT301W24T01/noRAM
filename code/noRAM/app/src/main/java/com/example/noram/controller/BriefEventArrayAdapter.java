/* Class for adapting an event name to the Brief Event list on the organizer dashboard.
* Outstanding Issues: None
* */
package com.example.noram.controller;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.noram.R;
import com.example.noram.model.Event;

import java.util.ArrayList;

/**
 * Class for adapting an event name to an item in the BriefEvent list on the organizer dashboard
 * @maintainer Cole
 * @author Cole
 */
public class BriefEventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    /**
     * A constructor to create an EventArrayAdapter
     * @param context the context of the adapter
     * @param events the name of the events to be displayed
     */
    public BriefEventArrayAdapter(Context context, ArrayList<Event> events){
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
            view = LayoutInflater.from(context).inflate(R.layout.brief_event_list_item, parent,false);
        }

        // get event data
        Event event = events.get(position);

        // get item's fields (UI)
        TextView eventTitle = view.findViewById(R.id.event_name_text);

        // underline the text
        SpannableString underlinedName = new SpannableString(event.getName());
        underlinedName.setSpan(new UnderlineSpan(), 0, event.getName().length(), 0);

        // update fields and return view
        eventTitle.setText(underlinedName);
        return view;
    }
}
