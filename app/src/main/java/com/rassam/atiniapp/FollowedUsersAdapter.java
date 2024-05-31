package com.rassam.atiniapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rassam.atiniapp.models.User;

import java.util.List;

public class FollowedUsersAdapter extends RecyclerView.Adapter<FollowedUsersAdapter.FollowedUserViewHolder> {

    private List<User> followedUsers;

    public FollowedUsersAdapter(List<User> followedUsers) {
        this.followedUsers = followedUsers;
    }

    @NonNull
    @Override
    public FollowedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_followed_user, parent, false);
        return new FollowedUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowedUserViewHolder holder, int position) {
        User user = followedUsers.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return followedUsers.size();
    }

    static class FollowedUserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;

        FollowedUserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
        }

        void bind(User user) {
            textViewUsername.setText(user.getUsername());
        }
    }
}
