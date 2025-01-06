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

public class ProfileSelfSeekerFragment extends Fragment {

    private RecyclerView RVSkill, RVExpShow;
    private RecycleviewSkillAdapter adapter;
    private RecycleviewExperienceShowAdapter adapter2;
    private ArrayList<Skill> skillList = new ArrayList<>();
    private ArrayList<ExperienceShow> expShowList = new ArrayList<>();

    private String sanitizedEmail = "andrew@gmail_com";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_profile_seeker_self, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnSetting = view.findViewById(R.id.btnSetting2);
        Button btnEditProfile = view.findViewById(R.id.btnEditProfile3);

         //Set click listeners
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.seekerSettingFragment);
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.seekerEditFragment);
            }
        });

        // Initialize RecyclerView for Skills
        RVSkill = view.findViewById(R.id.RVSkill);
        adapter = new RecycleviewSkillAdapter(requireContext(), skillList);
        RVSkill.setAdapter(adapter);
        RVSkill.setLayoutManager(new LinearLayoutManager(requireContext()));
        setUpSkill();

        // Initialize RecyclerView for Experience
        RVExpShow = view.findViewById(R.id.RVExpShow);
        adapter2 = new RecycleviewExperienceShowAdapter(requireContext(), expShowList);
        RVExpShow.setAdapter(adapter2);
        RVExpShow.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        setUpExp();

        return view;
    }

    private void setUpSkill() {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users")
                .child("jobseeker")
                .child(sanitizedEmail)
                .child("skills");

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to load skill.", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Error loading skill: " + databaseError.getMessage());
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
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                        Log.d("setUpExp", "No experience found for user.");
                    }
                } catch (Exception e) {
                    Log.e("setUpExp", "Error fetching experience details: " + e.getMessage(), e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("setUpExp", "Error loading experience: " + databaseError.getMessage());
            }
        });
    }
}
