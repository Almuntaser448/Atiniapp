package com.rassam.atiniapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rassam.atiniapp.models.Item;
import com.rassam.atiniapp.models.User;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProfileManagementFragment ProfileManagementFragment;
    private FirebaseUser currentUser ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();


        if (currentUser == null) {
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
            return;
        }
        String userId = currentUser.getUid();

        fetchCurrentUserAndFavorites(userId);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_profile) {
                    if (ProfileManagementFragment == null) {
                        ProfileManagementFragment = new ProfileManagementFragment();
                    }
                    selectedFragment = ProfileManagementFragment;
                }  else if (itemId == R.id.nav_main_page) {
                    selectedFragment = new MainPageFragment();
                } else if (itemId == R.id.nav_chats) {
                    selectedFragment = new ChatsFragment();
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            }
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

    }

    private void fetchCurrentUserAndFavorites(String userId) {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    User currentUser = document.toObject(User.class);
                    if (currentUser != null) {
                        List<String> favoriteItems = currentUser.getFavorites();
                        // You can now use favoriteItems to populate your RecyclerView or any other UI component
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No user data found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
