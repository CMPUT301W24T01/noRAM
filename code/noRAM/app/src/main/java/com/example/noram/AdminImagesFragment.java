package com.example.noram;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.noram.controller.PhotoArrayAdapter;
import com.example.noram.model.Photo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Fragment of the Admin page, displaying all the images of the app's database
 * @author Gabriel
 */
public class AdminImagesFragment extends Fragment {

    /**
     * Default constructor
     * @return a new instance of this fragment
     */
    public static AdminImagesFragment newInstance() {
        return new AdminImagesFragment();
    }

    public GridView imagesGrid;
    ArrayList<Photo> imagesDataList;

    /**
     * Adds an item (image) from firebase's storage to the page's gridview
     * @param item Storage's item to be added
     */
    public void addPhotoToGrid(StorageReference item){
        // Create new photo and update with item's values
        Photo photo = new Photo();
        photo.setPhotoPath(item.getPath());
        photo.setBitmapFromDB(getContext());
        // store it
        imagesDataList.add(photo);
    }

    /**
     * Inflates the layout for this fragment
     * @param inflater the layout inflater
     * @param container the view group
     * @param savedInstanceState the saved instance state
     * @return the inflated layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_images, container, false);

        // get basic elements
        imagesGrid = rootView.findViewById(R.id.imagesGrid);
        imagesDataList = new ArrayList<Photo>();
        PhotoArrayAdapter imagesAdapter = new PhotoArrayAdapter(this.getContext(), imagesDataList);
        imagesGrid.setAdapter(imagesAdapter);

        // get all images from firebase's storage and store it(NOT updated in real-time!)
        MainActivity.db.getStorage().getReference("profile_photos/").listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {

                    @Override
                    public void onSuccess(ListResult listResult) {
                        for(StorageReference item : listResult.getItems()){
                            addPhotoToGrid(item);
                        }
                    }
                });
        MainActivity.db.getStorage().getReference("event_banners/").listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {

                    @Override
                    public void onSuccess(ListResult listResult) {
                        for(StorageReference item : listResult.getItems()){
                            addPhotoToGrid(item);
                        }
                    }
                });

        // update gridview
        imagesAdapter.notifyDataSetChanged();

                /*
        // connection each list element to show the 'delete' popup TODO: implement the delete function
        imagesGrid.setOnItemClickListener((parent, view, position, id) -> {
            // get document
            Photo photo = imagesDataList.get(position);

            // initialize popup
            ConfirmDeleteFragment deleteFragment = new ConfirmDeleteFragment();
            deleteFragment.setDeleteDoc(doc);
            deleteFragment.show(getParentFragmentManager(), "Delete Document");
        });
            */

        return rootView;
    }
}
