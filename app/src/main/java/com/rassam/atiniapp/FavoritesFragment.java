package com.rassam.atiniapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rassam.atiniapp.models.Item;

import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    private List<Item> favoriteItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        User currentUser = DynamoDBHelper.getCurrentUser(); // Implement this method to get the current user
        if (currentUser != null) {
            favoriteItems = currentUser.getFavorites();
            adapter = new FavoritesAdapter(favoriteItems);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }
}
