package com.example.noram.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.noram.MainActivity;
import com.example.noram.R;
import com.example.noram.model.Photo;

import java.util.ArrayList;

/**
 * An adapter that connects a GridView of Photos with an ArrayList of Photos.
 * A {@link PhotoArrayAdapter} object is used to display a list of Photos  in a Gridview.
 * @author Gabriel
 */
public class PhotoArrayAdapter extends ArrayAdapter<Photo>{
    private ArrayList<Photo> photos;
    private Context context;

    /**
     * A constructor to create a PhotoArrayAdapter
     *
     * @param context the context of the adapter
     * @param photos  the photos to be displayed
     */
    public PhotoArrayAdapter(Context context, ArrayList<Photo> photos) {
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
        Photo photo = photos.get(position);

        // get view
        ImageView photoView = view.findViewById(R.id.photoView);

        // set default bitmap then update with photo's real bitmap (asynchronous)
        photoView.setImageResource(R.drawable.add_photo_24px);
        photo.updateWithBitmap(photoView);

        return view;
    }
}