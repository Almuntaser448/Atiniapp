package com.rassam.atiniapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rassam.atiniapp.models.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatsFragment extends Fragment {

    private static final String TAG = "ChatsFragment";

    private RecyclerView recyclerViewChats;
    private ChatListAdapter chatListAdapter;
    private List<User> chatUsersList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerViewChats = view.findViewById(R.id.recyclerViewChats);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(getActivity()));

        chatUsersList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chatUsersList, user -> openChatWithUser(user));
        recyclerViewChats.setAdapter(chatListAdapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadChatUsers();

        return view;
    }

    private void loadChatUsers() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        db.collection("chats")
                .whereArrayContains("participants", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Chat documents fetched: " + task.getResult().size()); //
                        Set<String> userIds = new HashSet<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<String> participants = (List<String>) document.get("participants");
                            for (String participant : participants) {
                                if (!participant.equals(currentUserId)) {
                                    userIds.add(participant);
                                }
                            }
                        }
                        if (!userIds.isEmpty()) {
                            loadUserDetails(new ArrayList<>(userIds));
                            Log.d(TAG, "User IDs to load: " + userIds);
                        } else {
                            Log.d(TAG, "No chat users found.");
                        }
                    } else {
                        Log.d(TAG, "Error getting chat documents: ", task.getException());
                    }
                });
    }

    private void loadUserDetails(List<String> userIds) {
        if (userIds.isEmpty()) {
            Log.d(TAG, "User IDs list is empty, no need to query Firestore.");
            return;
        }

        List<DocumentReference> userRefs = new ArrayList<>();
        for (String userId : userIds) {
            userRefs.add(db.collection("users").document(userId));
        }

        Tasks.whenAllSuccess(userRefs.stream()
                        .map(DocumentReference::get)
                        .collect(Collectors.toList()))
                .addOnSuccessListener(list -> {
                    chatUsersList.clear();
                    for (Object obj : list) {
                        if (obj instanceof DocumentSnapshot) {
                            DocumentSnapshot document = (DocumentSnapshot) obj;
                            if (document.exists()) {
                               User user = document.toObject(User.class);
                                chatUsersList.add(user);
                            }
                        }
                    }
                    chatListAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.d(TAG, "Error getting user documents: ", e));
    }

    private void openChatWithUser(String userId) {
        Log.d(TAG, "Received User ID: " + userId);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("RECEIVER_ID", userId);
        intent.putExtra("CHAT_ID", createChatId(userId));
        startActivity(intent);
    }

    private String createChatId(String userId) {
        // Generate a unique chat ID based on the user IDs
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Log the IDs for debugging
        Log.d(TAG, "Current User ID: " + currentUserId);
        Log.d(TAG, "Other User ID: " + userId);

        // Handle potential null values
        if (currentUserId == null || userId == null) {
            Log.e(TAG, "Error: One or both user IDs are null.");
            return "defaultChatId"; // Replace with appropriate error handling
        }

        return currentUserId.compareTo(userId) < 0 ? currentUserId + "_" + userId : userId + "_" + currentUserId;
    }
}
