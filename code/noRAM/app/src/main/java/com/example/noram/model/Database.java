package com.example.noram.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.example.noram.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * A class to represent the database
 */
public class Database {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference attendeeRef = db.collection("Attendees");
    private final CollectionReference organizerRef = db.collection("Organizers");
    private final CollectionReference adminRef = db.collection("Admins");
    private final CollectionReference photoRef = db.collection("Photos");
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
     * A method to add an attendee
     * @param identifier the identifier of the attendee
     * @return true if the attendee is added, false otherwise
     */
    public boolean addAttendee(String identifier) {
        try {
            attendeeRef.add(new Attendee(identifier)); // TODO: add the attendee properly
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * A method to add an organizer
     * @param identifier the identifier of the organizer
     * @return true if the organizer is added, false otherwise
     */
    public boolean addOrganizer(String identifier) {
        try {
            organizerRef.add(new Organizer(identifier)); // TODO: add the organizer properly
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * A method to add an admin
     * @param identifier the identifier of the admin
     * @return true if the admin is added, false otherwise
     */
    public boolean addAdmin(String identifier) {
        try {
            adminRef.add(new Admin(identifier)); // TODO: add the admin properly
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * A method to add a photo
     * @param identifier // TODO: PLACE HOLDER FOR PHOTO's
     * @return true if the photo is added, false otherwise
     */
    public boolean addPhoto(int identifier) {
        try {
            photoRef.add(new Photo()); // TODO: add the photo properly

            return true;
        } catch (Exception e) {
            return false;
        }
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
        profileRef.getStream().addOnSuccessListener(new OnSuccessListener<StreamDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(StreamDownloadTask.TaskSnapshot taskSnapshot) {
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
            }
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

