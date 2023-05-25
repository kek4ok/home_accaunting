package com.example.home_accaunting;

public class Transaction {
    private Integer userId;
    private Integer accountId;
    private Integer currencyId;
    private Integer categoryId;
    private String amount;
    private String description;
    private String date_time;

    public Transaction(Integer userId, Integer accountId, Integer currencyId, Integer categoryId, String amount, String description, String date_time) {
        this.userId = userId;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.currencyId = currencyId;
        this.amount = amount;
        this.description = description;
        this.date_time = date_time;
    }


    // getters and setters
}
