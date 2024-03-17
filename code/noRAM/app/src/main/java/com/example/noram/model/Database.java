/*
This file is used to represent the database and provide methods to interact with it.
Outstanding Issues:
- None
 */

package com.example.noram.model;

import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * A class to represent the database
 * @maintainer Christiaan
 * @author Christiaan
 * @author Ethan
 * @author Cole
 */
public class Database {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference attendeeRef = db.collection("Attendees");
    private final CollectionReference organizerRef = db.collection("Organizers");
    private final CollectionReference adminRef = db.collection("Admins");
    private final CollectionReference photoRef = db.collection("Photos");
    private final CollectionReference qrRef = db.collection("QRCodes");
    private final CollectionReference eventsRef = db.collection("Events");
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    /**
     * A method to get the authentication
     * @return the authentication
     */
    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    /**
     * A method to get the database
     * @return the database
     */
    public FirebaseFirestore getDb() {
        return db;
    }

    /**
     * A method to get the attendee reference
     * @return the attendee reference
     */
    public CollectionReference getAttendeeRef() {
        return attendeeRef;
    }

    /**
     * A method to get the organizer reference
     * @return the organizer reference
     */
    public CollectionReference getOrganizerRef() {
        return organizerRef;
    }

    /**
     * A method to get the admin reference
     * @return the admin reference
     */
    public CollectionReference getAdminRef() {
        return adminRef;
    }

    /**
     * Getter method for QR code reference
     * @return the qr code reference
     */
    public CollectionReference getQrRef() {
        return qrRef;
    }

    /**
     * Getter method for the Events reference
     * @return the events reference
     */
    public CollectionReference getEventsRef() {
        return eventsRef;
    }

    /**
     *
     * Downloads the photo at the given path from Cloud Storage.
     * This is intended as a utility so that photo download is consistent through the app.
     * @param photoRef path to the photo in cloud storage
     * @param completeFunction function to be called once the download is complete. Should be passed using
     *                         a lambda, e.g. t -> myFunction(t). The function passed should take in
     *                         only one argument, a Bitmap, which will be the downloaded image.
     *                         Note that if the function you call is going to update the UI, you will
     *                         most likely need to wrap it in runOnUiThread(() -> myFunction(t)). This
     *                         prevents an error from modifying the UI outside of the UI thread.
     */
    public void downloadPhoto(String photoRef, Consumer<Bitmap> completeFunction) {
        StorageReference profileRef = storage.getReference().child(photoRef);
        profileRef.getStream().addOnSuccessListener(taskSnapshot -> {
            InputStream photoStream = taskSnapshot.getStream();

            // we can't run decodeStream() in the main thread, so we create an executor to
            // run it instead.
            Executor executor = Executors.newSingleThreadExecutor();

            Runnable decodeRunnable = () -> {
                Bitmap image = BitmapFactory.decodeStream(photoStream);

                // call the passed function
                completeFunction.accept(image);
            };
            executor.execute(decodeRunnable);
        });
    }

    /**
     * Uploads a photo to the database
     * @param photo URI of the photo to upload
     * @param storagePath path to store the photo in cloud storage
     */
    public void uploadPhoto(Uri photo, String storagePath) {
        StorageReference uploadRef = storage.getReference().child(storagePath);
        uploadRef.putFile(photo);
    }

    /**
     * A method to get the cloud storage
     * @return database
     */
    public FirebaseStorage getStorage() {
        return storage;
    }

}

