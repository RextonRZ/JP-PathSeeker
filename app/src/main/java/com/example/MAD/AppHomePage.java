package com.example.MAD;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.denzcoskun.imageslider.ImageSlider;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AppHomePage extends AppCompatActivity {

    private ImageSlider imageSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_home_page);

        // Retrieve the username from the Intent
        String userName = getIntent().getStringExtra("userName");

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        // Initialize the fragment when the activity is created (if no saved state exists)
        if (savedInstanceState == null) {
            AppHome_Fragment appHomeFragment = new AppHome_Fragment();
            Bundle bundle = new Bundle();
            bundle.putString("userName", userName); // Add the username to the bundle
            appHomeFragment.setArguments(bundle);

            // Load the fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, appHomeFragment) // Replace 'fragment_container' with the ID of your FrameLayout
                    .commit();
        }

        // Use the new method 'setOnItemSelectedListener'
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new AppHome_Fragment();
            } else if (item.getItemId() == R.id.nav_search) {
                selectedFragment = new Search_Fragment();
            } else if (item.getItemId() == R.id.nav_career) {
                selectedFragment = new CareerMain_Fragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });
    }
}