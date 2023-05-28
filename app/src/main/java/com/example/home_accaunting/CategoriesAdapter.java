package com.example.home_accaunting;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ItemViewHolder> {
    private List<Category> items;

    public CategoriesAdapter(List<Category> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CategoriesAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new CategoriesAdapter.ItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.itemName.setText(items.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
        }
    }
}
