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

import com.rassam.atiniapp.models.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGES_REQUEST = 1;

    private EditText editTextTitle, editTextCategory, editTextDescription;
    private LinearLayout linearLayoutImages;
    private List<Uri> imageUris;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextCategory = view.findViewById(R.id.editTextCategory);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        linearLayoutImages = view.findViewById(R.id.linearLayoutImages);
        Button buttonChooseImages = view.findViewById(R.id.buttonChooseImages);
        Button buttonUpload = view.findViewById(R.id.buttonUpload);

        imageUris = new ArrayList<>();

        buttonChooseImages.setOnClickListener(v -> openImageChooser());
        buttonUpload.setOnClickListener(v -> uploadAd());

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
        linearLayoutImages.addView(imageView        );
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
            S3Helper.uploadFile(getActivity(), uri, "YOUR_S3_BUCKET_NAME", key);
            photoUrls.add("https://YOUR_S3_BUCKET_NAME.s3.amazonaws.com/" + key);
        }

        Item newItem = new Item(UUID.randomUUID().toString(), title, category, description, photoUrls);
        // Add the item to the user's favorites or the general items list
        // This example adds it to the user's favorites
        User currentUser = DynamoDBHelper.getCurrentUser(); // You need to implement this method to get the current user
        if (currentUser != null) {
            currentUser.getFavorites().add(newItem);
            DynamoDBHelper.saveUser(currentUser);
        } else {
            DynamoDBHelper.saveItem(newItem);
        }

        Toast.makeText(getActivity(), "Ad uploaded successfully", Toast.LENGTH_SHORT).show();
    }
}

