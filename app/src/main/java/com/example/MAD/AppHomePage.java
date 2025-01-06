package com.example.MAD;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AppHomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_home_page);

        // Retrieve data from Intent
        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("userEmail");
        String userName = intent.getStringExtra("userName");
        String dob = intent.getStringExtra("dob");
        String workingStatus = intent.getStringExtra("workingStatus");
        String sector = intent.getStringExtra("sector");

        //++++++++++++ LINKED MAIL DE +++++++++++++++++ Initialize UserSessionManager
        UserSessionManager.getInstance().setUserEmail(userEmail);
        UserSessionManager.getInstance().setUserName(userName);
        UserSessionManager.getInstance().setDob(dob);
        UserSessionManager.getInstance().setWorkingStatus(workingStatus);
        UserSessionManager.getInstance().setSector(sector);


        // Prepare the Bundle
        Bundle args = new Bundle();
        args.putString("userEmail", userEmail);
        args.putString("userName", userName);
        args.putString("dob", dob);
        args.putString("workingStatus", workingStatus);
        args.putString("sector", sector);

        //Solved the click here click there problem
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        NavController navController = Navigation.findNavController(this, R.id.fragment_container);

        navController.setGraph(navController.getGraph(), args);

        // Link BottomNavigationView with NavController
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Make sure menu items match destination IDs
        bottomNav.getMenu().findItem(R.id.careerFragment).setChecked(true);

        // Setup the NavigationUI
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Add destination change listener
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Clear all selections first
            for (int i = 0; i < bottomNav.getMenu().size(); i++) {
                bottomNav.getMenu().getItem(i).setChecked(false);
            }

            // Check if we're in any career-related fragment
            if (destination.getId() == R.id.careerFragment ||
                    destination.getId() == R.id.mentorshipFragment ||
                    destination.getId() == R.id.articleFragment ||
                    destination.getId() == R.id.forumFragment ||
                    destination.getId() == R.id.healthFragment ||
                    destination.getId() == R.id.articleDetailFragment ||
                    destination.getId() == R.id.scheduleFragment ||
                    destination.getId() == R.id.calendarFragment) {
                bottomNav.getMenu().findItem(R.id.careerFragment).setChecked(true);
            } else {
                // For other destinations, check the corresponding menu item
                MenuItem item = bottomNav.getMenu().findItem(destination.getId());
                if (item != null) {
                    item.setChecked(true);
                }
            }
        });

        // Add Main Fragments with Arguments
        setupMainFragments(args);
    }

    private void setupMainFragments(Bundle args) {
        // Create instances of all main fragments
        AppHome_Fragment homeFragment = new AppHome_Fragment();
        homeFragment.setArguments(args);

        Search_Fragment searchFragment = new Search_Fragment();
        searchFragment.setArguments(args);

        CareerMain_Fragment careerFragment = new CareerMain_Fragment();
        careerFragment.setArguments(args);

        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(args);

        // Replace the initial fragment (e.g., AppHome_Fragment)
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();
    }
}