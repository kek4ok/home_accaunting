package com.example.home_accaunting;

import android.content.Intent;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MenuHandler {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    public MenuHandler(DrawerLayout drawerLayout, NavigationView navigationView) {
        this.drawerLayout = drawerLayout;
        this.navigationView = navigationView;
    }

    public void handleMenu() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            drawerLayout.closeDrawers();

            // Обработка нажатий на пункты меню
            switch (menuItem.getItemId()) {
                case R.id.settings:
                    // ваш код
                    break;
                case R.id.categories:
                    openCategoriesActivity();
                    break;
                case R.id.transactions:
                    openTransactionsActivity();
                    break;
                case R.id.accounts:
                    // ваш код
                    break;
                case R.id.currencies:
                    // ваш код
                    break;
                case R.id.reports:
                    // ваш код
                    break;
            }

            return true;
        });
    }

    private void openCategoriesActivity() {
        Intent intent = new Intent(navigationView.getContext(), CategoriesActivity.class);

        navigationView.getContext().startActivity(intent);
    }

    private void openTransactionsActivity() {
        Intent intent = new Intent(navigationView.getContext(), SecondActivity.class);

        navigationView.getContext().startActivity(intent);
    }
}
