package com.rassam.atiniapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rassam.atiniapp.models.Item;

import java.util.List;

public class SimilarItemsAdapter extends RecyclerView.Adapter<SimilarItemsAdapter.ViewHolder> {

    private Context context;
    private List<Item> items;
    private OnItemClickListener onItemClickListener;

    public SimilarItemsAdapter(Context context, List<Item> items, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.items = items;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_similar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.textViewTitle.setText(item.getTitle()); // Assuming your Item class has a 'title' property

        // Load image using Glide or Picasso (adjust as needed)
        Glide.with(context)
                .load(item.getPhotoUrls().isEmpty() ? null : item.getPhotoUrls().get(0)) // Assuming your Item class has a list of photo URLs
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemImage); // Adjust ID as needed
            textViewTitle = itemView.findViewById(R.id.itemTitle); // Adjust ID as needed
        }
    }

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(Item item);
    }
}