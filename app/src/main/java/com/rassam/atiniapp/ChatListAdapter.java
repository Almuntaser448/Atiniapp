package com.rassam.atiniapp;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rassam.atiniapp.models.User;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<User> userList;
    private OnUserClickListener onUserClickListener;

    public ChatListAdapter(List<User> userList, OnUserClickListener onUserClickListener) {
        this.userList = userList;
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);
        return new ChatViewHolder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
       User user = userList.get(position);
        Log.d(TAG, "Item count: " + userList.size());

        // Handle null username
        String username = user.getUsername();
        if (username != null) {
            holder.textViewUsername.setText(username);
        } else {
            // Display a placeholder or default text
            holder.textViewUsername.setText("Unknown User");
        }

        Glide.with(holder.itemView.getContext()).load(user.getProfilePhotoUrl()).into(holder.imageViewProfile);
        holder.itemView.setOnClickListener(v -> onUserClickListener.onUserClick(user.getUserId()));
    }
    @Override
    public int getItemCount() {
        Log.d(TAG, "Item count: " + userList.size());
        return userList.size();
    }

    public interface OnUserClickListener {
        void onUserClick(String userId);
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewProfile;
        TextView textViewUsername;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            textViewUsername = itemView.findViewById(R.id.textViewName);
        }
    }
}
