package com.example.home_accaunting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button registerButton, loginButton;
    private EditText usernameEditText, passwordEditText, emailEditText;
    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerButton = findViewById(R.id.register_button);
        loginButton = findViewById(R.id.login_button);
        usernameEditText = findViewById(R.id.username_input);
        passwordEditText = findViewById(R.id.password_input);
        emailEditText = findViewById(R.id.email_input);

        apiClient = new ApiClient(this);

        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String email = emailEditText.getText().toString();

            apiClient.registerUser(username, password, email, response -> {
                // Обработка ответа
                runOnUiThread(() -> {
                    Intent intent = new Intent(this, SecondActivity.class);
                    this.startActivity(intent);
                    Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                });
            }, error -> {
                // Обработка ошибки
                runOnUiThread(() -> {
                    Toast.makeText(this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                });
            });
        });

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            apiClient.loginUser(username, password, response -> {
                // Обработка ответа
                runOnUiThread(() -> {
                    Intent intent = new Intent(this, SecondActivity.class);
                    this.startActivity(intent);
                    Toast.makeText(this, "Авторизация успешна", Toast.LENGTH_SHORT).show();
                });
            }, error -> {
                // Обработка ошибки
                runOnUiThread(() -> {
                    Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
                });
            });
        });
    }

    public void onGoogleLoginClicked(View view) {
        // Здесь можно добавить код аутентификации через Google, если это необходимо

        // Переход на SecondActivity после успешной аутентификации
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }


}