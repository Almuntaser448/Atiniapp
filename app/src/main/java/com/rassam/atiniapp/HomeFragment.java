package com.rassam.atiniapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.rassam.atiniapp.models.Item;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private List<Item> itemList;

    private EditText editTextSearch, editTextLocation;
    private Spinner spinnerStatusFilter, spinnerCategoryFilter;
    private Button buttonSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        editTextSearch = view.findViewById(R.id.editTextSearch);
        editTextLocation = view.findViewById(R.id.editTextLocation);
        spinnerStatusFilter = view.findViewById(R.id.spinnerStatus);
        spinnerCategoryFilter = view.findViewById(R.id.spinnerCategoryFilter);
        buttonSearch = view.findViewById(R.id.buttonSearch);

        buttonSearch.setOnClickListener(v -> performSearch());

        itemList = new ArrayList<>();
        adapter = new HomeAdapter(itemList, item -> {
            // Handle item click if needed
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void performSearch() {
        String searchText = editTextSearch.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String status = spinnerStatusFilter.getSelectedItem().toString();
        int category = spinnerCategoryFilter.getSelectedItemPosition() + 1;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("items");

        if (!TextUtils.isEmpty(searchText)) {
            query = query.whereArrayContains("keywords", searchText.toLowerCase());
        }
        if (!TextUtils.isEmpty(location)) {
            query = query.whereEqualTo("location", location);
        }
        if (!status.equals("All")) {
            query = query.whereEqualTo("status", status);
        }
        if (category != 0) {
            query = query.whereEqualTo("categoryNumber", category);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                itemList.clear();
                itemList.addAll(task.getResult().toObjects(Item.class));
                adapter.notifyDataSetChanged();
            }
        });
    }
}
