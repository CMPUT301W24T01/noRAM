/* RecyclerView adapter for the image carousel */
package com.example.noram.controller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noram.MainActivity;
import com.example.noram.R;
import com.example.noram.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * RecyclerView adapter for the ImageCarousel on the MainActivity
 * @maintainter Cole
 * @author Cole
 */
public class ImageCarouselAdapter extends RecyclerView.Adapter<ImageCarouselAdapter.ViewHolder> {
    // reference for this class: EveryDayProgrammer, https://medium.com/@everydayprogrammer/implementing-material-3-carousel-in-android-studio-245435a5cdc5
    // "Implementing Material 3 Carousel in Android Studio", accessed april 3 2024.
    private ArrayList<Event> events;
    private Context context;

    /**
     * ViewHolder for the RecyclerView adapter
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView eventNameView;
        private TextView dateView;

        /**
         * Create the viewholder
         * @param itemView view to create the holder for
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.carousel_item_image);
            eventNameView = itemView.findViewById(R.id.carousel_item_name);
            dateView = itemView.findViewById(R.id.carousel_date_text);
        }

        /**
         * Get the imageview for the view holder
         * @return image view
         */
        public ImageView getImageView() {
            return imageView;
        }

        /**
         * Get the event name text for the view holder
         * @return textview for event name
         */
        public TextView getEventNameView() {
            return eventNameView;
        }

        /**
         * Get the date name textview for the view holder
         * @return textview for the date
         */
        public TextView getDateView() {
            return dateView;
        }
    }

    /**
     * Create a new ImageCarouselAdapter
     * @param context context to create the adapter in
     * @param events list of events to display
     */
    public ImageCarouselAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
    }

    /**
     * Creates the ViewHolder for the recycler view item
     * @param viewGroup viewgroup to inflate from
     * @param i item index to create
     * @return ViewHolder for index i.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.carousel_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * Setup the UI when the viewHolder is bound
     * @param viewHolder viewholder item that has been bound
     * @param i event item index
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        // download + set photo
        String photoPath = "event_banners/"+events.get(i).getId()+"-upload";

        // download the photo to display
        Activity ac = (Activity) context;
        MainActivity.db.downloadPhoto(photoPath, t -> ac.runOnUiThread(() -> viewHolder.getImageView().setImageBitmap(t)));

        // set on click to go to event page
        viewHolder.getImageView().setOnClickListener(v -> {
            EventManager.displayAttendeeEvent(context, events.get(i));
        });

        // set name text
        viewHolder.getEventNameView().setText(events.get(i).getName());

        // setup date
        LocalDateTime startTime = events.get(i).getStartTime();
        LocalDateTime endTime = events.get(i).getEndTime();
        if (startTime.toLocalDate().equals(endTime.toLocalDate())) {
            viewHolder.getDateView().setText(String.format("%s \n%s to %s",
                    startTime.format(DateTimeFormatter.ofPattern("MMM dd")),
                    startTime.format(DateTimeFormatter.ofPattern("h:mma")),
                    endTime.format(DateTimeFormatter.ofPattern("h:mma"))
            ));
        } else {
            // not the same date: need to include both dates
            viewHolder.getDateView().setText(String.format("%s at %s to \n%s at %s",
                    startTime.format(DateTimeFormatter.ofPattern("MMM dd")),
                    startTime.format(DateTimeFormatter.ofPattern("h:mm a")),
                    endTime.format(DateTimeFormatter.ofPattern("MMM dd")),
                    endTime.format(DateTimeFormatter.ofPattern("h:mm a"))
            ));
        }

    }

    /**
     * Get the item count of the data
     * @return size of events list
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

}
