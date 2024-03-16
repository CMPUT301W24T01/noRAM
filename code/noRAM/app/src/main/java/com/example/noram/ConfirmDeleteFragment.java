package com.example.noram;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

/**
 * DialogFragment that shows a popup to confirm the user wants to delete a specific document from
 * the firestore database
 * @author Gabriel
 */
public class ConfirmDeleteFragment extends DialogFragment {

    private DocumentReference docToDelete; // document that will be deleted

    /**
     * Deletes the document 'docToDelete' from the database
     */
    private void deleteDoc(){
        docToDelete.delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AdminDeleteDoc", "Could not delete the document from admin " +
                                "page");
                    }
                });
    }

    /**
     * Sets the document that will be later deleted from the database
     * @param doc The reference to the document that will be deleted from the database
     */
    public void setDeleteDoc(DocumentReference doc){
        docToDelete = doc;
    }

    /**
     * Prompts user to confirm the deletion of the document they clicked on or cancel
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return The (alert) dialog fragment that lets user confirm their choice or cancel
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setTitle("Confirm delete?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    deleteDoc();
                })
                .create();
    }

}
