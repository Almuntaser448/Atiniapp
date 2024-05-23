package com.rassam.atiniapp;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class AWSProvider extends Application {

    private static FirebaseFirestore firestore;
    private static FirebaseStorage storage;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public static FirebaseFirestore getFirestore() {
        return firestore;
    }

    public static FirebaseStorage getStorage() {
        return storage;
    }
}
