package com.example.noram.model;

import android.app.Application;

/**
 * A class to share data between activities
 */
public class NoRAMApp extends Application {
    private Database db; // firestore database

    @Override
    public void onCreate(){
        super.onCreate();
    }

    /**
     * Returns the firebase database
     * @return The database stored in DataManager
     */
    public Database getdatabase() {
        if(db == null){
            throw new IllegalStateException("Database in DataManager was not initialized");
        }
        return db;
    }

    /**
     * Update the firebase database
     * @param Database The database stored in DataManager
     */
    public void setDatabase(Database database) {
        this.db = database;
    }
}
