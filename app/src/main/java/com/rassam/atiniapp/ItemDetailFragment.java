package com.rassam.atiniapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.rassam.atiniapp.SimilarItemsAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rassam.atiniapp.models.Chat;
import com.rassam.atiniapp.models.Item;
import com.rassam.atiniapp.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ItemDetailFragment extends Fragment {
    private RecyclerView recyclerViewSimilarItems;
    private static final String ARG_ITEM_ID = "item_id";
    private String itemId;
    private Item item;
    private ViewPager2 viewPagerImages;
    private ImageSliderAdapter imageAdapter;
    private List<Item> similarItemList;
    private SimilarItemsAdapter similarItemsAdapter;
    public static ItemDetailFragment newInstance(String itemId) {
        ItemDetailFragment fragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemId = getArguments().getString(ARG_ITEM_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_detail, container, false);

        viewPagerImages = view.findViewById(R.id.viewPagerImages);
        ImageButton buttonLeftArrow = view.findViewById(R.id.buttonLeftArrow);
        ImageButton buttonRightArrow = view.findViewById(R.id.buttonRightArrow);
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        TextView textViewLocation = view.findViewById(R.id.textViewLocation);
        TextView textViewUserProfile = view.findViewById(R.id.textViewUserProfile);
        Button buttonChat = view.findViewById(R.id.buttonChat);
        Button buttonFavorite = view.findViewById(R.id.buttonFavorite);

        recyclerViewSimilarItems = view.findViewById(R.id.recyclerViewSimilarItems); // Add this line
        recyclerViewSimilarItems.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        similarItemList = new ArrayList<>();
        similarItemsAdapter = new SimilarItemsAdapter(getContext(), similarItemList, item -> {
            // Handle click on similar item (e.g., open its details)
        });
        recyclerViewSimilarItems.setAdapter(similarItemsAdapter);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (itemId != null) {
            db.collection("items").document(itemId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    item = task.getResult().toObject(Item.class);
                    if (item != null) {
                        // Set up image slider
                        imageAdapter = new ImageSliderAdapter(getContext(), item.getPhotoUrls());
                        viewPagerImages.setAdapter(imageAdapter);

                        // Set up arrow buttons
                        buttonLeftArrow.setOnClickListener(v -> {
                            int currentItem = viewPagerImages.getCurrentItem();
                            if (currentItem > 0) {
                                viewPagerImages.setCurrentItem(currentItem - 1, true);
                            }
                        });

                        buttonRightArrow.setOnClickListener(v -> {
                            int currentItem = viewPagerImages.getCurrentItem();
                            if (currentItem < imageAdapter.getItemCount() - 1) {
                                viewPagerImages.setCurrentItem(currentItem + 1, true);
                            }
                        });
                        fetchSimilarItems(item);
                        // Set other item details
                        textViewDescription.setText(item.getDescription());
                        textViewLocation.setText(item.getLocation());
                        textViewUserProfile.setText(fetchUserProfile(item.getUserId()));
                    }
                }
            });
        }

        buttonChat.setOnClickListener(v -> openChatWithUser(item.getUserId()));
        buttonFavorite.setOnClickListener(v -> toggleFavorite(item));

        return view;
    }
    private void fetchSimilarItems(Item currentItem) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Query> queries = new ArrayList<>();

        // Query for items matching both title, description and category (string)
      Query queryAll = db.collection("items")
            .whereEqualTo("category", currentItem.getCategory())
                .whereEqualTo("status", currentItem.getStatus())
                .limit(3);
        queries.add(queryAll);

        // Query for items matching title and category (string)
        Query queryTitleCategory = db.collection("items")
                .whereEqualTo("category", currentItem.getCategory())
                .limit(3);
        queries.add(queryTitleCategory);

        // Query for items matching description and category (string)
        Query queryDescriptionCategory= db.collection("items")
                .whereEqualTo("category", currentItem.getCategory())
                .limit(3);
        queries.add(queryDescriptionCategory);

        // Query for items matching just category (string)
        Query queryCategory = db.collection("items")
                .whereEqualTo("category", currentItem.getCategory())
                .limit(3);
        queries.add(queryCategory);

        // Execute queries sequentially
        executeQueriesSequentially(queries, 0);
    }

    private void executeQueriesSequentially(List<Query> queries, int index) {if (index >= queries.size() || similarItemList.size() >= 3) {
        // Stop if we've executed all queries or found 3 similar items
        similarItemsAdapter.notifyDataSetChanged();
        return;
    }

        queries.get(index).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (!document.getId().equals(itemId)) { // Exclude the current item
                        Item similarItem = document.toObject(Item.class);
                        similarItemList.add(similarItem);
                        if (similarItemList.size() >= 3) {
                            break; // Stop if we've found 3 similar items
                        }
                    }
                }
                // Move to the next query AFTER processing all results from the current query
                executeQueriesSequentially(queries, index + 1);
            }
        });
    }
    private String fetchUserProfile(String userId) {
        // Implement logic to fetch user profile details using userId
        return "User Profile Info"; // Placeholder
    }

    private void openChatWithUser(String userId) {
        String chatId = createChatId(userId);
        createOrGetChat(chatId, userId, chat -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("RECEIVER_ID", userId);
            intent.putExtra("CHAT_ID", chatId);
            startActivity(intent);
        });
    }

    private String createChatId(String userId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return currentUserId.compareTo(userId) < 0 ? currentUserId + "_" + userId : userId + "_" + currentUserId;
    }

    private void createOrGetChat(String chatId, String otherUserId, OnChatCreatedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("chats").document(chatId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Chat already exists
                            Chat existingChat = document.toObject(Chat.class);
                            listener.onChatCreated(existingChat);
                        } else {
                            // Create new chat
                            List<String> participants = Arrays.asList(currentUserId, otherUserId);
                            Chat newChat = new Chat(chatId, "", "", new Date(), participants);

                            db.collection("chats").document(chatId).set(newChat)
                                    .addOnSuccessListener(aVoid -> {listener.onChatCreated(newChat);
                                        updateChatPartners(currentUserId, otherUserId);
                                        updateChatPartners(otherUserId, currentUserId);})
                                    .addOnFailureListener(e -> {
                                        // Handle the error
                                        Log.e("CreateChat", "Error creating chat", e);
                                    });
                        }
                    } else {
                        // Handle the error
                        Log.e("CreateChat", "Error checking for existing chat", task.getException());
                    }
                });
    }

    interface OnChatCreatedListener {
        void onChatCreated(Chat chat);
    }
    private void updateChatPartners(String userId, String newChatPartnerId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        List<String> chatPartners = user.getChatPartners();
                        if (chatPartners == null) {
                            chatPartners = new ArrayList<>();
                        }
                        if (!chatPartners.contains(newChatPartnerId)) {
                            chatPartners.add(newChatPartnerId);
                            user.setChatPartners(chatPartners);
                            db.collection("users").document(userId).set(user); // Update user document
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    Log.e("UpdateChatPartners", "Error updating chat partners for user " + userId, e);
                });
    }
    private void toggleFavorite(Item item) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();

        db.collection("users").document(currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> favoriteItems = (List<String>) task.getResult().get("favorites");
                if (favoriteItems == null) {
                    favoriteItems = new ArrayList<>();
                }
                if (favoriteItems.contains(item.getId())) {
                    favoriteItems.remove(item.getId());
                    Toast.makeText(getActivity(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    favoriteItems.add(item.getId());
                    Toast.makeText(getActivity(), "Added to favorites", Toast.LENGTH_SHORT).show();
                }
                db.collection("users").document(currentUserId).update("favorites", favoriteItems);
            }
        });
    }
}
