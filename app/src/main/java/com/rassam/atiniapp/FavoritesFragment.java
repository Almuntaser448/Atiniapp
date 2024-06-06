package com.rassam.atiniapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rassam.atiniapp.models.Item;
import com.rassam.atiniapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    private List<Item> favoriteItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        favoriteItems = new ArrayList<>(); // Initialize the list

        // Get the current Firebase user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Retrieve the favorites of the current user from Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // User document exists, retrieve favorites
                                User user = document.toObject(User.class);
                                if (user != null && user.getFavorites() != null) {
                                    List<String> favoriteItemIds = user.getFavorites();
                                    List<Item> fetchedItems = new ArrayList<>(); // To store fetched items

                                    for (String itemId : favoriteItemIds) {
                                        db.collection("items").document(itemId).get().addOnCompleteListener(itemTask -> {
                                            if (itemTask.isSuccessful() && itemTask.getResult() != null) {
                                                Item item = itemTask.getResult().toObject(Item.class);
                                                fetchedItems.add(item);

                                                // Update adapter when all items are fetched
                                                if (fetchedItems.size() == favoriteItemIds.size()) {
                                                    adapter = new FavoritesAdapter(fetchedItems);
                                                    recyclerView.setAdapter(adapter);
                                                }
                                            } else {
                                                // Handle error fetching item
                                            }
                                        });
                                    }
                                }
                            }
                        } else {
                            // Handle errors
                            // Log.d(TAG, "Error getting user document: ", task.getException());
                        }
                    });
        }

        return view;
    }

}
