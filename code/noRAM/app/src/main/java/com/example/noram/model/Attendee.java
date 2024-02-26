package com.example.noram.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.example.noram.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
    private String profilePicture = "";

    private Boolean allowLocation = false;

    private Boolean defaultProfilePicture;

    /**
     * A constructor to create an attendee with just an identifier
     * @param identifier the identifier of the attendee
     */
    public Attendee(String identifier) {
        this.identifier = identifier;
        defaultProfilePicture = true;
    }

    /**
     * A constructor to create an attendee with all field filled
     * @param identifier the identifier of the attendee
     * @param firstName the first name of the attendee
     * @param lastName the last name of the attendee
     * @param homePage the home page of the attendee
     * @param phoneNumber the phone number of the attendee
     * @param profilePicture the profile picture of the attendee
     * @param allowLocation the location allowance of the attendee
     */
    public Attendee(String identifier, String firstName, String lastName, String homePage, String phoneNumber, String profilePicture, Boolean allowLocation) {
        this.identifier = identifier;
        this.firstName = firstName;
        this.lastName = lastName;
        this.homePage = homePage;
        this.phoneNumber = phoneNumber;
        this.profilePicture = profilePicture;
        this.allowLocation = allowLocation;
        defaultProfilePicture = false;
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
     * A method to get the profile picture of the attendee
     * @return the profile picture of the attendee
     */
    public String getProfilePicture() {
        return profilePicture;
    }

    /**
     * A method to set the profile picture of the attendee
     * @param profilePicture the profile picture of the attendee
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
        defaultProfilePicture = false;
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
     * A method to generate a default profile picture for the attendee
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void profilePhotoGenerator() {
        // Temporary Way to generate a default profile picture
//        if (defaultProfilePicture) {
//            profilePicture = "https://www.gravatar.com/avatar/" + Integer.valueOf(firstName) + "?d=identicon";
//            updateDBAttendee();
//        }

        // Cupcake way
        if (defaultProfilePicture) {
            StorageReference storageReferenceIcing = MainActivity.db.getStorage().getReference().child("profile_pictures/cupcakeIcing.png");
            StorageReference storageReferenceCherry = MainActivity.db.getStorage().getReference().child("profile_pictures/cupcakeCherry.png");
            StorageReference storageReferenceCupcake = MainActivity.db.getStorage().getReference().child("profile_pictures/cupcakeCake.png");

            Bitmap icingBitmap = null;
            Bitmap cherryBitmap = null;
            Bitmap cakeBitmap = null;

            // Need to get the bitmaps from the storage

            int numIdentifier = Integer.parseInt(firstName.toString());
            int R = (numIdentifier) % 256;
            int G = (numIdentifier * 10) % 256;
            int B = (numIdentifier * 100) % 256;

            Color icingColor = Color.valueOf(R,G,B);
            Random random = new Random();
            random.setSeed(firstName.hashCode());
            int randomNum = random.nextInt(100);
            Color cherryColor = Color.valueOf(randomNum, randomNum, randomNum);

            // Create a new bitmap for the final composition
            Bitmap finalBitmap = Bitmap.createBitmap(cakeBitmap.getWidth(), cakeBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(finalBitmap);

            // Draw the cake onto the canvas
            canvas.drawBitmap(cakeBitmap, 0, 0, null);

            int icingX = 0; // Example icing X coordinate
            int icingY = 0; // Example icing Y coordinate
            int cherryX = 0; // Example cherry X coordinate
            int cherryY = 0; // Example cherry Y coordinate

            // Draw the icing on top of the cake
            canvas.drawBitmap(icingBitmap, icingX, icingY, null); // Adjust X and Y coordinates as needed

            // Draw the cherry on top of the icing
            canvas.drawBitmap(cherryBitmap, cherryX, cherryY, null); // Adjust X and Y coordinates as needed

            Paint icingPaint = new Paint();
            icingPaint.setColorFilter(new PorterDuffColorFilter(icingColor.toArgb(), PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(icingBitmap, icingX, icingY, icingPaint);

            Paint cherryPaint = new Paint();
            cherryPaint.setColorFilter(new PorterDuffColorFilter(cherryColor.toArgb(), PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(cherryBitmap, cherryX, cherryY, cherryPaint);

            String photoPath = String.valueOf("profile_photos/"+identifier+"_default.png");
            StorageReference storageReference = MainActivity.db.getStorage().getReference().child(photoPath);
            storageReference.putBytes(finalBitmap.toString().getBytes());
        }
    }
}
