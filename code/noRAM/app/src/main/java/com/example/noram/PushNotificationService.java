/*
This file is used to create the PushNotificationService class. This class is used to send notifications to attendees of an event.
Outstanding Issues:
- Currently only sends notifications in app to the main activity
 */

package com.example.noram;

import android.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;

import com.example.noram.model.Event;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
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
 * @Maintainer Christiaan
 * @Author Christiaan
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
        MainActivity.mn.runOnUiThread(() -> {
            // Display the notification with an alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.mn);
            builder.setTitle(remoteMessage.getNotification().getTitle());
            builder.setMessage(remoteMessage.getNotification().getBody());
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

    }

    /**
     * A method to send a notification to the attendees of an event
     * @param title the title of the notification
     * @param data the data of the notification
     * @param event the event to send the notification to
     */
    public void sendNotification(String title, String data, Event event) {

        // Get the list of attendees

        Set<String> attendeeList = (Set<String>) event.getCheckedInAttendees(); // cast to Set<String> so no duplicate attendees

        if (attendeeList == null) {
            throw new IllegalArgumentException("Attendee list is null");
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

                            try {
                                Response response = client.newCall(request).execute();
                                Log.d("Response", response.toString());
                                response.close();
                            } catch (IOException e) {
                                Log.d("Error", e.toString());
                            }
                        }
                    });
                }
            });
        }
    }
}

