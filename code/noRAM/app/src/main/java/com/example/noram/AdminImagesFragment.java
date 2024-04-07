/*
This file is used to create the Admin's Images section fragment, where all the app's images are
listed
Outstanding Issues:
- Images listed are not updated in real-time, meaning that the activity needs to be refreshed in
order to see recent changes in the database's storage.
 */

package com.example.noram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.noram.controller.PhotoArrayAdapter;
import com.example.noram.model.AdminPhoto;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Fragment of the Admin page, displaying all the images of the app's database
 * @maintainer Gabriel
 * @author Gabriel
 */
public class AdminImagesFragment extends Fragment {

    /**
     * Default constructor
     * @return a new instance of this fragment
     */
    public static AdminImagesFragment newInstance() {
        return new AdminImagesFragment();
    }

    public GridView imagesGrid; // images being displayed
    ArrayList<AdminPhoto> imagesDataList; // data containing images
    PhotoArrayAdapter imagesAdapter; // connect images being displayed with images' data

    /**
     * Removes a photo from the gridView and from the database
     * @param photoPos Index in imagesDataList of the photo being deleted
     */
    public void deletePhoto(int photoPos){
        // remove photo from database
        AdminPhoto photo = imagesDataList.get(photoPos);
        photo.deletePhoto();

        // remove from local data
        imagesDataList.remove(photoPos);
        imagesAdapter.notifyDataSetChanged();
    }

    /**
     * Adds an item (image) from firebase's storage to the page's gridview
     * @param item Storage's item to be added
     * @param isProfile Indicates if the photo is an attendee's profile photo
     */
    public void addPhotoToGrid(StorageReference item, boolean isProfile){
        // Create new photo and update with item's values
        AdminPhoto photo = new AdminPhoto();
        photo.setPhotoPath(item.getPath());
        photo.setPhotoProfile(isProfile);
        photo.setBitmapFromDB(getContext());
        // store it
        imagesDataList.add(photo);
    }

    /**
     * Inflates the layout for this fragment
     * @param inflater the layout inflater
     * @param container the view group
     * @param savedInstanceState the saved instance state
     * @return the inflated layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_images, container, false);

        // get basic elements
        imagesGrid = rootView.findViewById(R.id.imagesGrid);
        imagesDataList = new ArrayList<AdminPhoto>();
        imagesAdapter = new PhotoArrayAdapter(this.getContext(), imagesDataList);
        imagesGrid.setAdapter(imagesAdapter);

        // get all images from firebase's storage and store it(NOT updated in real-time!)
        MainActivity.db.getStorage().getReference("profile_photos/").listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {

                    @Override
                    public void onSuccess(ListResult listResult) {
                        for(StorageReference item : listResult.getItems()){
                            // add photo if it's not a setting photo
                            String path = item.getPath().toLowerCase();
                            if(!path.contains("cupcake") && !path.contains("-default")){
                                addPhotoToGrid(item, true);
                            }
                        }
                    }
                });
        MainActivity.db.getStorage().getReference("event_banners/").listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {

                    @Override
                    public void onSuccess(ListResult listResult) {
                        for(StorageReference item : listResult.getItems()){
                            addPhotoToGrid(item, false);
                        }
                    }
                });

        // update gridview after adding everything
        imagesAdapter.notifyDataSetChanged();


        // connection each list element to show the 'delete' popup
        imagesGrid.setOnItemClickListener((parent, view, position, id) -> {
            // initialize popup
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Confirm delete?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        deletePhoto(position);
                    })
                    .create().show();
        });


        return rootView;
    }
}
