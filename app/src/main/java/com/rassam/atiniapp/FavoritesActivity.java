package com.rassam.atiniapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rassam.atiniapp.models.Item;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private List<Item> favoriteItems;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoriteItems = new ArrayList<>();
        adapter = new HomeAdapter(favoriteItems);
        recyclerView.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadFavoriteItems();
    }

    private void loadFavoriteItems() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    List<String> favoriteItemIds = (List<String>) task.getResult().get("favorites");
                    if (favoriteItemIds != null) {
                        for (String itemId : favoriteItemIds) {
                            db.collection("items").document(itemId).get().addOnCompleteListener(itemTask -> {
                                if (itemTask.isSuccessful() && itemTask.getResult() != null) {
                                    Item item = itemTask.getResult().toObject(Item.class);
                                    favoriteItems.add(item);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(FavoritesActivity.this, "Failed to load favorites", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
