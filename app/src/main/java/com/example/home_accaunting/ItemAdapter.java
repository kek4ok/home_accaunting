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

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> items;

    public ItemAdapter(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.itemName.setText(items.get(position).getCategoryName());
        holder.itemAmount.setText(String.valueOf(items.get(position).getAmount()));

        // Обработчик нажатия на элемент
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item item = items.get(position);

                // Создаем и показываем AlertDialog с информацией о транзакции
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Transaction Details")
                        .setMessage(
                                "Category: " + item.getCategoryName() + "\n" +
                                        "Description: " + item.getDescription() + "\n" +
                                        "Amount: " + item.getAmount() + "\n" +
                                        "Date: " + item.getDate()
                        )
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemAmount;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemAmount = itemView.findViewById(R.id.item_amount);
        }
    }
}

