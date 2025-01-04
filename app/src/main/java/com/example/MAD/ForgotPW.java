package com.example.MAD;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ForgotPW extends AppCompatActivity {

    EditText emailInput;
    Button resetPWBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pw);

        ImageView backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button cancelBtn = findViewById(R.id.cancelBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();}
        });

        emailInput = findViewById(R.id.emailInput);
        resetPWBtn = findViewById(R.id.resetPWBtn);
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validateEmail(emailInput);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        resetPWBtn.setOnClickListener(v -> {
            if (validateEmail(emailInput)) {
                checkUser();
            }
        });
    }

    private boolean validateEmail(EditText email) {
        String emailText = email.getText().toString().trim();

        if (emailText.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Enter a valid email address");
            email.requestFocus();
            return false;
        } else {
            email.setError(null);
            email.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.done_icon, null), null);
            return true;
        }
    }

    public void checkUser() {
        String userEmail = emailInput.getText().toString().trim().replace(".", "_");


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query jobseekerQuery = reference.child("jobseeker").child(userEmail);
        jobseekerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    handleUserFound("jobseeker", userEmail);
                } else {
                    // Check recruiter path if jobseeker not found
                    Query recruiterQuery = reference.child("recruiter").child(userEmail);
                    recruiterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                handleUserFound("recruiter", userEmail);
                            } else {
                                // User not found in either jobseeker or recruiter paths
                                emailInput.setError("User email does not exist");
                                emailInput.requestFocus();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("ForgotPW", "Error checking recruiter: " + error.getMessage());
                            Toast.makeText(ForgotPW.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ForgotPW", "Error checking jobseeker: " + error.getMessage());
                Toast.makeText(ForgotPW.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles the case when a user is found in the database.
     * @param userType The type of user (e.g., "jobseeker" or "recruiter").
     * @param userEmail The email of the user.
     */
    private void handleUserFound(String userType, String userEmail) {
        Toast.makeText(ForgotPW.this, userType + " email found!", Toast.LENGTH_SHORT).show();

        // TODO: Add password reset logic here (e.g., sending a reset email)
        Log.d("ForgotPW", userType + " email exists: " + userEmail);
    }
}