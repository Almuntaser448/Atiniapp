package com.rassam.atiniapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.rassam.atiniapp.models.User;

public class ProfileManagementFragment extends Fragment {

    private ImageView profileImageView;
    private TextView profileNameTextView, profileRatingTextView;
    private TextView myAdsTextView, favoritesTextView, followedUsersTextView, myReviewsTextView, settingsTextView, logoutTextView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_management, container, false);

        profileImageView = view.findViewById(R.id.profileImageView);
        profileNameTextView = view.findViewById(R.id.profileNameTextView);
        profileRatingTextView = view.findViewById(R.id.profileRatingTextView);
        myAdsTextView = view.findViewById(R.id.myAdsTextView);
        favoritesTextView = view.findViewById(R.id.favoritesTextView);
        followedUsersTextView = view.findViewById(R.id.followedUsersTextView);
        myReviewsTextView = view.findViewById(R.id.myReviewsTextView);
        settingsTextView = view.findViewById(R.id.settingsTextView);
        logoutTextView = view.findViewById(R.id.logoutTextView);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserProfile();

        myAdsTextView.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyAdsActivity.class)));
        favoritesTextView.setOnClickListener(v -> startActivity(new Intent(getActivity(), FavoritesActivity.class)));
        followedUsersTextView.setOnClickListener(v -> startActivity(new Intent(getActivity(), FollowedUsersActivity.class)));
        myReviewsTextView.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyReviewsActivity.class)));
        settingsTextView.setOnClickListener(v -> startActivity(new Intent(getActivity(), SettingsActivity.class)));
        logoutTextView.setOnClickListener(v -> logout());

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
                        profileNameTextView.setText(user.getName());
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
        firebaseAuth.signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }
}
