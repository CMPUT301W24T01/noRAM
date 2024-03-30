/*
This file is used to create an Attendee object. This object is used to store information about an attendee.
Outstanding Issues:
- Generate proflie picture duplicated for some functionality, may want to decouple database from setters
 */

package com.example.noram.model;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.noram.MainActivity;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A class representing an attendee
 * @maintainer Christiaan
 * @author Christiaan
 * @author Ethan
 * @author Cole
 */
public class Attendee {
    private String identifier;
    private String firstName = "";
    private String lastName = "";
    private String homePage = "";
    private String email = "";
    private Boolean allowLocation = false;
    private List<String> eventsCheckedInto = new ArrayList<>();
    private Boolean usingDefaultProfilePhoto = true;
    private String FCMToken = "";

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
     * @param email the email of the attendee
     * @param allowLocation the location allowance of the attendee
     * @param defaultPhoto whether or not the attendee is using the default profile photo
     * @param eventsCheckedInto the events the attendee is checked into
     */
    public Attendee(String identifier, String firstName, String lastName, String homePage, String email, Boolean allowLocation, Boolean defaultPhoto, List<String> eventsCheckedInto) {
        this.identifier = identifier;
        this.firstName = firstName;
        this.lastName = lastName;
        this.homePage = homePage;
        this.email = email;
        this.allowLocation = allowLocation;
        this.eventsCheckedInto = eventsCheckedInto;
        this.usingDefaultProfilePhoto = defaultPhoto;
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
     * A method to get the email of the attendee
     * @return the email of the attendee
     */
    public String getEmail() {
        return email;
    }

    /**
     * A method to set the email of the attendee
     * @param email the email of the attendee
     */
    public void setEmail(String email) {
        this.email = email;
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
     * Get the list of events an attendee is checked into
     * @return List of event ids
     */
    public List<String> getEventsCheckedInto() {
        return eventsCheckedInto;
    }

    /**
     * Set the list of events an attendee is checked into
     * @param eventsCheckedInto new list
     */
    public void setEventsCheckedInto(List<String> eventsCheckedInto) {
        this.eventsCheckedInto = eventsCheckedInto;
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
        return usingDefaultProfilePhoto;
    }

    /**
     * Set whether we are currently using a default profile photo
     * @param defaultProfilePhoto new value.
     */
    public void setDefaultProfilePhoto(Boolean defaultProfilePhoto) {
        this.usingDefaultProfilePhoto = defaultProfilePhoto;
        updateDBAttendee();
    }

    /**
     * Gets the string for the attendee's profile photo in cloud storage
     * @return string filepath in cloud storage.
     */
    public String getProfilePhotoString() {
        if (usingDefaultProfilePhoto) {
            return "profile_photos/" + getIdentifier() + "-default";
        } else {
            return "profile_photos/" + getIdentifier() + "-upload";
        }
    }

    /**
     * Set the FCM token for the attendee
     * @param FCMToken new token
     */
    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
        updateDBAttendee();
    }

    /**
     * Get the FCM token for the attendee
     * @return the FCM token
     */
    public String getFCMToken() {
        return FCMToken;
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

        // create result bitmap output
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        // color information
        int pixel;

        // iteration through pixels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                int index = y * width + x;
                pixel = pixels[index];
                // set new pixel color to output bitmap if it is white
                if(pixel == -1) {
                    // change color
                    pixels[index] = color_1_replacement;
                }
            }
        }

        // set pixel array to result
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }

    /**
     * A method to generate a default profile picture for the attendee. Should be called
     * when the attendee is created so that a photo can immediately be displayed.
     */
    public void generateDefaultProfilePhoto() {

        // Ensure we are using the default photo
        if (usingDefaultProfilePhoto) {

            // Generate a color based on the attendee's name

            String colorIdentifier = identifier.toString() + firstName;
            StringBuilder builder = new StringBuilder();
            for (char c : colorIdentifier.toCharArray()) {
                builder.append((int)c);
            }
            int numIdentifier = new BigInteger(builder.toString()).intValue();
            int R = (numIdentifier) % 256;
            int G = (numIdentifier * 10) % 256;
            int B = (numIdentifier * 100) % 256;
            int icingColor = R << 16 | G << 8 | B;

            // Download the default photo and change its color
            Consumer<Bitmap> downloadConsumer = bitmap -> {
                Bitmap finalBitmap = changeColor(bitmap, icingColor);

                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteOutputStream);
                byte[] data = byteOutputStream.toByteArray();

                MainActivity.db.getStorage().getReference()
                        .child("profile_photos/" + getIdentifier() + "-default")
                        .putBytes(data);

            };

            // Download the default photo
            MainActivity.db.downloadPhoto("profile_photos/cupcakeCakeDefault.png", downloadConsumer);
        }
    }

    /**
     * A method to generate a default profile picture for the attendee. Should be called
     * when the attendee is created so that a photo can immediately be displayed.
     * In addition, this method will return the generated photo to the calling function.
     * @param callingFunction the function to return the photo to (e.g. a lambda that sets the photo in the UI)
     */
    public void generateAndReturnDefaultProfilePhoto(Consumer<Bitmap> callingFunction) {

        // Currently pretty TEMP function to get around the async nature of the download

        // Ensure we are using the default photo
        if (usingDefaultProfilePhoto) {

            String colorIdentifier = identifier.toString() + firstName;
            StringBuilder builder = new StringBuilder();
            for (char c : colorIdentifier.toCharArray()) {
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
                        callingFunction.accept(finalBitmap);
            };
            MainActivity.db.downloadPhoto("profile_photos/cupcakeCakeDefault.png", downloadConsumer);
        }
    }

    /**
     * A method to generate the FCM token for the attendee
     */
    public void generateAttendeeFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                return;
            }
            // Get new FCM registration token
            String token = task.getResult();
            setFCMToken(token);
            Log.d("FCM", token);
        });
    }

    /**
     * A method to update the attendee with a Map object
     * @param map the Map with information about the attendee attributes
     */
    public void updateWithMap(Map<String, Object> map) {
        this.firstName = (String) map.get("firstName");
        this.lastName = (String) map.get("lastName");
        this.homePage = (String) map.get("homePage");
        this.email = (String) map.get("email");
        this.allowLocation = (Boolean) map.get("allowLocation");
        this.usingDefaultProfilePhoto = (Boolean) map.get("defaultProfilePhoto");
        this.eventsCheckedInto = (List<String>) map.get("eventsCheckedInto");
        this.FCMToken = (String) map.get("FCMToken");
    }
}