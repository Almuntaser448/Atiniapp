package com.rassam.atiniapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class SettingsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextEmail, editTextPassword,editTextUsername;
    private ImageView imageViewProfile;
    private Button buttonUpdate, buttonChangePhoto;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseStorage firebaseStorage;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        editTextUsername= findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonChangePhoto = findViewById(R.id.buttonChangePhoto);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        loadUserProfile();

        buttonUpdate.setOnClickListener(v -> updateProfile());
        buttonChangePhoto.setOnClickListener(v -> openImageChooser());
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            editTextEmail.setText(currentUser.getEmail());
            db.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String photoUrl = task.getResult().getString("profilePhotoUrl");
                    Glide.with(this).load(photoUrl).into(imageViewProfile);
                }
            });
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewProfile.setImageURI(imageUri);
        }
    }

    private void updateProfile() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String newEmail = editTextEmail.getText().toString().trim();
            String newPassword = editTextPassword.getText().toString().trim();

            if (!TextUtils.isEmpty(newEmail)) {
                currentUser.updateEmail(newEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.collection("users").document(currentUser.getUid()).update("email", newEmail);
                        Toast.makeText(SettingsActivity.this, "Email updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (!TextUtils.isEmpty(newPassword)) {
                currentUser.updatePassword(newPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SettingsActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            String newUsername = editTextUsername.getText().toString().trim();
            if (!TextUtils.isEmpty(newUsername)) {
                db.collection("users").document(currentUser.getUid())
                        .update("username", newUsername) // Update the "name" field in Firestore
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, "Username updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingsActivity.this, "Failed to update username", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            if (imageUri != null) {
                uploadImageToFirestore(currentUser.getUid());
            }
        }
    }

    private void uploadImageToFirestore(String userId) {
        StorageReference storageRef = firebaseStorage.getReference().child("profile_images/" + UUID.randomUUID().toString());
        storageRef.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                storageRef.getDownloadUrl().addOnCompleteListener(urlTask -> {
                    if (urlTask.isSuccessful()) {
                        String downloadUrl = urlTask.getResult().toString();
                        db.collection("users").document(userId).update("profilePhotoUrl", downloadUrl).addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, "Profile photo updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingsActivity.this, "Failed to update profile photo", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(SettingsActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


