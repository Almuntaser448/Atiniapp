package com.rassam.atiniapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rassam.atiniapp.models.Rating;

import java.util.List;

public class MyReviewsAdapter extends RecyclerView.Adapter<MyReviewsAdapter.ReviewViewHolder> {

    private List<Rating> reviewList;

    public MyReviewsAdapter(List<Rating> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Rating rating = reviewList.get(position);
        holder.ratingBar.setRating(rating.getRating());
        holder.textViewComment.setText(rating.getComment());
        // Assuming you have methods to get the user's name or other details
        holder.textViewUser.setText(rating.getRatingUserId());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        RatingBar ratingBar;
        TextView textViewComment, textViewUser;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            textViewUser = itemView.findViewById(R.id.textViewUser);
        }
    }
}
