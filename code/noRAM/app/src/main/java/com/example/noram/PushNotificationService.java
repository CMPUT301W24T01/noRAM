/*


 */

package com.example.noram;

import android.util.Log;

import androidx.annotation.NonNull;

import static android.content.ContentValues.TAG;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.noram.model.Event;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PushNotificationService extends FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();

    public void subscribeToTopic(String topic) {
        firebaseMessaging.subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    String msg = "Subscribed";
                    if (!task.isSuccessful()) {
                        msg = "Subscribe failed";
                    }
                    Log.d("Subscribe", msg);
                });
    }

    public void unsubscribeFromTopic(String topic) {
        firebaseMessaging.unsubscribeFromTopic(topic)
                .addOnCompleteListener(task -> {
                    String msg = "Unsubscribed";
                    if (!task.isSuccessful()) {
                        msg = "Unsubscribe failed";
                    }
                    Log.d("Unsubscribe", msg);
                });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // super.onMessageReceived(remoteMessage);
        // Toast.makeText(getApplicationContext(), remoteMessage.getNotification().getBody(), Toast.LENGTH_LONG).show();
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        // Toast.makeText(getApplicationContext(), remoteMessage.getNotification().getBody(), Toast.LENGTH_LONG).show()
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        // sendRegistrationToServer(token);
    }

    /**
     * A method to send a notification to the attendees of an event
     * @param title the title of the notification
     * @param data the data of the notification
     * @param event the event to send the notification to
     */
    public void sendNotification(String title, String data, Event event) { // String EventName

        List<String> attendeeList = event.getCheckedInAttendees();
        assert attendeeList != null;
        for (String attendeeID : attendeeList) {
            MainActivity.db.getAttendeeRef().document(attendeeID).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    MainActivity.db.getKeyRef().document("FCMKEY").get().addOnCompleteListener(task2 -> {

                        if (task2.isSuccessful()) {

                            String FCMToken = task.getResult().getString("fcmtoken");
                            Log.d("FCMToken", FCMToken);

                            // Set the URL and token
                            String url = "https://fcm.googleapis.com/fcm/send";

                            String token = "key=" + task2.getResult().get("FCMKEY");
                            Log.d("Token", token);

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                                    response -> Log.d("Response", response.toString()),
                                    error -> Log.d("Error", error.toString())) {
                                @Override
                                public Map<String, String> getHeaders() {
                                    Map<String, String> headers = new HashMap<>();
                                    headers.put("Authorization", token);
                                    headers.put("Content-Type", "application/json");
                                    return headers;
                                }

                                @Override
                                public byte[] getBody() {
                                    JSONObject message = new JSONObject();
                                    JSONObject notification = new JSONObject();
                                    MainActivity.db.getAttendeeRef().document("").get().addOnSuccessListener(documentSnapshot -> {
                                        try {
                                            notification.put("title", title);
                                            notification.put("body", data);
                                            message.put("to", documentSnapshot.getString("FCMToken"));
                                            message.put("notification", notification);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    return message.toString().getBytes();
                                }
                            };

                            Log.d("Request", Arrays.toString(jsonObjectRequest.getBody()));

                            // Add the request to the RequestQueue
                            // Volley.newRequestQueue(this).add(jsonObjectRequest);
                            // Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);

                        }
                    });
                }
            });
        }
    }
}