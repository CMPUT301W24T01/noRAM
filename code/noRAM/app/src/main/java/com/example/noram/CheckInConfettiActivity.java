/* Class responsible for showing a fun confetti page after someone checks into an event. */
package com.example.noram;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noram.controller.EventManager;
import com.example.noram.model.Event;

import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;

/**
 * Activity that appears after a check-in that shows confetti
 * @maintainer Cole
 * @author Cole
 */
public class CheckInConfettiActivity extends AppCompatActivity {

    private KonfettiView confetti;
    private Party party;

    /**
     * When created, setup the confetti config, and button listeners.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_confetti);

        // setup confetti
        confetti = findViewById(R.id.confetti_view);
        EmitterConfig config = new Emitter(10, TimeUnit.SECONDS).perSecond(50);
        party = new PartyFactory(config)
                .angle(270)
                .spread(90)
                .setSpeedBetween(1f, 5f)
                .sizes(new Size(12, 5f, 0.2f))
                .position(0.0, 0.0, 1.0, 0.0)
                .build();

        // get event from bundle so we know what to display
        Bundle bundle = getIntent().getExtras();
        String eventId = bundle.getString("eventId");
        String eventName = bundle.getString("eventName");

        // update textview to display proper event name
        TextView haveFunEventText = findViewById(R.id.checkin_have_fun_event);
        haveFunEventText.setText(haveFunEventText.getText().toString().replace("<event>", eventName));

        // Create an event object that has the same ID as the event we are looking for.
        // When comparing events, we just check for the same ID, so we only need this
        // in order to go to the proper event from the event list.
        Event event = new Event();
        event.setId(eventId);

        // setup continue button to go to the event page
        Button continueButton = findViewById(R.id.checkin_continue_button);
        continueButton.setOnClickListener(v -> {
            EventManager.displayEvent(CheckInConfettiActivity.this, event);
            finish();
        });
    }

    /**
     * Start the confetti when the activity starts.
     */
    @Override
    protected void onStart() {
        super.onStart();
        confetti.start(party);
    }
}