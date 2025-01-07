package com.example.MAD;

import android.app.Dialog;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class ProfileSeekerViewRateFragment extends Fragment {

    private float rating;
    private RecyclerView RVSkillRate, RVExpShowRate;

    private RecycleviewSkillAdapter adapter;
    private RecycleviewExperienceShowAdapter adapter2;
    private ArrayList<Skill> skillList = new ArrayList<>();
    private ArrayList<ExperienceShow> expShowList = new ArrayList<>();

    TextView nameRate1, statusRate1, numExpRate, bioRate1;
    ImageView profilePhotoRate1;

    Button btnPdfRate;

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

        // Ensure userEmail is not null before sanitizing
        String userEmail = UserSessionManager.getInstance().getUserEmail();
        if (userEmail != null && !userEmail.isEmpty()) {
            String sanitizedEmail = userEmail.replace(".", "_");
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child("jobseeker").child(sanitizedEmail);

            // Initialize RecyclerView for Skills
            RVSkillRate = view.findViewById(R.id.RVSkillRate);
            adapter = new RecycleviewSkillAdapter(requireContext(), skillList);
            RVSkillRate.setAdapter(adapter);
            RVSkillRate.setLayoutManager(new LinearLayoutManager(requireContext()));
            setUpSkill(userRef);

            // Initialize RecyclerView for Experience
            RVExpShowRate = view.findViewById(R.id.RVExpShowRate);
            adapter2 = new RecycleviewExperienceShowAdapter(requireContext(), expShowList);
            RVExpShowRate.setAdapter(adapter2);
            RVExpShowRate.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            setUpExp(userRef);

            // Initialize Views
            nameRate1 = view.findViewById(R.id.nameRate1);
            userRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    if (name != null) {
                        nameRate1.setText(name);
                    } else {
                        nameRate1.setText("Name not available");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("YourFragment", "Error fetching user name: " + databaseError.getMessage());
                }
            });

            statusRate1 = view.findViewById(R.id.statusRate1);

            userRef.child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    if (name != null) {
                        statusRate1.setText(name);
                    } else {
                        statusRate1.setText("Status not available");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("YourFragment", "Error fetching status: " + databaseError.getMessage());
                }
            });



            bioRate1 = view.findViewById(R.id.bioRate1);
            accessBio(userRef);

            profilePhotoRate1 = view.findViewById(R.id.profilePhotoRate1);
            accessProfile(userRef);

            btnPdfRate = view.findViewById(R.id.btnPdfRate);
            btnPdfRate.setOnClickListener(v -> retrievePDFFromDatabase(userRef));

            numExpRate = view.findViewById(R.id.expRate);

            // Handle "Rate Seeker" button click
            Button showDialogButton = view.findViewById(R.id.btnRateSeeker);
            showDialogButton.setOnClickListener(v -> showCustomDialog());
        }
        return view;
    }

    private void setUpSkill(DatabaseReference userRef) {
        DatabaseReference dataRef = userRef
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

    private void setUpExp(DatabaseReference userRef) {
        DatabaseReference dataRef = userRef
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
                        numExpRate.setText(String.valueOf(expShowList.size()));
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

    private void accessBio(DatabaseReference userRef) {
        userRef.child("bio").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String existingBio = dataSnapshot.getValue(String.class);
                    bioRate1.setText(existingBio);
                } else {
                    bioRate1.setText("");
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
                        profilePhotoRate1.setImageBitmap(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error checking for existing profile photo: " + databaseError.getMessage());
            }
        });
    }

    private void retrievePDFFromDatabase(DatabaseReference userRef) {

        // Reference to the user's 'resume' node in the database
        DatabaseReference userPdfRef = userRef.child("resume");

        // Get the data for the resume
        userPdfRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                Toast.makeText(requireContext(), "No PDF found for this user", Toast.LENGTH_SHORT).show();
            } else {
                // Retrieve the base64-encoded PDF string and fileName
                String base64EncodedPDF = snapshot.child("base64Pdf").getValue(String.class);
                String fileName = snapshot.child("fileName").getValue(String.class);

                if (base64EncodedPDF != null && fileName != null) {
                    // Decode the base64 string to get the PDF bytes
                    byte[] pdfBytes = Base64.decode(base64EncodedPDF, Base64.DEFAULT);

                    // Download the PDF with the appropriate fileName
                    downloadPDF(pdfBytes, fileName);
                } else {
                    Toast.makeText(requireContext(), "PDF data is incomplete", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Error retrieving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void downloadPDF(byte[] pdfBytes, String fileName) {
        try {
            // Check if we are on Android 10 (API 29) or later
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use MediaStore to save the file in the Downloads folder (Scoped Storage for Android 10+)
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.RELATIVE_PATH, "Download/");

                Uri pdfUri = requireContext().getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

                if (pdfUri != null) {
                    // Open output stream to save the PDF file
                    try (OutputStream outputStream = requireContext().getContentResolver().openOutputStream(pdfUri)) {
                        if (outputStream != null) {
                            outputStream.write(pdfBytes);
                            Toast.makeText(requireContext(), "PDF downloaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error downloading PDF", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                // For devices below Android 10, write to external storage directly
                File downloadsFolder = new File(requireContext().getExternalFilesDir(null), "Download");
                if (!downloadsFolder.exists()) {
                    downloadsFolder.mkdirs();
                }

                // Save PDF in the downloads folder
                File file = new File(downloadsFolder, fileName);
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    outputStream.write(pdfBytes);
                    Toast.makeText(requireContext(), "PDF downloaded successfully", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Error downloading PDF", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error saving PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCustomDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.rating_dialog);
        dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.dialog_boxs));
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
        dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.dialog_boxs));

        TextView successRating = dialog.findViewById(R.id.text1);
        successRating.setText("You rated " + " as " + rating + " star!");

        dialog.show();
    }
}
