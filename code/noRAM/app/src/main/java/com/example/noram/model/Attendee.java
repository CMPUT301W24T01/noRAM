package com.example.noram.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.params.BlackLevelPattern;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.noram.MainActivity;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.util.Random;
import java.util.function.Consumer;

/**
 * A class representing an attendee
 */
public class Attendee {
    private String identifier;
    private String firstName = "";
    private String lastName = "";
    private String homePage = "";

    // Phone number is stored as a string to avoid overflow and to deal with any character if necessary
    private String phoneNumber = "";

    private Boolean allowLocation = false;

    private Boolean usingDefaultProfilePicture = true;

    /**
     * A constructor to create an attendee with just an identifier
     * @param identifier the identifier of the attendee
     */
    public Attendee(String identifier) {
        this.identifier = identifier;
    }

    /**
     * A constructor to create an attendee with all field filled
     * @param identifier the identifier of the attendee
     * @param firstName the first name of the attendee
     * @param lastName the last name of the attendee
     * @param homePage the home page of the attendee
     * @param phoneNumber the phone number of the attendee
     * @param allowLocation the location allowance of the attendee
     */
    public Attendee(String identifier, String firstName, String lastName, String homePage, String phoneNumber, Boolean allowLocation, Boolean defaultPhoto) {
        this.identifier = identifier;
        this.firstName = firstName;
        this.lastName = lastName;
        this.homePage = homePage;
        this.phoneNumber = phoneNumber;
        this.allowLocation = allowLocation;
        this.usingDefaultProfilePicture = defaultPhoto;
    }

    /**
     * A method to get the identifier of the attendee
     * @return the identifier of the attendee
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * A method to set the identifier of the attendee
     * @param identifier the identifier of the attendee
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        updateDBAttendee();
    }

    /**
     * A method to get the first name of the attendee
     * @return the first name of the attendee
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * A method to set the first name of the attendee
     * @param firstName the first name of the attendee
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateDBAttendee();
    }

    /**
     * A method to get the last name of the attendee
     * @return the last name of the attendee
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * A method to set the last name of the attendee
     * @param lastName the last name of the attendee
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateDBAttendee();
    }

    /**
     * A method to get the home page of the attendee
     * @return the home page of the attendee
     */
    public String getHomePage() {
        return homePage;
    }

    /**
     * A method to set the home page of the attendee
     * @param homePage the home page of the attendee
     */
    public void setHomePage(String homePage) {
        this.homePage = homePage;
        updateDBAttendee();
    }

    /**
     * A method to get the phone number of the attendee
     * @return the phone number of the attendee
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * A method to set the phone number of the attendee
     * @param phoneNumber the phone number of the attendee
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        updateDBAttendee();
    }

    /**
     * A method to get the location allowance of the attendee
     * @return the location allowance of the attendee
     */
    public Boolean getAllowLocation() {
        return allowLocation;
    }

    /**
     * A method to set the location allowance of the attendee
     * @param allowLocation the location allowance of the attendee
     */
    public void setAllowLocation(Boolean allowLocation) {
        this.allowLocation = allowLocation;
        updateDBAttendee();
    }

    /**
     * A method to update the database with the new attendee information
     */
    public void updateDBAttendee() {
        MainActivity.db.getAttendeeRef().document(identifier).set(this);
    }

    /**
     * Returns whether or not we are currently using a default profile photo.
     * @return True if yes, false otherwise.
     */
    public Boolean getDefaultProfilePhoto() {
        return usingDefaultProfilePicture;
    }

    /**
     * Set whether we are currently using a default profile photo
     * @param defaultProfilePhoto new value.
     */
    public void setDefaultProfilePhoto(Boolean defaultProfilePhoto) {
        this.usingDefaultProfilePicture = defaultProfilePhoto;
    }

    /**
     * Gets the string for the attendee's profile photo in cloud storage
     * @return string filepath in cloud storage.
     */
    public String getProfilePhotoString() {
        if (usingDefaultProfilePicture) {
            return "profile_photos/" + getIdentifier() + "-default";
        } else {
            return "profile_photos/" + getIdentifier() + "-upload";
        }
    }

    /**
     * A method to change the color of the default profile picture
     * @param src the source bitmap
     * @param color_1_replacement the color to replace the default color with
     * @return the new bitmap with the color replaced
     */
    private Bitmap changeColor(Bitmap src, int color_1_replacement) {
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        int pixel;

        // iteration through pixels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                int index = y * width + x;
                pixel = pixels[index];
                if(pixel == -1) {
                    pixels[index] = color_1_replacement;
                }
            }
        }
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }

    /**
     * A method to generate a default profile picture for the attendee. Should be called
     * when the attendee is created so that a photo can immediately be displayed.
     */
    public void generateDefaultProfilePhoto() {
        // Cupcake way
        if (usingDefaultProfilePicture) {

            StringBuilder builder = new StringBuilder();
            for (char c : firstName.toCharArray()) {
                builder.append((int)c);
            }
            int numIdentifier = new BigInteger(builder.toString()).intValue();
            int R = (numIdentifier) % 256;
            int G = (numIdentifier * 10) % 256;
            int B = (numIdentifier * 100) % 256;
            int icingColor = R << 16 | G << 8 | B;
            
            Consumer<Bitmap> downloadConsumer = bitmap -> {
                Bitmap finalBitmap = changeColor(bitmap, icingColor);

                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteOutputStream);
                byte[] data = byteOutputStream.toByteArray();

                MainActivity.db.getStorage().getReference()
                        .child("profile_photos/" + getIdentifier() + "-default")
                        .putBytes(data);

            };
            MainActivity.db.downloadPhoto("profile_photos/cupcakeCakeDefault.png", downloadConsumer);
        }
    }
}