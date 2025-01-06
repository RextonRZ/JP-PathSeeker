package com.example.MAD;

import android.content.Intent;
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
    RecyclerView RVJob;
    private RecycleviewJobOfferedAdapter adapter;
    private ArrayList<jobOffered> jobList = new ArrayList<>();
    String sanitizedEmail = "balaena@gmail_com";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_self_company, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnSetting = view.findViewById(R.id.btnSettingCompany);
        Button btnEditProfile = view.findViewById(R.id.btnEditProfile2);

        // Set an OnClickListener to navigate when clicked
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.companySettingFragment);
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.companyEditFragment);
            }
        });

        // Initialize RecyclerView
        RVJob = view.findViewById(R.id.RVJob);
        adapter = new RecycleviewJobOfferedAdapter(getContext(), jobList);
        RVJob.setAdapter(adapter);
        RVJob.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        setUpJob();  // Fetch the job data from Firebase

        return view;
    }

    private void setUpJob() {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users")
                .child("recruiter")
                .child(sanitizedEmail)
                .child("postedJobs");

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        jobList.clear();  // Clear any existing data in the list

                        for (DataSnapshot jobIdSnapshot : dataSnapshot.getChildren()) {
                            String jobId = jobIdSnapshot.getValue(String.class);  // Get the job ID

                            DatabaseReference jobRef = FirebaseDatabase.getInstance().getReference("jobs")
                                    .child(jobId);

                            jobRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot jobSnapshot) {
                                    if (jobSnapshot.exists()) {
                                        jobOffered job = jobSnapshot.getValue(jobOffered.class);
                                        if (job != null) {
                                            jobList.add(job);  // Add the job to the list
                                            adapter.notifyItemInserted(jobList.size() - 1);  // Notify the adapter of the new item
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e("Firebase", "Error fetching job details: " + databaseError.getMessage());
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    Log.e("Error", "Error fetching posted jobs: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load posted jobs.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
