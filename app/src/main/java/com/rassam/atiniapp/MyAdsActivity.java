package com.rassam.atiniapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rassam.atiniapp.models.Item;

import java.util.ArrayList;
import java.util.List;

public class MyAdsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private List<Item> itemList;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();

        // Pass an empty OnItemClickListener implementation if no action is needed on item click
        adapter = new HomeAdapter(itemList, item -> {
            // Handle item click if needed
        });
        recyclerView.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadMyAds();
    }

    private void loadMyAds() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("items").whereEqualTo("userId", userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    itemList.clear();
                    for (DocumentSnapshot document : task.getResult()) {
                        Item item = document.toObject(Item.class);
                        itemList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MyAdsActivity.this, "Failed to load ads", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
