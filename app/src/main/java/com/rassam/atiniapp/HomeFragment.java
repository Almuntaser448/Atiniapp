package com.rassam.atiniapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rassam.atiniapp.models.Item;
import com.rassam.atiniapp.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private List<Item> itemList;
    private User currentUser;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Sample data
        itemList = new ArrayList<>();
        itemList.add(new Item("1", "Title 1", "Description 1"));
        itemList.add(new Item("2", "Title 2", "Description 2"));
        itemList.add(new Item("3", "Title 3", "Description 3"));

        adapter = new HomeAdapter(itemList, this::addToFavorites);
        recyclerView.setAdapter(adapter);

        fetchCurrentUser();

        return view;
    }

    private void fetchCurrentUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        currentUser = document.toObject(User.class);
                    } else {
                        // Handle case where the user document doesn't exist
                        currentUser = new User(userId, "John Doe"); // Placeholder
                        db.collection("users").document(userId).set(currentUser);
                    }
                } else {
                    // Handle errors
                    Toast.makeText(getActivity(), "Failed to fetch user", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle user not logged in
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToFavorites(Item item) {
        if (currentUser != null) {
            String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                currentUser.getFavorites().add(item);
                db.collection("users").document(userId).set(currentUser)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getActivity(), item.getTitle() + " added to favorites", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "Error adding to favorites", Toast.LENGTH_SHORT).show();
                        });
           } else {
            Toast.makeText(getActivity(), "Current user is null", Toast.LENGTH_SHORT).show();
        }
    }
}
