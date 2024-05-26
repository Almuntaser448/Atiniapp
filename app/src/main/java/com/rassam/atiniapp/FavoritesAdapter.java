package com.rassam.atiniapp;

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

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> {

    private List<Item> favoriteItems;

    public FavoritesAdapter(List<Item> favoriteItems) {
        this.favoriteItems = favoriteItems;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {
        Item item = favoriteItems.get(position);
        if (item != null) {
            holder.textViewTitle.setText(item.getTitle());
            holder.textViewCategory.setText(item.getCategory());
            holder.textViewDescription.setText(item.getDescription());

            List<String> photoUrls = item.getPhotoUrls();
            if (photoUrls != null && !photoUrls.isEmpty()) {
                Glide.with(holder.itemView.getContext()).load(photoUrls.get(0)).into(holder.imageView);
            } else {
                // Handle case where no photo URL is available
                holder.imageView.setImageDrawable(null);
            }
        }
    }


    @Override
    public int getItemCount() {
        return favoriteItems.size();
    }

    static class FavoritesViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewCategory;
        TextView textViewDescription;
        ImageView imageView;

        FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
