package com.example.noram;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.noram.controller.AttendeeArrayAdapter;
import com.example.noram.controller.EventArrayAdapter;
import com.example.noram.model.Attendee;
import com.example.noram.model.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment of the Admin page, listing all the profiles of the app's database
 * @author Gabriel
 */
public class AdminProfilesFragment extends Fragment {

    /**
     * Default constructor
     * @return a new instance of this fragment
     */
    public static AdminProfilesFragment newInstance() {
        return new AdminProfilesFragment();
    }

    public ListView profilesList;
    ArrayList<Attendee> profilesDataList;
    ArrayAdapter<Attendee> profilesAdapter;
    CollectionReference attendeeRef;

    /**
     * Inflates the layout for this fragment
     * @param inflater the layout inflater
     * @param container the view group
     * @param savedInstanceState the saved instance state
     * @return the inflated layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_profiles, container, false);

        // get basic elements
        profilesList = rootView.findViewById(R.id.profilesList);
        profilesDataList = new ArrayList<Attendee>();
        profilesAdapter = new AttendeeArrayAdapter(this.getContext(), profilesDataList);
        profilesList.setAdapter(profilesAdapter);
        attendeeRef = MainActivity.db.getAttendeeRef();

        // connection each list element to show the 'delete' popup
        profilesList.setOnItemClickListener((parent, view, position, id) -> {
            // get document
            Attendee attendee = profilesDataList.get(position);
            DocumentReference doc = attendeeRef.document(attendee.getIdentifier());

            // initialize popup
            ConfirmDeleteFragment deleteFragment = new ConfirmDeleteFragment();
            deleteFragment.setDeleteDoc(doc);
            deleteFragment.show(getParentFragmentManager(), "Delete Document");
        });

        // connect database to list
        attendeeRef.addSnapshotListener((querySnapshots, error) -> {
            if(error != null){
                Log.e("Firestore", error.toString());
                return;
            }
            if(querySnapshots != null){
                profilesDataList.clear();
                for(QueryDocumentSnapshot doc: querySnapshots){
                    // get attendee info and create Attendee instance
                    Attendee attendee = new Attendee(
                            doc.getString("identifier"),
                            doc.getString("firstName"),
                            doc.getString("lastName"),
                            doc.getString("homePage"),
                            doc.getString("email"),
                            doc.getBoolean("allowLocation"),
                            doc.getBoolean("defaultsProfilePhoto"),
                            (List<String>) doc.get("eventsCheckedInto")
                    );
                    profilesDataList.add(attendee);
                }
                profilesAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }
}
