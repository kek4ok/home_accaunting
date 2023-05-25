package com.example.home_accaunting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class SecondActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton addButton;
    private Toolbar toolbar;
    private SharedPrefManager prefManager;
    private ApiClient apiClient;
    private MenuHandler menuHandler;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    Integer userId, accountId, currencyId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        prefManager = SharedPrefManager.getInstance(this);
        apiClient = new ApiClient(this);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu); // Ваш значок для кнопки меню

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        addButton = findViewById(R.id.add_button);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Вычисление 40% ширины экрана
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        ViewGroup.LayoutParams params = navigationView.getLayoutParams();
        params.width = (int) (width * 0.7);
        navigationView.setLayoutParams(params);

        menuHandler = new MenuHandler(drawerLayout, navigationView);
        menuHandler.handleMenu();

        setupViewPager();

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Доходы");
            } else {
                tab.setText("Расходы");
            }
        }).attach();


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // здесь вы вызываете функцию, которая отображает новую карточку с полями ввода и выбора
                displayInputCard();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new IncomeFragment());
        fragments.add(new ExpenseFragment());

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return fragments.size();
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments.get(position);
            }
        });
    }


    void displayInputCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_form, null);

        Spinner spinnerCategory = view.findViewById(R.id.spinner_category);
        EditText editTextAmount = view.findViewById(R.id.edittext_amount);
        EditText editTextDescription = view.findViewById(R.id.edittext_description);
        Button buttonDate = view.findViewById(R.id.button_date);
        Button buttonSubmit = view.findViewById(R.id.button_submit);

        // Fill the spinner with data from your API.
        // For this example, we will use a simple array of strings.
        final Context context = this;
        int position = viewPager.getCurrentItem();
        apiClient.getCategories(result -> {
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, Arrays.asList(result));
            spinnerCategory.setAdapter(spinnerArrayAdapter);
        }, position);


        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yy"; // your format
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                buttonDate.setText(sdf.format(myCalendar.getTime()));
            }
        };


        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SecondActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int category = spinnerCategory.getSelectedItemPosition() + 1;
                String amount = editTextAmount.getText().toString();
                String description = editTextDescription.getText().toString();
                String date = buttonDate.getText().toString();

                // Here you should send your data to your API
                // convert to json and send
                userId = prefManager.getUserId();
                accountId = prefManager.getAccountId();
                currencyId = prefManager.getCurrencyId();
                apiClient.sendTransaction(position, userId, accountId, currencyId, category, amount, description, date, dialog, response -> {
                    // Обработка ответа
                    runOnUiThread(() -> {
                        Toast.makeText(SecondActivity.this, "Транзакция добавлена", Toast.LENGTH_SHORT).show();
                    });
                }, error -> {
                    // Обработка ошибки
                    runOnUiThread(() -> {
                        Toast.makeText(SecondActivity.this, "Ошибка добавления", Toast.LENGTH_SHORT).show();
                    });
                });


            }

        });

    }


}
