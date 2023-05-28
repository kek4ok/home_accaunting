package com.example.home_accaunting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CategoriesExpFragment extends Fragment {
    private RecyclerView expenseList;
    private CategoriesAdapter itemAdapter;
    private List<Category> items;
    private ApiClient apiClient;
    private SwipeRefreshLayout swipeContainer;
    private int position = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        apiClient = new ApiClient(getActivity());

        setupSwipeRefresh(view);
        setupRecyclerView(view);

        fetchCategoriesAsync();

        return view;
    }

    private void setupSwipeRefresh(View view) {
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this::fetchCategoriesAsync);
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }

    private void setupRecyclerView(View view) {
        items = new ArrayList<>();
        itemAdapter = new CategoriesAdapter(items);
        expenseList = view.findViewById(R.id.expense_list);
        expenseList.setLayoutManager(new LinearLayoutManager(getActivity()));
        expenseList.setAdapter(itemAdapter);
    }

    public void fetchCategoriesAsync() {
        items.clear();
        itemAdapter.notifyDataSetChanged();
        apiClient.getCategories(this::handleTransactionsResponse, position);
        swipeContainer.setRefreshing(false);
    }

    private void handleTransactionsResponse(String[] categories){
        System.out.println(Arrays.toString(categories));
        for (int i = 0; i < categories.length; i++) {
            String name = categories[i];
            int id = i;
            items.add(new Category(id, name));
        }
        updateUI();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void updateUI() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(() -> itemAdapter.notifyDataSetChanged());
        }
    }
}

