package com.example.noram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.noram.model.Database;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Database db = new Database();
        
    }
}