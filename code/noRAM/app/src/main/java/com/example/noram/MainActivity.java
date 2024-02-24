package com.example.noram;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;
import android.widget.Button;

import com.example.noram.model.Database;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private final Database db = new Database();

    private Button adminButton;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        // NOTE: temporary buttons to move to each activity
        // In the future, we should evaluate whether there is a better method of navigation;
        // for now, this will give us a base to start work without clashing against each other.
        adminButton = findViewById(R.id.adminButton);
        adminButton.setVisibility(View.INVISIBLE);
        Button organizerButton = findViewById(R.id.organizerButton);
        Button attendeeButton = findViewById(R.id.attendeeButton);

        // Start each activity via an intent.
        adminButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AdminActivity.class))
        );

        organizerButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, OrganizerActivity.class))
        );

        attendeeButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AttendeeActivity.class))
        );

        // ask for camera permission
        // Reference: https://stackoverflow.com/a/66751594, Kfir Ram, "How to get camera permission on android", accessed feb 19 2024
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = db.getmAuth().getCurrentUser();

        // https://firebase.google.com/docs/auth/android/anonymous-auth?authuser=1#java
        db.getmAuth().signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = db.getmAuth().getCurrentUser();
                            updateAdminAccess(user.getUid());
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }
                    }
                });
    }

    /**
     * Updates the admin access for the application given a user ID
     * @param uid user ID to check for admin privileges.
     */
    private void updateAdminAccess(String uid) {
        DocumentReference adminRef = db.getAdminRef().document(uid);
        adminRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("AdminAccess", "User granted admin privileges");
                        adminButton.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("AdminAccess", "User does not have admin privileges");
                    }
                }
            }
        });
    }
}