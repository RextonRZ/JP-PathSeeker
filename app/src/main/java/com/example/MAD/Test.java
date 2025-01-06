// File: Test.java
package com.example.MAD;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class Test extends AppCompatActivity {
    private static final String TAG = "Test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Load the CourseListFragment as the default fragment
        if (savedInstanceState == null) {
            loadFragment(new PartnershipProgramFragment(), false);
        }
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        try {
            Log.d(TAG, "Loading fragment: " + fragment.getClass().getSimpleName());

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);

            if (addToBackStack) {
                transaction.addToBackStack(null);
            }

            transaction.commit();
            Log.d(TAG, "Fragment transaction committed");
        } catch (Exception e) {
            Log.e(TAG, "Error loading fragment: " + e.getMessage());
            e.printStackTrace();
        }
    }
}