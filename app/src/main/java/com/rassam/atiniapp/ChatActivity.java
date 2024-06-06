package com.rassam.atiniapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rassam.atiniapp.models.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private EditText editTextMessage;
    private Button buttonSend;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String chatId;
    private String receiverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        chatId = getIntent().getStringExtra("CHAT_ID");
        receiverId = getIntent().getStringExtra("RECEIVER_ID");
        createChatDocument();
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setAdapter(messageAdapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        loadMessages();
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String senderId = currentUser.getUid();
                Date timestamp = new Date(System.currentTimeMillis());
                Message message = new Message(senderId, receiverId, messageText, timestamp);

                CollectionReference messagesRef = db.collection("chats").document(chatId).collection("messages");
                messagesRef.add(message).addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Message sent: " + documentReference.getId());
                    editTextMessage.setText("");
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Error sending message", e);
                });
            }
        }
    }

    private void loadMessages() {
        CollectionReference messagesRef = db.collection("chats").document(chatId).collection("messages");
        messagesRef.orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Listen failed.", error);
                    return;
                }
                messageList.clear();
                if (value != null) {
                    for (QueryDocumentSnapshot doc : value) {
                        Message message = doc.toObject(Message.class);
                        messageList.add(message);
                        Log.d(TAG, "Message loaded: " + message.getText());
                    }
                }
                messageAdapter.notifyDataSetChanged();
                recyclerViewMessages.scrollToPosition(messageList.size() - 1);
            }
        });
    }
    private void createChatDocument() {
        String currentUserId = mAuth.getCurrentUser().getUid();
        List<String> participants = Arrays.asList(currentUserId, receiverId);

        DocumentReference chatRef = db.collection("chats").document(chatId);

        // Check if the chat document exists
        chatRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().exists()) {
                    // Chat document doesn't exist, create it
                    Map<String, Object> chatData = new HashMap<>();
                    chatData.put("participants", participants);

                    chatRef.set(chatData)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Chat document created successfully"))
                            .addOnFailureListener(e -> Log.e(TAG, "Error creating chat document", e));
                } else {
                    Log.d(TAG, "Chat document already exists");
                }
            } else {
                Log.e(TAG, "Error checking if chat document exists", task.getException());
            }
        });
    }
}
