package com.example.noram.model;

import com.google.firebase.firestore.FirebaseFirestore;

public abstract class Database {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirebaseFirestore getDb() {
        return db;
    }

}
