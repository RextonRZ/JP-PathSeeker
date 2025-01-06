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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileSeekerViewRateFragment extends Fragment {

    private float rating;
    private RecyclerView RVSkill, RVExpShow;

    private RecycleviewSkillAdapter adapter;
    private RecycleviewExperienceShowAdapter adapter2;
    private ArrayList<Skill> skillList = new ArrayList<>();
    private ArrayList<ExperienceShow> expShowList = new ArrayList<>();

    private final String sanitizedEmail = "andrew@gmail_com";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_profile_seeker_view_rate, container, false);

        // Handle window insets for Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerViews
        RVSkill = view.findViewById(R.id.RVSkill);
        RVExpShow = view.findViewById(R.id.RVExpShow);

        // Set up skill RecyclerView
        adapter = new RecycleviewSkillAdapter(requireContext(), skillList);
        RVSkill.setAdapter(adapter);
        RVSkill.setLayoutManager(new LinearLayoutManager(requireContext()));
        setUpSkill();

        // Set up experience RecyclerView
        adapter2 = new RecycleviewExperienceShowAdapter(requireContext(), expShowList);
        RVExpShow.setAdapter(adapter2);
        RVExpShow.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        setUpExp();

        // Handle "Rate Seeker" button click
        Button showDialogButton = view.findViewById(R.id.btnRateSeeker);
        showDialogButton.setOnClickListener(v -> showCustomDialog());

        return view;
    }

    private void setUpSkill() {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users")
                .child("jobseeker")
                .child(sanitizedEmail)
                .child("skills");

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        skillList.clear();
                        for (DataSnapshot skillIdSnapshot : dataSnapshot.getChildren()) {
                            String skillId = skillIdSnapshot.getKey();
                            Skill skill = skillIdSnapshot.getValue(Skill.class);
                            if (skill != null) {
                                skill.setSkillID(skillId);
                                skillList.add(skill);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("Skill List", "No skills found for user.");
                    }
                } catch (Exception e) {
                    Log.e("Error", "Error fetching skill details: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to load skills.", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Error loading skills: " + databaseError.getMessage());
            }
        });
    }

    private void setUpExp() {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users")
                .child("jobseeker")
                .child(sanitizedEmail)
                .child("experience");

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        expShowList.clear();
                        for (DataSnapshot expIdSnapshot : dataSnapshot.getChildren()) {
                            String expId = expIdSnapshot.getKey();
                            ExperienceShow exp = expIdSnapshot.getValue(ExperienceShow.class);
                            if (exp != null) {
                                exp.setExpID(expId);
                                expShowList.add(exp);
                            }
                        }
                        adapter2.notifyDataSetChanged();
                    } else {
                        Log.d("Experience List", "No experiences found for user.");
                    }
                } catch (Exception e) {
                    Log.e("Error", "Error fetching experience details: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to load experiences.", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Error loading experiences: " + databaseError.getMessage());
            }
        });
    }

    private void showCustomDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.rating_dialog);
        dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.dialog_box));
        dialog.show();

        Button BtnCancel = dialog.findViewById(R.id.BtnCancel);
        BtnCancel.setOnClickListener(v -> dialog.dismiss());

        Button BtnRate = dialog.findViewById(R.id.BtnSave);
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

        dialog.show();
    }
}
