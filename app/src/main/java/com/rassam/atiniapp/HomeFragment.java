package com.rassam.atiniapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

        adapter = new HomeAdapter(getActivity(), itemList, item -> openItemDetails(item.getId()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void performSearch() {
        String searchText = editTextSearch.getText().toString().trim();
        String selectedStatus = spinnerStatusFilter.getSelectedItem().toString();
        String selectedCategory = spinnerCategoryFilter.getSelectedItem().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("items");

        // Handle search text
        if (!TextUtils.isEmpty(searchText)) {
            query = query.whereArrayContains("keywords", searchText.toLowerCase());
        }

        // Handle status filter
        if (!selectedStatus.equals("All")) {
            query = query.whereEqualTo("status", selectedStatus);
        }

        // Handle category filter
        if (!selectedCategory.equals("Category 0")) {
            query = query.whereEqualTo("categoryNumber", selectedCategory.substring(10)); // Extract number from "Category X"
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                itemList.clear();
                itemList.addAll(task.getResult().toObjects(Item.class));
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void openItemDetails(String itemId) {
        ItemDetailFragment fragment = ItemDetailFragment.newInstance(itemId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
