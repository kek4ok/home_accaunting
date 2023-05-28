package com.example.home_accaunting;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {

    private OkHttpClient client;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private Context context;
    private SharedPrefManager prefManager;
    private String HOST = "http://192.168.56.1:5000";

    public ApiClient(Context context) {
        if (this.client == null) {
            this.client = new OkHttpClient();
        }
        if (this.context == null) {
            this.context = context;
        }
        if (this.prefManager == null) {
            this.prefManager = new SharedPrefManager(context);
        }
    }


    public void registerUser(String username, String password, String email, ResponseCallback responseCallback, ErrorCallback errorCallback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("email", email);
        } catch (JSONException e) {
            errorCallback.onError(e);
            return;
        }

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(HOST + "/register")
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                errorCallback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    responseCallback.onResponse(response);
                    authAndSaveId(username);
                } else {
                    errorCallback.onError(new IOException("Unexpected code " + response));
                }
            }
        });
    }

    public void loginUser(String username, String password, ResponseCallback responseCallback, ErrorCallback errorCallback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            errorCallback.onError(e);
            return;
        }

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(HOST + "/login")
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                errorCallback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    responseCallback.onResponse(response);
                    authAndSaveId(username);
                } else {
                    errorCallback.onError(new IOException("Unexpected code " + response));
                }
            }
        });
    }

    private void authAndSaveId(String username) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                // создаем JSON объект с параметром username
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", username);

                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                Request request = new Request.Builder()
                        .url(HOST + "/auth")
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                // парсим ответ и извлекаем id
                String responseStr = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseStr);
                int id = jsonResponse.getInt("id");

                // сохраняем id в SharedPreferences
                handler.post(() -> prefManager.saveUserDetails(id, 1, 1));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void getCategories(Callback callback, int position) {
        String type;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        if (position == 0) {
            type = "Income";
        } else {
            type = "Expense";
        }

        executor.execute(() -> {
            try {

                Request request = new Request.Builder()
                        .url(HOST + "/get_categories/" + type)
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                JSONArray jsonArray = new JSONArray(response.body().string());
                String[] categories = new String[jsonArray.length()];

                // Вместо использования SharedPreferences напрямую, мы будем использовать SharedPrefManager
                SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
                Map<Integer, String> categoriesMap = new HashMap<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    categories[i] = jsonObject.getString("name");

                    // Добавить категорию в Map
                    categoriesMap.put(jsonObject.getInt("id"), jsonObject.getString("name"));
                }

                // Сохранить категории с помощью SharedPrefManager
                sharedPrefManager.saveCategories(categoriesMap);

                handler.post(() -> {
                    try {
                        callback.onResult(categories);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sendTransaction(int position, int userId, int accountId, int currencyId,
                                int categoryId, String amount, String description,
                                String date_time, AlertDialog dialog,
                                ResponseCallback responseCallback, ErrorCallback errorCallback) {

        String type;
        if (position == 0) {
            type = "Income";
        } else {
            type = "Expense";
        }
        // Создание JSON-объекта и добавление данных
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", userId);
            json.put("account_id", accountId);
            json.put("category_id", categoryId);
            json.put("currency_id", currencyId);
            json.put("amount", amount);
            json.put("description", description);
            json.put("date_time", date_time);
            json.put("type", type);
        } catch (JSONException e) {
            errorCallback.onError(e);
            return;
        }


        // Создание RequestBody с JSON-строкой и MediaType
        RequestBody requestBody = RequestBody.create(JSON, json.toString());

        Request request = new Request.Builder()
                .url(HOST + "/add_transaction")
                .post(requestBody)
                .addHeader("Content-Type", "application/json") // Установка заголовка Content-Type на application/json
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                errorCallback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    responseCallback.onResponse(response);
                    dialog.dismiss();
                } else {
                    errorCallback.onError(new IOException("Unexpected code " + response));
                }
            }
        });
    }

    public void getUserTransactions(int position, ResponseCallback responseCallback, ErrorCallback errorCallback) {

        String type;
        if (position == 0) {
            type = "Income";
        } else {
            type = "Expense";
        }

        int userId = prefManager.getUserId();
        String url = HOST + "/transactions/" + type + "/" + userId;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                errorCallback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    responseCallback.onResponse(response);
                }
            }
        });

    }


    public interface ResponseCallback {
        void onResponse(Response response) throws IOException;
    }

    public interface ErrorCallback {
        void onError(Exception e);
    }
}

