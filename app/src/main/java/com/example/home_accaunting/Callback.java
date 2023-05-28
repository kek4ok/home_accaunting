package com.example.home_accaunting;

import org.json.JSONException;

public interface Callback {
    void onResult(String[] result) throws JSONException;
}