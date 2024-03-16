/*
This file is used to create a photo object. It contains the photo name and the photo path.
Outstanding Issues:
- None
 */

package com.example.noram.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.example.noram.MainActivity;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A class to represent a photo
 * @maintainer Carlin
 * @author Christiaan
 */
public class Photo {
    private String photoName;
    private String photoPath;
    private Bitmap photoBitmap;
    // views that need to be updated with bitmap later
    private final ArrayList<ImageView> viewsToUpdate = new ArrayList<ImageView>();

    /**
     * A constructor to create a photo
     */
    public Photo() {
    }

    /**
     * A constructor to create a photo
     * @param photoName the name of the photo
     * @param photoPath the path of the photo
     */
    public Photo(String photoName, String photoPath) {
        this.photoName = photoName;
        this.photoPath = photoPath;
    }

    /**
     * A method to get the name of the photo
     * @return the name of the photo
     */
    public String getPhotoName() {
        return photoName;
    }

    /**
     * A method to set the name of the photo
     * @param photoName the name of the photo
     */
    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    /**
     * A method to get the path of the photo
     * @return the path of the photo
     */
    public String getPhotoPath() {
        return photoPath;
    }

    /**
     * A method to set the path of the photo
     * @param photoPath the path of the photo
     */
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    /**
     * Removes the photo from the cloud storage. If it was an attendee photo, also reset the default
     * parameter of the attendee
     * @param userPhoto Indicates if the photo is a attendee's profile and we need to reset the
     *                  attendee's default photo.
     */
    public void deletePhoto(boolean userPhoto) {
        // remove from database
        StorageReference storageReference = MainActivity.db.getStorage().getReference().child(photoPath);
        storageReference.delete().addOnSuccessListener(
                unused -> Log.d("Firebase", "Photo successfully deleted!")
        ).addOnFailureListener(
                e -> Log.d("Firebase", "Photo unsuccessfully deleted!")
        );

        // if attendee's profile photo, reset default
        if(userPhoto){
            // get ID by finding first "-" and taking what's before
            int IDIndex = photoPath.indexOf("-");
            if (IDIndex != -1) {
                String id = photoPath.substring(0, IDIndex);
                // TODO: update database with new value
            } else {
                throw new RuntimeException("Photo's path does not contain '-': cannot find ID.");
            }
        }

    }

    /**
     * Getter for the bitmap attribute of Photo
     * @return The Photo's bitmap
     */
    public Bitmap getBitmap(){
        return photoBitmap;
    }

    /**
     * Sets the photo's bitmap by getting it from the database
     * @param context The context in which the UIThread will run to get the bitmap
     */
    public void setBitmapFromDB(Context context){
        // verify we have the path
        if(photoPath == null){
            throw new RuntimeException("Photo's path should be initialized.");
        }
        MainActivity.db.downloadPhoto(photoPath,
                t -> ((Activity) context).runOnUiThread(() -> bitMapUpdated(t)));
    }

    /**
     * When the bitmap is updated asynchronously, this function gets called so that all instances
     * using this bitmap value gets updated
     * @param newBitmap The new bitmap value
     */
    public void bitMapUpdated(Bitmap newBitmap){
        photoBitmap = newBitmap;
        // all image views that wanted to use the bitmap gets their image updated
        for(ImageView v: viewsToUpdate){
            v.setImageBitmap(newBitmap);
        }
    }

    /**
     * Updates an imageView with the bitmap value of the Photo. If the bitmap has not been set yet,
     * the view is added to a list who will get updated when the bitmap values is set.
     * @param view The ImageView that will have its bitmap updated with the Photo's bitmap
     */
    public void updateWithBitmap(ImageView view){
        if(photoBitmap != null){
            view.setImageBitmap(photoBitmap);
        }
        else{
            viewsToUpdate.add(view);
        }
    }
}
