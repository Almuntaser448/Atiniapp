package com.rassam.atiniapp;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rassam.atiniapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class ChatPartnerAdapter extends RecyclerView.Adapter<ChatPartnerAdapter.ChatPartnerViewHolder> {

    private List<User> chatPartners = new ArrayList<>();
    private OnChatPartnerClickListener listener; // Add a listener interface

    // Interface for click events
    public interface OnChatPartnerClickListener {
        void onChatPartnerClick(User user);
    }

    // Constructor to set the listener
    public ChatPartnerAdapter(OnChatPartnerClickListener listener) {
        this.listener = listener;
        this.chatPartners = new ArrayList<>(); // Initialize the list
    }

    @NonNull
    @Override
    public ChatPartnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_partner_item, parent, false); // Use your item layout
        return new ChatPartnerViewHolder(itemView);
    }

    // ViewHolder for individual chat partner items
    public class ChatPartnerViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView profileImageView;

        public ChatPartnerViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }

        public void bind(User user, OnChatPartnerClickListener listener) {
            nameTextView.setText(user.getUsername());

            // Load profile image if available
            if (user.getProfilePhotoUrl() != null && !user.getProfilePhotoUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(user.getProfilePhotoUrl())
                        .placeholder(R.drawable.ic_user_placeholder)
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.ic_user_placeholder);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChatPartnerClick(user);
                }
            });
        }
    }

    // ... (rest of the adapter code remains the same)


    @Override
    public void onBindViewHolder(@NonNull ChatPartnerViewHolder holder, int position) {
        User user = chatPartners.get(position);
        Log.d("ChatPartnerAdapter", "Binding user at position " + position + ": " + user.getUsername());
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        Log.d("ChatPartnerAdapter", "getItemCount called, returning " + chatPartners.size());
        return chatPartners.size();
    }

    // Method to update the list of chat partners
    public void updateList(List<User> newChatPartners) {
        Log.d("ChatPartnerAdapter", "Updating list with " + newChatPartners.size() + " partners");
        this.chatPartners = new ArrayList<>(newChatPartners);
        notifyDataSetChanged();
    }
}