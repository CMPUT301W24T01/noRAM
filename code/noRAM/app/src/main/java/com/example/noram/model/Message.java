/*


 */

package com.example.noram.model;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import static android.content.ContentValues.TAG;

import com.example.noram.MainActivity;
import com.example.noram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class Message extends FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();

    public void subscribeToTopic(String topic) {
        firebaseMessaging.subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    String msg = "Subscribed";
                    if (!task.isSuccessful()) {
                        msg = "Subscribe failed";
                    }
                    Log.d(TAG, msg);
                });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (!remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

//    public void sendNotification(String topic) {
//
//        // See documentation on defining a message payload.
//        Message message = Message.builder()
//                .putData("score", "850")
//                .putData("time", "2:45")
//                .setTopic(topic)
//                .build();
//
//        // Send a message to the devices subscribed to the provided topic.
//        String response = firebaseMessaging.send(message);
//        // Response is a message ID string.
//        System.out.println("Successfully sent message: " + response);
//    }


//    public void conditionalSendMessage(String topic, String message) {
//        // [START send_to_condition]
//        // Create a condition that will send to devices with Google Play services installed
//        Condition condition = Condition.builder()
//                .addCondition(ConditionKey.TOPIC, topic)
//                .build();
//
//        // See documentation on defining a message payload.
//        Message message = Message.builder()
//                .setCondition(condition)
//                .putData("score", "850")
//                .putData("time", "2:45")
//                .build();
//
//        // Send a message to devices subscribed to the combination of topics
//        // specified by the provided condition.
//        String response = firebaseMessaging.send(message);
//        // Response is a message ID string.
//        System.out.println("Successfully sent message: " + response);
//        // [END send_to_condition]
//    }


//    public void getToken() {
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()) {
//                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                        return;
//                    }
//
//                    // Get new FCM registration token
//                    String token = task.getResult();
//
//                    // Log and toast
//                    Log.d(TAG, msg);
//                });
//    }

}








//    Declare the launcher at the top of your Activity/Fragment:
//    private final ActivityResultLauncher<String> requestPermissionLauncher =
//            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//                if (isGranted) {
//                    // FCM SDK (and your app) can post notifications.
//                } else {
//                    // TODO: Inform user that that your app will not show notifications.
//                }
//            });

//    private void askNotificationPermission() {
//        // This is only necessary for API level >= 33 (TIRAMISU)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
//                    PackageManager.PERMISSION_GRANTED) {
//                // FCM SDK (and your app) can post notifications.
//            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
//                // TODO: display an educational UI explaining to the user the features that will be enabled
//                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
//                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
//                //       If the user selects "No thanks," allow the user to continue without notifications.
//            } else {
//                // Directly ask for the permission
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
//            }
//        }
//    }