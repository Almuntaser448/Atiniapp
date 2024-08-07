package com.rassam.atiniapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rassam.atiniapp.models.Rating;
import com.rassam.atiniapp.models.User;


public class ProfileManagementFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView textViewAverageRating;
    private TextView textViewUserName;
    private ImageView profileImageView;
    private TextView profileNameTextView, profileRatingTextView;
    private Button myAdsButton, favoritesButton, myReviewsButton, settingsButton, logoutButton;
    private FirebaseAuth firebaseAuth;

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
        profileImageView = view.findViewById(R.id.profileImageView);
        profileNameTextView = view.findViewById(R.id.profileNameTextView);
        profileRatingTextView = view.findViewById(R.id.profileRatingTextView);
        myAdsButton = view.findViewById(R.id.myAdsButton);
        favoritesButton = view.findViewById(R.id.favoritesButton);
        myReviewsButton = view.findViewById(R.id.myReviewsButton);
        settingsButton = view.findViewById(R.id.settingsButton);
        logoutButton = view.findViewById(R.id.logoutButton);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserProfile();

        myAdsButton.setOnClickListener(v -> startActivity(new Intent(requireActivity(), MyAdsActivity.class)));
        favoritesButton.setOnClickListener(v -> startActivity(new Intent(requireActivity(), FavoritesActivity.class)));
        myReviewsButton.setOnClickListener(v -> startActivity(new Intent(requireActivity(), MyReviewsActivity.class)));
        settingsButton.setOnClickListener(v -> startActivity(new Intent(requireActivity(), SettingsActivity.class)));
        logoutButton.setOnClickListener(v -> {
            Log.d("ProfileManagementFragment", "Logout TextView clicked");
            logout();});

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    User user = document.toObject(User.class);
                    if (user != null) {
                        profileNameTextView.setText(user.getUsername());
                        profileRatingTextView.setText("Rating: " + user.getRating());
                        Glide.with(this).load(user.getProfilePhotoUrl()).into(profileImageView);
                    }
                } else {
                    Toast.makeText(getActivity(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();

        startActivity(new Intent(getActivity(), AuthActivity.class));
        getActivity().finish();
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