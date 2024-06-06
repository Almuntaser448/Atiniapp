package com.rassam.atiniapp;

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

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private List<User> chatUsersList;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(String userId);
    }

    public ChatsAdapter(List<User> chatUsersList, OnItemClickListener clickListener) {
        this.chatUsersList = chatUsersList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
 User user = chatUsersList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(user.getProfilePhotoUrl())
                .into(holder.imageViewProfile);

        holder.textViewName.setText(user.getUsername());
        holder.itemView.setOnClickListener(v -> clickListener.onItemClick(user.getUserId())); // Pass userId
    }

    @Override
    public int getItemCount() {
        return chatUsersList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfile;
        TextView textViewName;

        ViewHolder(View itemView) {
            super(itemView);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            textViewName = itemView.findViewById(R.id.textViewName);
        }
    }
}
