package com.example.MAD;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileCompanyViewRateFragment extends Fragment {

    private RecyclerView RVJob;
    private RecycleviewJobOfferedAdapter adapter;
    private ArrayList<jobOffered> jobList = new ArrayList<>();
    private final String sanitizedEmail = "balaena@gmail_com";  // Example email
    private float rating;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_company_view_rate, container, false);

        // Initialize RecyclerView
        RVJob = view.findViewById(R.id.RVJob);
        adapter = new RecycleviewJobOfferedAdapter(requireContext(), jobList);
        RVJob.setAdapter(adapter);
        RVJob.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        setUpJob(); // Fetch the job data from Firebase

        Button showDialogButton = view.findViewById(R.id.btnRateCompany);
        showDialogButton.setOnClickListener(v -> showCustomDialog());

        return view;
    }

    private void setUpJob() {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users")
                .child("recruiter")
                .child(sanitizedEmail)
                .child("postedJobs");

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        jobList.clear(); // Clear any existing data in the list

                        for (DataSnapshot jobIdSnapshot : dataSnapshot.getChildren()) {
                            String jobId = jobIdSnapshot.getValue(String.class); // Get the job ID

                            DatabaseReference jobRef = FirebaseDatabase.getInstance().getReference("jobs")
                                    .child(jobId);

                            jobRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot jobSnapshot) {
                                    if (jobSnapshot.exists()) {
                                        jobOffered job = jobSnapshot.getValue(jobOffered.class);
                                        if (job != null) {
                                            jobList.add(job); // Add the job to the list
                                            adapter.notifyItemInserted(jobList.size() - 1); // Notify the adapter of the new item
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to load posted jobs.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCustomDialog() {
        // Create a new dialog instance
        Dialog dialog = new Dialog(requireContext());

        // Set the custom layout for the dialog
        dialog.setContentView(R.layout.rating_dialog);
        dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.dialog_box));

        // Show the dialog
        dialog.show();

        // Set a button's functionality to dismiss the dialog
        Button BtnCancel = dialog.findViewById(R.id.BtnCancel);
        BtnCancel.setOnClickListener(v -> dialog.dismiss());

        // Find views for RatingBar and Buttons
        Button BtnRate = dialog.findViewById(R.id.BtnSave);

        // Handle BtnRate click
        BtnRate.setOnClickListener(v -> {
            RatingBar ratingBar = dialog.findViewById(R.id.rating);
            rating = ratingBar.getRating();

            if (rating != 0) {
                dialog.dismiss();
                showSuccessRating();
            }
        });
    }

    private void showSuccessRating() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.pop_up_message);
        dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.dialog_box));

        TextView successRating = dialog.findViewById(R.id.text1);
        successRating.setText("You rated " + " as " + rating + " star!");

        // Show the dialog
        dialog.show();
    }
}
