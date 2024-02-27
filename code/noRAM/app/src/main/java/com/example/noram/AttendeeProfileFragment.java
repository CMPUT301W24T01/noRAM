/*
This file is used to display the attendee's profile information and allow for editing.
Outstanding Issues:
- The attendee's information is not saved to the database.
- The attendee's profile picture is not the correct type.
 */

package com.example.noram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.example.noram.model.Attendee;

/**
 * A {@link Fragment} subclass that displays and allows editing of attendee information.
 * Use the {@link AttendeeProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendeeProfileFragment extends Fragment {

    private Attendee attendee;

    /**
     * This is the default constructor for the fragment.
     */
    public AttendeeProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment AttendeeProfileFragment.
     */
    public static AttendeeProfileFragment newInstance() {
        AttendeeProfileFragment fragment = new AttendeeProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        ImageButton profileImage = view.findViewById(R.id.edit_attendee_image_button);
        EditText firstName = view.findViewById(R.id.edit_attendee_first_name);
        EditText lastName = view.findViewById(R.id.edit_attendee_last_name);
        EditText homePage = view.findViewById(R.id.edit_attendee_home_page);
        EditText email = view.findViewById(R.id.edit_attendee_email);
        CheckBox allowLocation = view.findViewById(R.id.edit_attendee_location_box);

        attendee = MainActivity.attendee;

        // Set the fields to the attendee's information
        firstName.setText(attendee.getFirstName());
        lastName.setText(attendee.getLastName());
        homePage.setText(attendee.getHomePage());
        email.setText(attendee.getEmail());
        allowLocation.setChecked(attendee.getAllowLocation());

        // Save the entered information when the save button is clicked
        view.findViewById(R.id.attendee_info_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editFirstName = firstName.getText().toString();
                String editLastName = lastName.getText().toString();
                String editHomePage = homePage.getText().toString();
                String editEmail = email.getText().toString();
                Boolean editAllowLocation = allowLocation.isChecked();

                if (validateAttendeeFields(editFirstName, editLastName, editHomePage, editEmail)) {
                    attendee.setFirstName(editFirstName);
                    attendee.setLastName(editLastName);
                    attendee.setHomePage(editHomePage);
                    attendee.setEmail(editEmail);
                    attendee.setAllowLocation(editAllowLocation);
                }
            }
        });

        // Revert the changes when the cancel button is clicked
        view.findViewById(R.id.attendee_info_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName.setText(attendee.getFirstName());
                lastName.setText(attendee.getLastName());
                homePage.setText(attendee.getHomePage());
                email.setText(attendee.getEmail());
                allowLocation.setChecked(attendee.getAllowLocation());
            }
        });

        return view;
    }

    /**
     * Validate the fields of the attendee information, return true if valid, false otherwise.
     * If it is not valid display a message saying what is wrong
     * @param editFirstName the first name that was entered in the field
     * @param editLastName the last name that was entered in the field
     * @param editHomePage the home page that was entered in the field
     * @param editEmail the email that was entered in the field
     * @return true if all fields are valid, false otherwise
     */
    public Boolean validateAttendeeFields(String editFirstName, String editLastName, String editHomePage, String editEmail) {
        // TODO: Validate the fields
        return false;
    }
}