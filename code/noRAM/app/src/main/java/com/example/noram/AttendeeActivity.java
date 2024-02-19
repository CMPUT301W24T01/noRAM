package com.example.noram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.noram.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.Result;

public class AttendeeActivity extends AppCompatActivity {

    // https://github.com/yuriy-budiyev/code-scanner, Code Scanner Sample Usage, Yuriy Budiyev, retrieved Feb 18 2024
    private CodeScanner mCodeScanner;

    /**
     * Setup the activity when it is created.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        BottomNavigationView navBar = findViewById(R.id.bottom_nav);
        navBar.setSelectedItemId(R.id.navbar_scan);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AttendeeActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    /**
     * Runs when the activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    /**
     * Runs when the activity is paused.
     */
    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}