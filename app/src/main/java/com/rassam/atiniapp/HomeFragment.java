package com.rassam.atiniapp;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rassam.atiniapp.models.Item;
import com.rassam.atiniapp.models.Reservation;
import com.rassam.atiniapp.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private List<Item> itemList;
private TextView notifCountTextView,watermelonCountTextView;
    private SearchView searchView;
    private Spinner spinnerStatusFilter, spinnerCategoryFilter,editTextLocation;
    private  User user;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        editTextLocation=view.findViewById(R.id.spinnerLocation);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) view.findViewById(R.id.searchView);
        spinnerStatusFilter = view.findViewById(R.id.spinnerStatus);
        spinnerCategoryFilter = view.findViewById(R.id.spinnerCategoryFilter);
        notifCountTextView = view.findViewById(R.id.textViewNotif);
        notifCountTextView.setShadowLayer(4f, 2f, 2f, Color.BLACK); // Adjust values as needed

        watermelonCountTextView = view.findViewById(R.id.textViewWatermilon);
        watermelonCountTextView.setShadowLayer(4f, 2f, 2f, Color.BLACK);
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        itemList = new ArrayList<>();

        adapter = new HomeAdapter(getActivity(), itemList, item -> {
            // Handle item click here, e.g., open detail view
            openItemDetails(item.getId());
        });

        recyclerView.setAdapter(adapter);

        fetchAllItems();



        return view;
    }
    private void populateTextView(TextView textView, int count) {
        if (count > 0) {
            textView.setText(String.valueOf(count));
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }
    private int fetchNotificationsCount() {
        // Logic to fetch the number of notifications
        return user.getNotifications().size(); // Replace with actual count
    }

    private int fetchWatermelonsCount() {
        // Logic to fetch the number of watermelons
        return user.getWatermelon(); // Replace with actual count
    }
    @Override
public void onResume() {
            super.onResume();
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore db= FirebaseFirestore.getInstance();
                db.collection("users").document(userId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                user = documentSnapshot.toObject(User.class);
                                checkPastDueReservations(user);
                                int numNotifications = fetchNotificationsCount();
                                int numWatermelons = fetchWatermelonsCount();
                                populateTextView(notifCountTextView, numNotifications);
                                populateTextView(watermelonCountTextView, numWatermelons);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error fetching user data", Toast.LENGTH_SHORT).show();
                        });
            }



        }
    private void checkPastDueReservations(User user) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("items").whereEqualTo("reservation.reserved", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String itemId = document.getId(); // Get the item ID
                        Item item = document.toObject(Item.class);
                        Reservation reservation = item.getReservation();

                        if ((reservation.getSenderUserId().equals(userId) || reservation.getReceiverUserId().equals(userId))
                                && reservation.getDueDate().before(new Date())) {
                            // Found a past due reservation where the user is involved
                            Toast.makeText(getContext(), "You have a past duereservation for item: " + item.getTitle(), Toast.LENGTH_SHORT).show();

                            // Update watermelon count
                            int updatedWatermelonCount = user.getWatermelon();
                            if (reservation.getReceiverUserId().equals(userId)) {
                                updatedWatermelonCount--;
                            } else if (reservation.getSenderUserId().equals(userId)) {
                                updatedWatermelonCount++;
                            }
                            user.setWatermelon(updatedWatermelonCount);

                            // Update user document in Firestore (watermelon count)
                            db.collection("users").document(userId).update("watermelon", updatedWatermelonCount);
                            // ... (success and failure handlers)

                            // Reset reservation in item document
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("reservation.isReserved", false);
                            updates.put("reservation.senderUserId", "");
                            updates.put("reservation.receiverUserId", "");
                            updates.put("reservation.dueDate", null); // Or a default date if needed

                            db.collection("items").document(itemId)
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> {// Reservation reset successfully
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle error resetting reservation
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error fetching items
                });
    }

private void performSearch(String query) {
    // Remove all filters for now
    String searchText = query.trim().toLowerCase(); // Convert search to lowercase
        String selectedStatus = spinnerStatusFilter.getSelectedItem().toString();
       String selectedCategory = spinnerCategoryFilter.getSelectedItem().toString();
        String selectedLocation = editTextLocation.getSelectedItem().toString();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Query firebaseQuery = db.collection("items");

    if (!TextUtils.isEmpty(searchText)) {
        firebaseQuery = firebaseQuery.whereArrayContains("keywords", searchText);
    }
    if (!selectedStatus.equals("All")) { // Assuming "All" is the default option
        firebaseQuery = firebaseQuery.whereEqualTo("status", selectedStatus);
    }

    // Handle category filter
    if (!selectedCategory.equals("All")) {// Assuming "All Categories" is the default
        firebaseQuery = firebaseQuery.whereEqualTo("category", selectedCategory);
    }

    // Handle location filter
    if (!selectedLocation.equals("All")) { // Assuming "All Locations" is the default
        firebaseQuery = firebaseQuery.whereEqualTo("location", selectedLocation);
    }
    firebaseQuery.get().addOnCompleteListener(task -> {
        if (task.isSuccessful() && task.getResult() != null) {
            itemList.clear();
            itemList.addAll(task.getResult().toObjects(Item.class));
            for (Item item : itemList) {
                Log.d("SearchDebug", "Fetched Item Title: " + item.getTitle());}
            Log.d("SearchDebug", "Selected text: " + searchText);

            Log.d("SearchDebug", "Selected Status: " + selectedStatus);
            Log.d("SearchDebug", "Selected Category: " +selectedCategory);
            Log.d("SearchDebug", "Selected Location: " + selectedLocation);

            // Logthe number of fetched items
            Log.d("SearchDebug", "Fetched " + itemList.size() + " items.");

            adapter.notifyDataSetChanged();
        } else {
            Log.e("SearchDebug", "Query failed or returned null.");
        }
    });
}
    private void openItemDetails(String itemId) {
        ItemDetailFragment fragment = ItemDetailFragment.newInstance(itemId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void fetchAllItems() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        itemList.clear();
                        itemList.addAll(task.getResult().toObjects(Item.class));
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to load items", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
