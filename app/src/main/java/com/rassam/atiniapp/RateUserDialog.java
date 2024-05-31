package com.rassam.atiniapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rassam.atiniapp.models.Rating;

public class RateUserDialog extends DialogFragment {

    private RatingBar ratingBar;
    private EditText editTextComment;
    private Button buttonSubmit;

    private String ratedUserId;

    public RateUserDialog(String ratedUserId) {
        this.ratedUserId = ratedUserId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setContentView(R.layout.dialog_rate_user);

        ratingBar = dialog.findViewById(R.id.ratingBar);
        editTextComment = dialog.findViewById(R.id.editTextComment);
        buttonSubmit = dialog.findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRating();
            }
        });

        return dialog;
    }

    private void submitRating() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "You need to be logged in to submit a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        float ratingValue = ratingBar.getRating();
        String comment = editTextComment.getText().toString().trim();
        String ratingUserId = currentUser.getUid();

        Rating rating = new Rating(ratedUserId, ratingUserId, ratingValue, comment, System.currentTimeMillis());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ratings").add(rating)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Rating submitted", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to submit rating", Toast.LENGTH_SHORT).show();
                });
    }
}
