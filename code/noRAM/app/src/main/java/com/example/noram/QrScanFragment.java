package com.example.noram;

import android.app.Activity;
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
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.WriteBatch;
import com.google.zxing.Result;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QrScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QrScanFragment extends Fragment {

    private CodeScanner mCodeScanner;

    private ProgressBar scanLoadingSpinBar;


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
        // https://github.com/yuriy-budiyev/code-scanner, Code Scanner Sample Usage, Yuriy Budiyev, retrieved Feb 18 2024
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_qr_scan, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        scanLoadingSpinBar = root.findViewById(R.id.scan_progress_ring);
        scanLoadingSpinBar.setVisibility(View.INVISIBLE);
        mCodeScanner = new CodeScanner(activity, scannerView);
        checkInFromQR("test_code");

        // Set up QR Code decode callback to check into event
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO in future need to check if promo or checkin code.
                        String qrDecoded = result.getText();
                        checkInFromQR(qrDecoded);
                        scanLoadingSpinBar.setVisibility(View.VISIBLE);
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

        return root;
    }

    /**
     * Checks the app user into the event associated with the scanned qr code
     * @param qrCodeString qr code encoded string
     */
    private void checkInFromQR(String qrCodeString) {
        DocumentReference doc = MainActivity.db.getQrRef().document(qrCodeString);
        scanLoadingSpinBar.setVisibility(View.VISIBLE);
        Log.d("DEBUG", "trying to checkin");
        doc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Log.d("DEBUG", "got qr code");

                if (document.exists()) {
                    Log.d("DEBUG", "code exists");
                    // TODO: actual event
                    // TODO: the list of eventsAt needs to be preserved in the attendee class
                    String eventName = (String) document.get("event");

                    // Create a batched write to update attendee eventAt and event attendees
                    WriteBatch batch = MainActivity.db.getDb().batch();
                    DocumentReference eventRef = MainActivity.db.getEventsRef()
                            .document(eventName);
                    batch.update(
                            eventRef,
                            "attendees",
                            FieldValue.arrayUnion(MainActivity.attendee.getIdentifier()));
                    DocumentReference attendeeRef = MainActivity.db.getAttendeeRef()
                            .document(MainActivity.attendee.getIdentifier());
                    batch.update(
                            attendeeRef,
                            "eventsAt",
                            FieldValue.arrayUnion(eventName));
                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showCheckInSuccess(eventName);
                            } else {
                                showCheckInFailure("batch write failed.");
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Shows a toast when check in succeeds
     * @param event name of the event to include in message
     */
    private void showCheckInSuccess(String event) {
        scanLoadingSpinBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getActivity(), "Signed into " + event + "!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a toast when check in fails
     * @param errMsg error message to include
     */
    private void showCheckInFailure(String errMsg) {
        scanLoadingSpinBar.setVisibility(View.VISIBLE);
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