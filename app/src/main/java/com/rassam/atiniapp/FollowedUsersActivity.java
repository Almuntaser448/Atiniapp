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
import com.rassam.atiniapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class FollowedUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FollowedUsersAdapter adapter;
    private List<User> followedUsers;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followed_users);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        followedUsers = new ArrayList<>();
        adapter = new FollowedUsersAdapter(followedUsers);
        recyclerView.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadFollowedUsers();
    }

    private void loadFollowedUsers() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    List<String> followedUserIds = (List<String>) task.getResult().get("followedUsers");
                    if (followedUserIds != null) {
                        for (String followedUserId : followedUserIds) {
                            db.collection("users").document(followedUserId).get().addOnCompleteListener(userTask -> {
                                if (userTask.isSuccessful() && userTask.getResult() != null) {
                                    User user = userTask.getResult().toObject(User.class);
                                    followedUsers.add(user);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(FollowedUsersActivity.this, "Failed to load followed users", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
