/*
This file is used to create ArrayList Adapters that adapt array list of Photos. It is mainly used in
AdminImagesFragment.
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.noram.R;
import com.example.noram.model.AdminPhoto;

import java.util.ArrayList;

/**
 * An adapter that connects a GridView of Photos with an ArrayList of Photos.
 * A {@link PhotoArrayAdapter} object is used to display a list of Photos  in a Gridview.
 * @author Gabriel
 */
public class PhotoArrayAdapter extends ArrayAdapter<AdminPhoto>{
    private ArrayList<AdminPhoto> photos;
    private Context context;

    /**
     * A constructor to create a PhotoArrayAdapter
     *
     * @param context the context of the adapter
     * @param photos  the photos to be displayed
     */
    public PhotoArrayAdapter(Context context, ArrayList<AdminPhoto> photos) {
        super(context, 0, photos);
        this.photos = photos;
        this.context = context;
    }

    /**
     * A method to get the view of the adapter
     *
     * @param position    the position of the view
     * @param convertView the view to be converted
     * @param parent      the parent of the view
     * @return the view of the adapter
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.photo_list_item, parent, false);
        }

        // get photo data
        AdminPhoto photo = photos.get(position);

        // get view
        ImageView photoView = view.findViewById(R.id.photoView);

        // set default bitmap then update with photo's real bitmap (asynchronous)
        photo.updateWithBitmap(photoView);

        return view;
    }
}