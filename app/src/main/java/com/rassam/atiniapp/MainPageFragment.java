package com.rassam.atiniapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rassam.atiniapp.models.Item;
import com.rassam.atiniapp.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainPageFragment extends Fragment {

    private static final int PICK_IMAGES_REQUEST = 1;

    private EditText editTextTitle, editTextDescription;
    private Spinner spinnerCategory, spinnerStatus, editTextLocation;
    private LinearLayout linearLayoutImages;
    private List<Uri> imageUris;
    private AdUploadListener adUploadListener;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextLocation = view.findViewById(R.id.spinneLocationCreateAd);
        spinnerCategory = view.findViewById(R.id.spinnerCategoryCreateAd);
        spinnerStatus = view.findViewById(R.id.spinnerStatusCreateAd);
        linearLayoutImages = view.findViewById(R.id.linearLayoutImages);
        Button buttonChooseImages = view.findViewById(R.id.buttonChooseImages);
        Button buttonUpload = view.findViewById(R.id.buttonUpload);

        imageUris = new ArrayList<>();

        buttonChooseImages.setOnClickListener(v -> openImageChooser());
        buttonUpload.setOnClickListener(v -> uploadAd());

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.category_options_create_ad, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        ArrayAdapter<CharSequence>locationAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.location_options_create_ad, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTextLocation.setAdapter(locationAdapter);
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.status_options_create_ad, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

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
        linearLayoutImages.addView(imageView);
        Glide.with(this)
                .load(imageUri)
                .override(200, 200)  // Resize to 200x200 pixels
                .centerCrop()
                .into(imageView);
    }
    public void setAdUploadListener(AdUploadListener listener) {
        this.adUploadListener = listener;
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
                        currentUser = new User(userId, user.getDisplayName(), user.getEmail());
                        db.collection("users").document(userId).set(currentUser);
                    }
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch user", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadAd() {
        String title = editTextTitle.getText().toString().trim();
        String username = currentUser.getUsername();
        String description = editTextDescription.getText().toString().trim();
        String location = editTextLocation.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();
        String category = spinnerCategory.getSelectedItem().toString();

        if (title.isEmpty() || description.isEmpty() || imageUris.isEmpty() || location.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields and choose images", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> keywords = generateKeywords(title + " " + description);
        List<String> photoUrls = new ArrayList<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            for (Uri uri : imageUris) {
                String key = "user/" + userId + "/images/" + UUID.randomUUID().toString();
                StorageReference imageRef = storageRef.child(key);

                imageRef.putFile(uri)
                        .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                                .addOnSuccessListener(uri1 -> {
                                    photoUrls.add(uri1.toString());
                                    if (photoUrls.size() == imageUris.size()) {
                                        saveItemToFirestore(db, userId,username, title, description, category, status, keywords, location, photoUrls);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getActivity(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                }))
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }

    private void saveItemToFirestore(FirebaseFirestore db, String userId, String username, String title,
                                     String description, String category, String status,
                                     List<String> keywords, String location, List<String> photoUrls) {
        Item newItem = new Item(UUID.randomUUID().toString(), userId, username, title, description,
                location, category, status, photoUrls, false,keywords);
        db.collection("items").document(newItem.getId()).set(newItem)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Ad uploaded successfully", Toast.LENGTH_SHORT).show();
                    // Navigate to the home page
                    if (adUploadListener != null) {
                        adUploadListener.onAdUploadSuccess();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error uploading ad", Toast.LENGTH_SHORT).show());
    }


    private List<String> generateKeywords(String input) {
        List<String> keywords = new ArrayList<>();
        String[] words = input.split("\\s+");
        for (String word : words) {
            String lowercaseWord = word.toLowerCase(); // Convert to lowercase here
            if (!keywords.contains(lowercaseWord)) {
                keywords.add(lowercaseWord);
            }
        }
        return keywords;
    }
}
