package com.example.MAD;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationRequest extends Fragment {

    private DatabaseReference ref;

    private RecyclerView recyclerView;
    private ApplicationAdapter adapter;
    private List<Application> applicationList;
    private String username;

    public NotificationRequest() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_notification, container, false);

        username = "balaena@gmail_com";

        // Initialize and set the click listener for the ImageButton
        ImageButton btnBack = view.findViewById(R.id.IBback);
        btnBack.setOnClickListener(v -> {
            // Navigate to jobSearch Fragment (use the appropriate fragment transaction)
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new jobPosted())
                    .addToBackStack(null)
                    .commit();
        });

        applicationList = new ArrayList<>();

        // Initialize RecyclerView and set up the adapter
        recyclerView = view.findViewById(R.id.RVapplicant);
        adapter = new ApplicationAdapter(getActivity(), username, applicationList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        fetchApplicants();

        return view;
    }

    private void fetchApplicants() {
        ref = FirebaseDatabase.getInstance().getReference("applications/jobs");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                applicationList.clear();  // Clear the existing list

                if (snapshot.exists()) {
                    for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                        Application application = new Application();
                        String jobId = jobSnapshot.getKey();
                        application.setJobId(jobId);

                        // Iterate through users under a specific job
                        for (DataSnapshot userSnapshot : jobSnapshot.getChildren()) {
                            // Extract userId from userSnapshot
                            String userId = userSnapshot.getKey();  // The userId is the key in userSnapshot
                            application.setUserId(userId);

                            // Extract other fields as necessary
                            String name = userSnapshot.child("name").getValue(String.class);
                            application.setApplicantName(name);

                            String interviewDateTime = userSnapshot.child("interviewDateTime").getValue(String.class);
                            application.setInterviewDateTime(interviewDateTime);

                            String imageBase64 = userSnapshot.child("profile").getValue(String.class);
                            application.setImageBase64(imageBase64);

                            String jobTitle = userSnapshot.child("title").getValue(String.class);
                            application.setPosition(jobTitle);

                            ArrayList<String> jobTypeList = userSnapshot.child("jobType").getValue(new GenericTypeIndicator<ArrayList<String>>() {});
                            application.setJobTypeList(jobTypeList);

                            String status = userSnapshot.child("status").getValue(String.class);
                            application.setStatus(status);

                            // Filter out applications that have been accepted or rejected
                            if (!"Success".equals(status) && !"Rejected".equals(status)) {
                                applicationList.add(application);
                            }
                        }
                    }
                    adapter.setApplicationList(applicationList);
                } else {
                    Toast.makeText(getActivity(), "No applications found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error fetching applicants: " + error.getMessage());
            }
        });
    }
}