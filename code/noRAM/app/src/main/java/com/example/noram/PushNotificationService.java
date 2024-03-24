/*


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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PushNotificationService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        MainActivity.attendee.setFCMToken(token);
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

        // Display the notification with an alert dialog
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(remoteMessage.getNotification().getTitle());
//        builder.setMessage(remoteMessage.getNotification().getBody());
//        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
//        builder.show();
    }

    /**
     * A method to send a notification to the attendees of an event
     * @param title the title of the notification
     * @param data the data of the notification
     * @param event the event to send the notification to
     */
    public void sendNotification(String title, String data, Event event) {
        List<String> attendeeList = event.getCheckedInAttendees();
        assert attendeeList != null;
        for (String attendeeID : attendeeList) {

            Log.d("AttendeeID", attendeeID);

            MainActivity.db.getAttendeeRef().document(attendeeID).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    MainActivity.db.getKeyRef().document("FCMKEY").get().addOnCompleteListener(task2 -> {

                        if (task2.isSuccessful()) {

                            String token = "key=" + task2.getResult().get("FCMKEY");

                            OkHttpClient client = new OkHttpClient();

                            MediaType mediaType = MediaType.parse("application/json");

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

