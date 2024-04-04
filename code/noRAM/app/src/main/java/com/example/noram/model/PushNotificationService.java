/*
This file is used to create the PushNotificationService class. This class is used to send notifications to attendees of an event.
Outstanding Issues:
- Currently only sends notifications in app to the main activity
 */

package com.example.noram.model;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.noram.AttendeeActivity;
import com.example.noram.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * The PushNotificationService class is used to send notifications to attendees of an event.
 * A {@link FirebaseMessagingService} subclass.
 * @maintainer Christiaan
 * @author Christiaan
 * @author Ethan
 */
public class PushNotificationService extends FirebaseMessagingService {

    /**
     * Called when a new token is generated. Will update the FCM token of the attendee.
     * @param token the new token
     */
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        if (MainActivity.attendee != null) { MainActivity.attendee.setFCMToken(token); }
    }

    /**
     * Called when a message is received. Will display a notification with the message received.
     * @param remoteMessage the message received
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Message Notification Title: " + Objects.requireNonNull(remoteMessage.getNotification()).getTitle());
        Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

        // Change to the UI Thread
//        MainActivity.mn.runOnUiThread(() -> {
//            // Display the notification with an alert dialog
//            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.mn);
//            builder.setTitle(remoteMessage.getNotification().getTitle());
//            builder.setMessage(remoteMessage.getNotification().getBody());
//            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
//            builder.show();
//        });

        AttendeeActivity.showNotification(remoteMessage);
    }

    /**
     * A method to send a notification to the attendees of an event
     * @param title the title of the notification
     * @param data the data of the notification
     * @param event the event to send the notification to
     * @param sendToOrganizer a boolean to check if we wish to only send the notification to the organizer
     */
    public void sendNotification(String title, String data, Event event, Boolean sendToOrganizer) {

        // Send the notification to the database
        if (!sendToOrganizer) {
            sendNotificationToDB(title, data, event);
        }

        Set<String> attendeeList;

        if (sendToOrganizer) {
            attendeeList = Collections.singleton((event.getOrganizerId()));
        } else {
            attendeeList = new HashSet<>(event.getCheckedInAttendees()); // cast to Set<String> so no duplicate attendees
        }

        // Send a notification to each attendee

        for (String attendeeID : attendeeList) {

            Log.d("AttendeeID", attendeeID);

            MainActivity.db.getAttendeeRef().document(attendeeID).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    // Get the server key

                    MainActivity.db.getKeyRef().document("FCMKEY").get().addOnCompleteListener(task2 -> {

                        if (task2.isSuccessful()) {

                            // Get the attendee's FCM token

                            String token = "key=" + task2.getResult().get("FCMKEY");

                            OkHttpClient client = new OkHttpClient();

                            MediaType mediaType = MediaType.parse("application/json");

                            // Create the message to send

                            JSONObject message = new JSONObject();
                            JSONObject notification = new JSONObject();

                            try {
                                notification.put("title", title);
                                notification.put("body", data);
                                message.put("to", task.getResult().get("fcmtoken"));
                                message.put("notification", notification);
                            } catch (JSONException e) {
                                Log.d("Error", e.toString());
                            }

                            // Add to the message

                            RequestBody body = RequestBody.create(mediaType, message.toString());
                            Request request = new Request.Builder()
                                    .url("https://fcm.googleapis.com/fcm/send")
                                    .post(body)
                                    .addHeader("Authorization", token)
                                    .addHeader("Content-Type", "application/json")
                                    .build();

                            Log.d("Request", request.toString());
                            Log.d("Message", message.toString());
                            Log.d("Token", token);
                            Log.d("Body", body.toString());

                            // Send the message
                            // https://stackoverflow.com/a/14443056, Dr.Luiji, "How can I fix 'android.os.NetworkOnMainThreadException'?", accessed April 1 2024
                            Thread thread = new Thread(() -> {
                                try {
                                    Response response = client.newCall(request).execute();
                                    Log.d("Response", response.toString());
                                    response.close();
                                } catch (IOException e) {
                                    Log.d("Error", e.toString());
                                }
                            });

                            thread.start();
                        }
                    });
                }
            });
        }
    }

    /**
     * A method to send a notification to the attendees of an event
     * @param title the title of the notification
     * @param data the data of the notification
     * @param event the event to send the notification to
     */
    private void sendNotificationToDB(String title, String data, Event event) {
        event.addNotification(new Notification(title, data));
        event.updateDBEvent(); // update the event in the database
    }

}

