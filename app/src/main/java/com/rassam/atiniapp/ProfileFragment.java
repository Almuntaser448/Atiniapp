package com.rassam.atiniapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rassam.atiniapp.models.Item;
import com.rassam.atiniapp.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGES_REQUEST = 1;

    private EditText editTextTitle, editTextCategory, editTextDescription;
    private LinearLayout linearLayoutImages;
    private List<Uri> imageUris;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextCategory = view.findViewById(R.id.editTextCategory);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        linearLayoutImages = view.findViewById(R.id.linearLayoutImages);
        Button buttonChooseImages = view.findViewById(R.id.buttonChooseImages);
        Button buttonUpload = view.findViewById(R.id.buttonUpload);

        imageUris = new ArrayList<>();

        buttonChooseImages.setOnClickListener(v -> openImageChooser());
        buttonUpload.setOnClickListener(v -> uploadAd());

        fetchCurrentUser();

        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                    addImageView(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
                addImageView(imageUri);
            }
        }
    }

    private void addImageView(Uri imageUri) {
        ImageView imageView = new ImageView(getActivity());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        imageView.setImageURI(imageUri);
        linearLayoutImages.addView(imageView);
    }

    private void fetchCurrentUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        currentUser = document.toObject(User.class);
                    } else {
                        // Handle case where the user document doesn't exist
                        currentUser = new User(userId, user.getDisplayName(), user.getEmail());
                        db.collection("users").document(userId).set(currentUser);
                    }
                } else {
                    // Handle errors
                    Toast.makeText(getActivity(), "Failed to fetch user", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle user not logged in
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadAd() {
        String title = editTextTitle.getText().toString().trim();
        String category = editTextCategory.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (title.isEmpty() || category.isEmpty() || description.isEmpty() || imageUris.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields and choose images", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> photoUrls = new ArrayList<>();
        for (Uri uri : imageUris) {
            String key = "images/" + UUID.randomUUID().toString();
            StorageReference storageRef = storage.getReference().child(key);
            UploadTask uploadTask = storageRef.putFile(uri);

            uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                photoUrls.add(uri1.toString());
                if (photoUrls.size() == imageUris.size()) {
                    saveItemToFirestore(title, category, description, photoUrls);
                }
            })).addOnFailureListener(e -> {
                Toast.makeText(getActivity(), "Failed to upload image: " + uri.toString(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void saveItemToFirestore(String title, String category, String description, List<String> photoUrls) {
        Item newItem = new Item(UUID.randomUUID().toString(), title, category, description, photoUrls);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User currentUser = document.toObject(User.class);
                        if (currentUser != null) {
                            currentUser.getFavorites().add(newItem);
                            db.collection("users").document(userId).set(currentUser)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Ad uploaded successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error uploading ad", Toast.LENGTH_SHORT).show());
                        }
                    }
                }
            });
        }
    }
}
