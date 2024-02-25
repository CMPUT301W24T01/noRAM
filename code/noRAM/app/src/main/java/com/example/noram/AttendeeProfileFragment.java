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
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendeeProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendeeProfileFragment extends Fragment {

    private Attendee attendee;

    public AttendeeProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AttendeeProfileFragment.
     */
    public static AttendeeProfileFragment newInstance() {
        AttendeeProfileFragment fragment = new AttendeeProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendee_profile, container, false);

        ImageButton profileImage = view.findViewById(R.id.edit_attendee_image_button);
        EditText firstName = view.findViewById(R.id.edit_attendee_first_name);
        EditText lastName = view.findViewById(R.id.edit_attendee_last_name);
        EditText homePage = view.findViewById(R.id.edit_attendee_home_page);
        EditText phone = view.findViewById(R.id.edit_attendee_phone);
        CheckBox allowLocation = view.findViewById(R.id.edit_attendee_location_box);

        // TODO: get the attendee from the database
        if (attendee == null) {
            attendee = new Attendee(1234, "John", "Doe", "john.com", "123-456-7890", null, true);
        }

        // Set the fields to the attendee's information
        firstName.setText(attendee.getFirstName());
        lastName.setText(attendee.getLastName());
        homePage.setText(attendee.getHomePage());
        phone.setText(attendee.getPhoneNumber());
        allowLocation.setChecked(attendee.getAllowLocation());

        // Save the entered information when the save button is clicked
        view.findViewById(R.id.attendee_info_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: save the attendee profile picture
                attendee.setFirstName(firstName.getText().toString());
                attendee.setLastName(lastName.getText().toString());
                attendee.setHomePage(homePage.getText().toString());
                attendee.setPhoneNumber(phone.getText().toString());
                attendee.setAllowLocation(allowLocation.isChecked());
                // TODO: save the attendee to the database
            }
        });

        view.findViewById(R.id.attendee_info_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName.setText(attendee.getFirstName());
                lastName.setText(attendee.getLastName());
                homePage.setText(attendee.getHomePage());
                phone.setText(attendee.getPhoneNumber());
                allowLocation.setChecked(attendee.getAllowLocation());
            }
        });

        return view;
    }
}