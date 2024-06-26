/*
This file is used to create the QR scanning fragment. It uses the CodeScanner library to scan QR codes.
Outstanding Issues:
- Sometimes the camera loads very slowly - see if this can be fixed
 */

package com.example.noram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.example.noram.controller.EventManager;
import com.example.noram.model.HashHelper;
import com.example.noram.model.QRType;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.example.noram.controller.EventManager;
import com.google.firebase.firestore.Transaction;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QrScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 * @maintainer Cole
 * @author Cole
 */
public class QrScanFragment extends Fragment {

    // Attributes
    private CodeScanner mCodeScanner;
    private ProgressBar scanLoadingSpinBar;
    private GoToEventListener goToEventListener;

    /**
     * Empty constructor for QrScanFragment
     */
    public QrScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment qr_scan.
     */
    public static QrScanFragment newInstance() {
        QrScanFragment fragment = new QrScanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Create the fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Creates the view for the QRScan Fragment
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View for the fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if (activity instanceof GoToEventListener) {
            goToEventListener = (GoToEventListener) activity;
        } else {
            throw new IllegalArgumentException("Activity must extend goToEventListener");
        }
        View root = inflater.inflate(R.layout.fragment_qr_scan, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        scanLoadingSpinBar = root.findViewById(R.id.scan_progress_ring);
        scanLoadingSpinBar.setVisibility(View.INVISIBLE);
        mCodeScanner = new CodeScanner(activity, scannerView);
        // Reference: https://github.com/yuriy-budiyev/code-scanner, Code Scanner Sample Usage, Yuriy Budiyev, retrieved Feb 18 2024
        // Set up QR Code decode callback to check into event
        mCodeScanner.setDecodeCallback(result -> activity.runOnUiThread(() -> {
            String qrDecoded = result.getText();
            processQRCode(qrDecoded);
            scanLoadingSpinBar.setVisibility(View.VISIBLE);
        }));

        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());

        return root;
    }

    /**
     * Processes the scanned QR code based on the encoded data
     * @param qrCodeString qr code encoded string
     */
    @SuppressLint("MissingPermission")
    private void processQRCode(String qrCodeString) {
        String qrDocId = HashHelper.hashSHA256(qrCodeString);
        DocumentReference doc = MainActivity.db.getQrRef().document(qrDocId);
        scanLoadingSpinBar.setVisibility(View.VISIBLE);

        doc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot qrDocument = task.getResult();
                if (!qrDocument.exists()) {
                    showCheckInFailure("Event not found for the QR code.");
                    return;
                }
                // Get event id and qr type
                String eventId = qrDocument.getString("event");

                QRType qrType = QRType.valueOf(qrDocument.getString("type"));

                if (qrType == QRType.SIGN_IN) {
                    if (MainActivity.attendee.getAllowLocation()) {
                        getLocationQREntry(eventId);
                    }
                    else{
                        EventManager.checkInToEvent(eventId, null);
                        //sign up and confetti
                        EventManager.signUpForEvent(eventId, false);
                    }
                    showCheckInSuccess();
                    goToEventListener.goToConfetti(eventId);
                } else {
                    // tell the activity to go to the event
                    goToEventListener.goToEvent(eventId);
                }

                scanLoadingSpinBar.setVisibility(View.INVISIBLE);
            }
        });
    }
    /**
     * Get the location of the user from the phone.
     * uses the FusedLocationProviderClient provided by Google API to access location.
     * If location is accessed, goto eventManager.checkInEvent() to update db
     * If location access fails, exit map.
     * @suppress the permission check because I check it in onCreate()
     * @param ID the event ID
     */
    @SuppressLint("MissingPermission")
    private void getLocationQREntry(String ID) {
        //grab the FusedLocationProviderClient builder
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        //attempt to get the location
        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            /**
             On successful acquisition of user's location, pass it
             @param location the location to be added to the database
             */
            @Override
            public void onSuccess(Location location) {
                //and the location if it is not null
                if (location != null) {
                    EventManager.checkInToEvent(ID, location);
                    //sign up and confetti
                    EventManager.signUpForEvent(ID, false);
                } else {
                    //location was not provided
                    Toast.makeText(getContext(), "Location tracking denied, signing-in without location", Toast.LENGTH_LONG).show();
                    EventManager.checkInToEvent(ID, null);
                    //sign up and confetti
                    EventManager.signUpForEvent(ID, false);
                }
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            /**
             On failed acquisition of user's location, report error
             @param e the exception that occurred.
             */
            @Override
            public void onFailure(@NonNull Exception e) {
                //user provided permission to use location but the location was not properly received.
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to retrieve location, internal error. Signing-in without location", Toast.LENGTH_LONG).show();
                EventManager.checkInToEvent(ID, null);
            }
        });
    };
    /**
     * Shows a toast when check in succeeds
     */
    private void showCheckInSuccess() {
        scanLoadingSpinBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getActivity(), "Successfully checked in!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a toast when check in fails
     * @param errMsg error message to include
     */
    private void showCheckInFailure(String errMsg) {
        scanLoadingSpinBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getActivity(), "Couldn't check in: " + errMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when fragment is resumed.
     */
    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    /**
     * Called when fragment is paused.
     */
    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}