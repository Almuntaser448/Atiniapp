package com.rassam.atiniapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.rassam.atiniapp.models.User;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText,nameEditText,passwordConEdittext;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        nameEditText = findViewById(R.id.editTextName);

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextCreatePassword);
        passwordConEdittext=findViewById(R.id.editTextConfirmPassword);



        findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordConf = passwordConEdittext.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!passwordConf.equals(password)) {
            Toast.makeText(this, "Your passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            User newUser = new User(user.getUid(), name, email);
                                            // Add other user data as needed

                                            db.collection("users").document(user.getUid())
                                                    .set(newUser, SetOptions.merge())
                                                    .addOnSuccessListener(aVoid -> {
                                                        Log.d("Firestore", "User document created");
                                                        startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
                                                        finish();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.w("Firestore", "Error creating user document", e);
                                                        Toast.makeText(AuthenticationActivity.this, "Failed to create user data", Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            Toast.makeText(AuthenticationActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(AuthenticationActivity.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}