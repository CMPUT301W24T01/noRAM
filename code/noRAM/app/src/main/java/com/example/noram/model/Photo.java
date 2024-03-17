/*
This file is used to create a photo object. It contains the photo name and the photo path.
Outstanding Issues:
- None
 */

package com.example.noram.model;

/**
 * A class to represent a photo
 * @maintainer Carlin
 * @author Christiaan
 */
public class Photo {
    private String photoName;
    private String photoPath;

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
}
