package com.example.MAD;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
    private float rating;

    private ImageView profilePhotoRate2;
    private TextView nameRate2, sectorRate, jobRate, bioRate2;
    private Button btnLinkRate;
    private Uri url;
    DatabaseReference userRef;
    String jobId;
    String userEmail;
    String sanitizedEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_company_view_rate, container, false);

        if (getArguments() != null) {
            jobId = getArguments().getString("jobId");
            userEmail = getArguments().getString("rateUser");

            if (userEmail != null) {
                sanitizedEmail = userEmail.replace(".", "_");
                userRef = FirebaseDatabase.getInstance().getReference("users").child("recruiter").child(sanitizedEmail);
                // Now you can use userRef to fetch company details

                // Debug logging
                Log.d("ProfileCompanyViewRate", "JobID: " + jobId);
                Log.d("ProfileCompanyViewRate", "UserEmail: " + userEmail);
                Log.d("ProfileCompanyViewRate", "SanitizedEmail: " + sanitizedEmail);
            } else {
                Log.e("ProfileCompanyViewRate", "User email is null");
                Toast.makeText(requireContext(), "Error: Company information not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("ProfileCompanyViewRate", "No arguments passed to fragment");
            Toast.makeText(requireContext(), "Error: No company information provided", Toast.LENGTH_SHORT).show();
        }

        // Initialize RecyclerView
        RVJob = view.findViewById(R.id.RVJobRate);
        adapter = new RecycleviewJobOfferedAdapter(requireContext(), jobList);
        RVJob.setAdapter(adapter);
        RVJob.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        nameRate2 = view.findViewById(R.id.nameRate2);
        userRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                if (name != null) {
                    nameRate2.setText(name);
                } else {
                    nameRate2.setText("Name not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("YourFragment", "Error fetching user name: " + databaseError.getMessage());
            }
        });

        sectorRate = view.findViewById(R.id.sectorRate);
        userRef.child("sector").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                if (name != null) {
                    sectorRate.setText(name);
                } else {
                    sectorRate.setText("Status not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("YourFragment", "Error fetching sector: " + databaseError.getMessage());
            }
        });

        bioRate2 = view.findViewById(R.id.bioRate2);
        accessBio(userRef);

        profilePhotoRate2 = view.findViewById(R.id.profilePhotoRate2);
        accessProfile(userRef);

        jobRate = view.findViewById(R.id.jobRate);
        // Load data into RecyclerView
        setUpJob();

        btnLinkRate = view.findViewById(R.id.btnLinkRate);
        accessLink(userRef);

        btnLinkRate.setOnClickListener(v -> {
            // Validate the URL format
            if (url != null && Patterns.WEB_URL.matcher(url.toString()).matches()) {
                // If the URL is valid, launch it in a browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, url);
                startActivity(browserIntent);
            } else {
                // If the URL is invalid, show a toast message
                Toast.makeText(requireContext(), "Invalid URL. Please check the URL and try again.", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle "Rate Company" button click
        Button showDialogButton = view.findViewById(R.id.btnRateCompany);
        showDialogButton.setOnClickListener(v -> showCustomDialog());

        return view;
    }

    private void accessBio(DatabaseReference userRef) {
        userRef.child("bio").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String existingBio = dataSnapshot.getValue(String.class);
                    bioRate2.setText(existingBio);
                } else {
                    bioRate2.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error checking for existing bio: " + databaseError.getMessage());
            }
        });
    }

    private void accessProfile(DatabaseReference userRef) {
        userRef.child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String profileImageBase64 = dataSnapshot.getValue(String.class);
                    if (profileImageBase64 != null) {
                        byte[] imageBytes = Base64.decode(profileImageBase64, Base64.DEFAULT);
                        Bitmap profileImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        profilePhotoRate2.setImageBitmap(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error checking for existing profile photo: " + databaseError.getMessage());
            }
        });
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
                        jobRate.setText(String.valueOf(jobList.size()));
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

    private void accessLink(DatabaseReference userRef) {
        userRef.child("link").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String existingLink = dataSnapshot.getValue(String.class);
                    if (existingLink != null) {
                        // Convert the String to Uri
                        url = Uri.parse(existingLink);
                        btnLinkRate.setText(existingLink);
                    }
                } else {
                    btnLinkRate.setText("No Website");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error checking for existing link: " + databaseError.getMessage());
            }
        });
    }

    private void showCustomDialog() {
        // Create a new dialog instance
        Dialog dialog = new Dialog(requireContext());

        // Set the custom layout for the dialog
        dialog.setContentView(R.layout.rating_dialog);
        dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.dialog_boxs));

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
        dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.dialog_boxs));

        TextView successRating = dialog.findViewById(R.id.text1);
        successRating.setText("You rated " + " as " + rating + " star!");

        // Show the dialog
        dialog.show();
    }
}