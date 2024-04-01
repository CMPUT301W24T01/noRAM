/*
This file is used to create an adapter that connects a ListView of notifications with an ArrayList of notifications.
Outstanding Issues:
- None
 */

package com.example.noram.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.noram.R;
import com.example.noram.model.Event;
import com.example.noram.model.Notification;

import java.util.ArrayList;

/**
 * An adapter that connects a ListView of notifications with an ArrayList of notifications.
 * A {@link NotificationArrayAdapter} object is used to display a list of notifications in a ListView.
 * @maintainer Christiaan
 * @author Christiaan
 */
public class NotificationArrayAdapter extends ArrayAdapter<Notification> {

    private ArrayList<Notification> notifications;
    private Context context;

    /**
     * A constructor to create a NotificationArrayAdapter
     * @param context the context of the adapter
     * @param notifications the notifications to be displayed
     */
    public NotificationArrayAdapter(Context context, ArrayList<Notification> notifications){
        super(context,0, notifications);
        this.notifications = notifications;
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
            view = View.inflate(context, R.layout.attendee_announcements_item, null);
        }

        Notification notification = notifications.get(position);

        TextView notificationTitle = view.findViewById(R.id.notification_title_text);
        TextView notificationContent = view.findViewById(R.id.notification_content_text);

        notificationTitle.setText(notification.getTitle());
        notificationContent.setText(notification.getContent());

        return view;
    }

}
