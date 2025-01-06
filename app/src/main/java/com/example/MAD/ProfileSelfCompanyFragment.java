package com.example.MAD;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileSelfCompanyFragment extends Fragment {
    private RecyclerView RVJob;
    private RecycleviewJobOfferedAdapter adapter;
    private final ArrayList<jobOffered> jobList = new ArrayList<>();
    private final String sanitizedEmail = "balaena@gmail_com"; // Example email

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_self_company, container, false);

        // Handle window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize buttons and their actions
        ImageButton btnSetting = view.findViewById(R.id.btnSettingCompany);
        Button btnEditProfile = view.findViewById(R.id.btnEditProfile2);

        btnSetting.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.companySettingFragment));
        btnEditProfile.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.companyEditFragment));

        // Initialize RecyclerView and adapter
        RVJob = view.findViewById(R.id.RVJob);
        adapter = new RecycleviewJobOfferedAdapter(getContext(), jobList);
        RVJob.setAdapter(adapter);
        RVJob.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Load data into RecyclerView
        setUpJob();

        return view;
    }

    private void setUpJob() {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users")
                .child("recruiter")
                .child(sanitizedEmail)
                .child("postedJobs");

        // Clear the job list before fetching new data to avoid duplication
        jobList.clear();
        adapter.notifyDataSetChanged();

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot jobIdSnapshot : dataSnapshot.getChildren()) {
                        String jobId = jobIdSnapshot.getValue(String.class);

                        if (jobId != null) {
                            fetchJobDetails(jobId);
                        } else {
                            Log.w("ProfileSelfCompany", "Invalid job ID encountered.");
                        }
                    }
                } else {
                    Log.d("ProfileSelfCompany", "No jobs found for this user.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ProfileSelfCompany", "Error loading posted jobs: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Failed to load posted jobs.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchJobDetails(String jobId) {
        DatabaseReference jobRef = FirebaseDatabase.getInstance().getReference("jobs").child(jobId);

        jobRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot jobSnapshot) {
                if (jobSnapshot.exists()) {
                    jobOffered job = jobSnapshot.getValue(jobOffered.class);

                    if (job != null) {
                        jobList.add(job);
                        adapter.notifyItemInserted(jobList.size() - 1); // Notify adapter about the new item
                    } else {
                        Log.w("ProfileSelfCompany", "Job data is null for ID: " + jobId);
                    }
                } else {
                    Log.w("ProfileSelfCompany", "Job not found for ID: " + jobId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ProfileSelfCompany", "Error fetching job details: " + databaseError.getMessage());
            }
        });
    }
}