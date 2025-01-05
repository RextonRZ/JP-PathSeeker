package com.example.MAD;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AppHomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_home_page);

        // Set up NavController
        NavController navController = Navigation.findNavController(this, R.id.fragment_container);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        // Link BottomNavigationView with NavController
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}
