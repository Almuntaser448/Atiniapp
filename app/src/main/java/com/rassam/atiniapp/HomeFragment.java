package com.rassam.atiniapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.rassam.atiniapp.models.User;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private List<HomeItem> homeItemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        homeItemList = new ArrayList<>();
        homeItemList.add(new HomeItem("Item 1", "Description 1"));
        homeItemList.add(new HomeItem("Item 2", "Description 2"));
        homeItemList.add(new HomeItem("Item 3", "Description 3"));

        homeAdapter = new HomeAdapter(homeItemList);
        recyclerView.setAdapter(homeAdapter);

        saveUserData();

        return view;
    }

    private void saveUserData() {
        User user = new User();
        user.setUserId("123");
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        DynamoDBHelper.saveUser(user);
    }
}
