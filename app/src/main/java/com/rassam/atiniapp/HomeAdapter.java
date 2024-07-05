package com.rassam.atiniapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rassam.atiniapp.models.Item;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private List<Item> itemList;
    private FragmentActivity activity;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public HomeAdapter(FragmentActivity activity, List<Item> itemList, OnItemClickListener clickListener) {
        this.activity = activity;
        this.itemList = itemList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);

        // Check if the item has image URLs before loading
        if (item.getPhotoUrls() !=null && !item.getPhotoUrls().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getPhotoUrls().get(0))
                    .into(holder.imageView);
        } else {
            // Handle the case where there are no image URLs (e.g., set a placeholder image)
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.placeholder_image) // Replace with your placeholder image resource
                    .into(holder.imageView);
        }

        holder.textViewTitle.setText(item.getTitle());
        holder.textViewDescription.setText(item.getDescription());
        holder.itemView.setOnClickListener(v -> {
            clickListener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle,textViewDescription;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemImage);
            textViewTitle = itemView.findViewById(R.id.itemTitle);
            textViewDescription=itemView.findViewById(R.id.textViewDescription);
        }
    }
}
