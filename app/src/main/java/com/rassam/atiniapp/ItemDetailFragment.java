package com.rassam.atiniapp;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.rassam.atiniapp.models.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemDetailFragment extends Fragment {

    private static final String ARG_ITEM_ID = "item_id";
    private String itemId;
    private Item item;

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

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        TextView textViewLocation = view.findViewById(R.id.textViewLocation);
        TextView textViewUserProfile = view.findViewById(R.id.textViewUserProfile);
        Button buttonChat = view.findViewById(R.id.buttonChat);
        Button buttonFavorite = view.findViewById(R.id.buttonFavorite);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (itemId != null) {
            db.collection("items").document(itemId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    item = task.getResult().toObject(Item.class);
                    if (item != null) {
                        Glide.with(this).load(item.getPhotoUrls().get(0)).into(imageView);
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

    private String fetchUserProfile(String userId) {
        // Implement logic to fetch user profile details using userId
        return "User Profile Info"; // Placeholder
    }

    private void openChatWithUser(String userId) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("RECEIVER_ID", userId);
        intent.putExtra("CHAT_ID", createChatId(userId));
        startActivity(intent);
    }

    private String createChatId(String userId) {
        // Generate a unique chat ID based on the user IDs
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return currentUserId.compareTo(userId) < 0 ? currentUserId + "_" + userId : userId + "_" + currentUserId;
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
