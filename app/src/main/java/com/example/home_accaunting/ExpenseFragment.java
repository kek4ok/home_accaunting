package com.example.home_accaunting;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;


public class ExpenseFragment extends Fragment {

    private RecyclerView expenseList;
    private ItemAdapter itemAdapter;
    private List<Item> items;
    private ApiClient apiClient;
    private SwipeRefreshLayout swipeContainer;
    private SharedPrefManager prefManager;
    private int position = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        apiClient = new ApiClient(getActivity());
        prefManager = SharedPrefManager.getInstance(getActivity());

        setupSwipeRefresh(view);
        setupRecyclerView(view);

        fetchTransactionsAsync();

        return view;
    }

    private void setupSwipeRefresh(View view) {
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this::fetchTransactionsAsync);
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }

    private void setupRecyclerView(View view) {
        items = new ArrayList<>();
        itemAdapter = new ItemAdapter(items);
        expenseList = view.findViewById(R.id.expense_list);
        expenseList.setLayoutManager(new LinearLayoutManager(getActivity()));
        expenseList.setAdapter(itemAdapter);
    }

    public void fetchTransactionsAsync() {
        items.clear();
        itemAdapter.notifyDataSetChanged();
        apiClient.getUserTransactions(position, this::handleTransactionsResponse, this::handleTransactionsError);
        swipeContainer.setRefreshing(false);
    }

    private void handleTransactionsResponse(Response response) {
        try {
            String responseData = response.body().string();
            JSONArray jsonArray = new JSONArray(responseData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int categoryId = jsonObject.getInt("category_id");
                String category = prefManager.getCategoryNameById(categoryId);
                String description = jsonObject.getString("description");
                String date = jsonObject.getString("date_time");
                int amount = jsonObject.getInt("amount");
                items.add(new Item(category, amount, description, date));
            }
            updateUI();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private void handleTransactionsError(Throwable error) {
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(() ->
                    Toast.makeText(activity, "Неудалось получить транзакции", Toast.LENGTH_SHORT).show()
            );
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(() -> itemAdapter.notifyDataSetChanged());
        }
    }
}




