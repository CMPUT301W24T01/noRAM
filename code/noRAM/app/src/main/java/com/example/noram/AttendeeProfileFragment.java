/*
This file is used to display the attendee's profile information and allow for editing.
Outstanding Issues:
- The attendee's information is not saved to the database.
- The attendee's profile picture is not the correct type.
 */

package com.example.noram;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.noram.model.Attendee;

/**
 * A {@link Fragment} subclass.
 * AttendeeProfileFragment is a fragment that displays the attendee's profile information
 * It allows the user to edit their information and save it to the database.
 * It also displays information about their profile stored in the database.
 */
public class AttendeeProfileFragment extends Fragment {

    private ImageButton profileImage;
    private Attendee attendee;
    private EditText firstName;
    private EditText lastName;
    private EditText homePage;
    private EditText email;
    private CheckBox allowLocation;
    private Boolean firstEntry;

    /**
     * This is the default constructor for the fragment.
     */
    public AttendeeProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     * @param firstEntry This is the main user argument, whether this is the initial info entry for a user
     * on the app.
     *
     * @return A new instance of fragment AttendeeProfileFragment.
     */
    public static AttendeeProfileFragment newInstance(Boolean firstEntry) {
        AttendeeProfileFragment fragment = new AttendeeProfileFragment();
        Bundle args = new Bundle();
        args.putBoolean("firstEntry", firstEntry);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * This method is called when the fragment is created.
     * @param args This is the bundle of arguments that are passed to the fragment.
     * The main user argument is whether this is the initial info entry for a user
     */
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        firstEntry = args.getBoolean("firstEntry");
    }

    /**
     * This method is called when the fragment is created.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This method is called when the fragment is created, it initializes the
     * information and sets up listeners.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The view to be displayed with all attached listeners
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendee_profile, container, false);

        // Get the fields from the view
        profileImage = view.findViewById(R.id.edit_attendee_image_button);
        firstName = view.findViewById(R.id.edit_attendee_first_name);
        lastName = view.findViewById(R.id.edit_attendee_last_name);
        homePage = view.findViewById(R.id.edit_attendee_home_page);
        email = view.findViewById(R.id.edit_attendee_email);
        allowLocation = view.findViewById(R.id.edit_attendee_location_box);

        attendee = MainActivity.attendee;

        setFields(attendee);

        // Save the entered information when the save button is clicked
        view.findViewById(R.id.attendee_info_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editFirstName = firstName.getText().toString();
                String editLastName = lastName.getText().toString();
                String editHomePage = homePage.getText().toString();
                String editEmail = email.getText().toString();
                Boolean editAllowLocation = allowLocation.isChecked();

                // Validate name and email fields
                if (validateAttendeeFields(editFirstName, editLastName, editEmail)) {
                    attendee.setFirstName(editFirstName);
                    attendee.setLastName(editLastName);
                    attendee.setHomePage(editHomePage);
                    attendee.setEmail(editEmail);
                    attendee.setAllowLocation(editAllowLocation);

                    // Close the fragment if this is the first entry
                    if (firstEntry) {
                        FragmentActivity activity = getActivity();
                        if (activity != null) {
                            activity.findViewById(R.id.main_fragment_container_view).setVisibility(View.GONE);
                            activity.findViewById(R.id.organizerButton).setVisibility(View.VISIBLE);
                            activity.findViewById(R.id.attendeeButton).setVisibility(View.VISIBLE);
                            activity.getSupportFragmentManager().beginTransaction().remove(AttendeeProfileFragment.this).commit();
                        }
                    }
                }
            }
        });

        // Revert the changes when the cancel button is clicked
        view.findViewById(R.id.attendee_info_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFields(attendee);
            }
        });
        return view;
    }

    /**
     * This method is called when the fragment is resumed.
     * It sets the fields to the attendee's information,
     * ensuring that any unsaved changes are reset.
     */
    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        attendee = MainActivity.attendee;

        if (view != null) {
            setFields(attendee);
        }
    }

    /**
     * Set the fields of the view to the attendee's information
     * @param attendee the attendee whose information is being displayed
     */
    public void setFields(Attendee attendee) {
        // Set the fields to the attendee's information
        firstName.setText(attendee.getFirstName());
        lastName.setText(attendee.getLastName());
        homePage.setText(attendee.getHomePage());
        email.setText(attendee.getEmail());
        allowLocation.setChecked(attendee.getAllowLocation());
    }

    /**
     * Validate the fields of the attendee information, return true if valid, false otherwise.
     * If it is not valid display a message saying what is wrong
     * @param editFirstName the first name that was entered in the field
     * @param editLastName the last name that was entered in the field
     * @param editEmail the email that was entered in the field
     * @return true if all fields are valid, false otherwise
     */
    public Boolean validateAttendeeFields(String editFirstName, String editLastName, String editEmail) {
        if (editFirstName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter your first name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (editLastName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter your last name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (editEmail.isEmpty()) {
            Toast.makeText(getContext(), "Please enter your email", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(editEmail).matches()) {
            Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}