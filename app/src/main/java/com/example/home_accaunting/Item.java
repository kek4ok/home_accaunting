package com.example.home_accaunting;

public class Item {
    private String categoryName;
    private double amount;
    private String description;
    private String date;

    public Item(String categoryName, double amount, String description, String date) {
        this.categoryName = categoryName;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }


}
