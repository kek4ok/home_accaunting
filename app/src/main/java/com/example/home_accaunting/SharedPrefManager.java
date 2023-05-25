package com.example.home_accaunting;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "HomeAccaunting";

    private static final String KEY_USER_ID = "id";
    private static final String KEY_ACCOUNT_ID = "account_id";
    private static final String KEY_CURRENCY_ID = "currency_id";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void saveUserDetails(int userId, int accountId, int currencyId) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putInt(KEY_ACCOUNT_ID, accountId);
        editor.putInt(KEY_CURRENCY_ID, currencyId);
        editor.apply();
    }

    public int getUserId() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_ID, 0);  // Here, 0 is the default value to return if it cannot find KEY_USER_ID
    }

    public int getAccountId() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ACCOUNT_ID, 1);  // Here, 1 is the default value to return if it cannot find KEY_ACCOUNT_ID
    }

    public int getCurrencyId() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_CURRENCY_ID, 1);  // Here, 1 is the default value to return if it cannot find KEY_CURRENCY_ID
    }

    public void saveCategories(Map<Integer, String> categories) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (Map.Entry<Integer, String> entry : categories.entrySet()) {
            editor.putString(String.valueOf(entry.getKey()), entry.getValue());
        }

        editor.apply();
    }

    public Map<Integer, String> getCategories() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Map<Integer, String> categories = new HashMap<>();
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            categories.put(Integer.parseInt(entry.getKey()), (String) entry.getValue());
        }
        return categories;
    }

    public String getCategoryNameById(int id) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // Use the id as the key to get the category name
        return sharedPreferences.getString(String.valueOf(id), "Unknown Category");

    }
}
