package com.rassam.atiniapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rassam.atiniapp.models.Item;
import com.rassam.atiniapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private List<Item> itemList;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Sample data
        itemList = new ArrayList<>();
        itemList.add(new Item("1", "Title 1", "Description 1"));
        itemList.add(new Item("2", "Title 2", "Description 2"));
        itemList.add(new Item("3", "Title 3", "Description 3"));

        adapter = new HomeAdapter(itemList, this::addToFavorites);
        recyclerView.setAdapter(adapter);

        // Fetch current user (this should be replaced with actual user fetching logic)
        currentUser = new User();
        currentUser.setUserId("user1");
        currentUser.setName("John Doe");
        currentUser.setEmail("john.doe@example.com");
        currentUser.setFavorites(new ArrayList<>());

        return view;
    }

    private void addToFavorites(Item item) {
        currentUser.getFavorites().add(item);
        DynamoDBHelper.saveUser(currentUser);
        Toast.makeText(getActivity(), item.getTitle() + " added to favorites", Toast.LENGTH_SHORT).show();
    }
}
