package com.rassam.atiniapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rassam.atiniapp.models.Rating;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView textViewAverageRating;
    private TextView textViewUserName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textViewAverageRating = view.findViewById(R.id.textViewAverageRating);
        textViewUserName = view.findViewById(R.id.textViewUserName);

        fetchCurrentUser();
        loadAverageRating();

        return view;
    }

    private void fetchCurrentUser() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String userName = task.getResult().getString("name");
                    textViewUserName.setText(userName != null ? userName : "Anonymous");
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch user", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadAverageRating() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            db.collection("ratings").whereEqualTo("ratedUserId", userId).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        double totalRating = 0;
                        int count = 0;

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Rating rating = document.toObject(Rating.class);
                            totalRating += rating.getRating();
                            count++;
                        }

                        if (count > 0) {
                            double averageRating = totalRating / count;
                            textViewAverageRating.setText(String.format("Average Rating: %.1f", averageRating));
                        } else {
                            textViewAverageRating.setText("No ratings yet");
                        }
                    })
                    .addOnFailureListener(e -> {
                        textViewAverageRating.setText("Failed to load ratings");
                    });
        }
    }
}
