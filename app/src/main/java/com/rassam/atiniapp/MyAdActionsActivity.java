package com.rassam.atiniapp;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.graphics.Color;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rassam.atiniapp.models.Item;
import com.rassam.atiniapp.models.Notification;
import com.rassam.atiniapp.models.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdActionsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String itemId;
    private Item currentItem;
    private ViewPager2 viewPagerImages;
    private ImageSliderAdapter imageAdapter;
    ChatPartnerAdapter chatPartnerAdapter;
    RecyclerView recyclerView; // Declare recyclerView@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ad_actions); // Set the layout for the Activity
        db = FirebaseFirestore.getInstance();


        chatPartnerAdapter = new ChatPartnerAdapter(user -> {
            Log.d("ChatPartnerClick", "Clicked on user: " + user.getUsername());

            showDatePicker(user,user.getUserId());
        });
        recyclerView = findViewById(R.id.chatPartnerRecyclerView);
        recyclerView.setAdapter(chatPartnerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            Log.d("MyAdActionsActivity", "RecyclerView layout changed. Visible: " + (recyclerView.getVisibility() == View.VISIBLE));
        });
        itemId = getIntent().getStringExtra("itemId");

        viewPagerImages = findViewById(R.id.viewPagerImages); // Initialize ViewPager2

        //Fetch item data from Firestore
        fetchItemData();

        ImageButton buttonLeftArrow = findViewById(R.id.buttonLeftArrow);
        ImageButton buttonRightArrow = findViewById(R.id.buttonRightArrow);
        TextView textViewDescription = findViewById(R.id.textViewDescription);
        TextView textViewLocation = findViewById(R.id.textViewLocation);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button reserveButton = findViewById(R.id.reserveButton);

        deleteButton.setOnClickListener(v -> deleteItem());
        reserveButton.setOnClickListener(v -> {
            Log.d("MyAdActionsActivity", "Reserve button clicked");
            toggleReservation();
        });
    }

    private void showDatePicker(User selectedUser,String selectedUserId) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, monthOfYear,dayOfMonth);

                    // Validate selected date
                    if (selectedDate.before(Calendar.getInstance())) {
                        // Selected date is in the past
                        Toast.makeText(MyAdActionsActivity.this, "Please select a future date", Toast.LENGTH_SHORT).show();
                    } else {
                        // Valid date, proceed with update
                        currentItem.getReservation().setDueDate(selectedDate.getTime());
                        currentItem.getReservation().setReserved(true);
                        currentItem.getReservation().setReceiverUserId(selectedUserId);

                        Log.d("MyAdActionsActivity", "toggleReservation called"+selectedUser.getUserId()+currentItem.getReservation().getReceiverUserId());
                        // Update Firestore with complete Reservation object
                        db.collection("items").document(itemId)
                                .update("reservation", currentItem.getReservation());
                        // ... (success and failure handlers)

                    // Provide visual feedback
                    Toast.makeText(this, "Reservation made with " + selectedUser.getUsername() + " for " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1, Toast.LENGTH_LONG).show();
                    }
                    // Hide the RecyclerView and dim overlay
                    recyclerView.setVisibility(View.GONE);
                    findViewById(R.id.dimOverlay).setVisibility(View.GONE); },
                year, month, day);
        datePickerDialog.show();
    }
    private void fetchItemData() {
        db.collection("items").document(itemId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentItem = documentSnapshot.toObject(Item.class);
                    if (currentItem != null) {
                        // Set up image slider
                        imageAdapter = new ImageSliderAdapter(this, currentItem.getPhotoUrls());
                        viewPagerImages.setAdapter(imageAdapter);

                        // Set up arrow buttons
                        ImageButton buttonLeftArrow = findViewById(R.id.buttonLeftArrow);
                        ImageButton buttonRightArrow = findViewById(R.id.buttonRightArrow);
                        buttonLeftArrow.setOnClickListener(v -> {
                            int currentItemImage = viewPagerImages.getCurrentItem();
                            if (currentItemImage > 0) {
                                viewPagerImages.setCurrentItem(currentItemImage - 1, true);
                            }
                        });

                        buttonRightArrow.setOnClickListener(v -> {
                            int currentItemImage = viewPagerImages.getCurrentItem();
                            if (currentItemImage < imageAdapter.getItemCount() - 1) {
                                viewPagerImages.setCurrentItem(currentItemImage + 1, true);
                            }
                        });

                        // Set other item details
                        TextView textViewDescription = findViewById(R.id.textViewDescription);
                        TextView textViewLocation = findViewById(R.id.textViewLocation);
                        textViewDescription.setText(currentItem.getDescription());
                        textViewLocation.setText(currentItem.getLocation());

                        Button reserveButton = findViewById(R.id.reserveButton);
                        if (currentItem.getReservation().isReserved()) {
                            reserveButton.setText("Unreserve");
                        }else{
                            reserveButton.setText("Reserve");
                        }
                    }
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
    private void addChatNotificationToReceiver(String receiverId, String itemTitle) { // Add senderId parameter
        db.collection("users").document(receiverId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User receiver = documentSnapshot.toObject(User.class);
                    List<Notification> notifications = receiver.getNotifications();

                    // Create the new notification
                    Notification notification = new Notification("New item reserved " + itemTitle, false, new Date());
                    Collections.sort(notifications, (n1, n2) -> n1.getTimestamp().compareTo(n2.getTimestamp()));
                    // Check if the limit is reached before adding
                    if (notifications.size() >= 10) {
                        notifications.remove(0); // Remove the oldest notification
                    }
                    notifications.add(notification); // Add the new notification

                    // Update the user document with the modified notifications and chatPartners
                    Map<String, Object> updates = new HashMap<>();
                    db.collection("users").document(receiverId).update("notifications", notifications);
                })
                .addOnFailureListener(e -> { /* Handle error */ });
    }

    private void toggleReservation() {
        if(!currentItem.getReservation().isReserved()){
        Log.d("MyAdActionsActivity", "toggleReservation called");
        ConstraintLayout mainContent = findViewById(R.id.mainContent);
        View dimOverlay = findViewById(R.id.dimOverlay);

        if (recyclerView.getVisibility() == View.GONE) {
            // Show the RecyclerView and dim the background
            dimOverlay.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            mainContent.setClickable(false);
            loadChatPartners(); // Load chat partners when showing the RecyclerView
        } else {
            // Hide the RecyclerView and restore the background
            dimOverlay.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            mainContent.setClickable(true);
        }}else{
            currentItem.getReservation().setReserved(false);
            currentItem.getReservation().setReceiverUserId(null);
        }
    }

    private void loadChatPartners() {
        Log.d("MyAdActionsActivity", "loadChatPartners called");
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User currentUser = documentSnapshot.toObject(User.class);
                    if (currentUser != null && currentUser.getChatPartners() != null && !currentUser.getChatPartners().isEmpty()) {
                        List<String> chatPartnerIds = currentUser.getChatPartners();
                        Log.d("MyAdActionsActivity", "Number of chat partner IDs: " + chatPartnerIds.size());

                        List<User> chatPartnerUsers = new ArrayList<>();

                        for (String partnerId : chatPartnerIds) {
                            Log.d("MyAdActionsActivity", "Fetching chat partner with ID: " + partnerId);
                            db.collection("users").document(partnerId).get()
                                    .addOnSuccessListener(partnerDoc -> {
                                        User partnerUser = partnerDoc.toObject(User.class);
                                        if (partnerUser != null) {
                                            chatPartnerUsers.add(partnerUser);
                                            Log.d("MyAdActionsActivity", "Added chat partner: " + partnerUser.getUsername());

                                            if (chatPartnerUsers.size() == chatPartnerIds.size()) {
                                                Log.d("MyAdActionsActivity", "All chat partners loaded. Total: " + chatPartnerUsers.size());
                                                runOnUiThread(() -> {
                                                    chatPartnerAdapter.updateList(chatPartnerUsers);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                });
                                            }
                                        } else {
                                            Log.d("MyAdActionsActivity", "Partner user is null for ID: " + partnerId);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("MyAdActionsActivity", "Error loading chat partner with ID: " + partnerId, e);
                                    });
                        }
                    } else {
                        Log.d("MyAdActionsActivity", "No chat partners found");
                        runOnUiThread(this::showNoChatPartnersMessage);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MyAdActionsActivity", "Error loading current user", e);
                    runOnUiThread(this::showErrorMessage);
                });
    }

    private void showNoChatPartnersMessage() {
        recyclerView.setVisibility(View.GONE);
        TextView messageView = findViewById(R.id.noChatPartnersMessage); // You'll need to add this to your layout
        messageView.setVisibility(View.VISIBLE);
        messageView.setText("No chat partners available.");
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.GONE);
        TextView messageView = findViewById(R.id.noChatPartnersMessage);
        messageView.setVisibility(View.VISIBLE);
        messageView.setText("An error occurred while loading chat partners.");
    }
}
