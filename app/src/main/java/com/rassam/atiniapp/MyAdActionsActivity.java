package com.rassam.atiniapp;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.rassam.atiniapp.models.Item;

public class MyAdActionsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String itemId;
    private Item currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ad_actions);

        db = FirebaseFirestore.getInstance();

        // Get item ID from intent
        itemId = getIntent().getStringExtra("itemId");

        // Fetch item data from Firestore
        fetchItemData();

        // Set up click listeners for buttons/switches
        Button deleteButton = findViewById(R.id.deleteButton);
        Button reserveButton = findViewById(R.id.reserveButton); // Or use a Switch

        deleteButton.setOnClickListener(v -> deleteItem());
        reserveButton.setOnClickListener(v -> toggleReservation());
    }

    private void fetchItemData() {
        db.collection("items").document(itemId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentItem = documentSnapshot.toObject(Item.class);
                    // Update UI with item details
                    // ...
                })
                .addOnFailureListener(e -> {
                    // Handle error fetching item data
                    Toast.makeText(this, "Failed to load item details", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteItem() {
        db.collection("items").document(itemId).delete()
                .addOnSuccessListener(aVoid -> {
                    // Item deleted successfully
                    Toast.makeText(this, "Ad deleted", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    // Handle error deleting item
                    Toast.makeText(this, "Failed to delete ad", Toast.LENGTH_SHORT).show();
                });
    }

    private void toggleReservation() {
        if (currentItem != null) {
            boolean newReservationStatus = !currentItem.isReserved();
            db.collection("items").document(itemId)
                    .update("isReserved", newReservationStatus)
                    .addOnSuccessListener(aVoid -> {
                        // Reservation status updated
                        currentItem.setReserved(newReservationStatus);
                        // Update UI to reflect the change
                        // ...
                        Toast.makeText(this, "Reservation updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle error updating reservation
                        Toast.makeText(this, "Failed to update reservation", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}