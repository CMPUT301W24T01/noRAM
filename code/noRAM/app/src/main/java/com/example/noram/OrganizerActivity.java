package com.example.noram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class OrganizerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        // Temp stuff for add event
        Button temp = findViewById(R.id.temp_button_for_add_event);
        temp.setOnClickListener(v ->
                startActivity(new Intent(OrganizerActivity.this, AddEventActivity.class))
        );
    }
}