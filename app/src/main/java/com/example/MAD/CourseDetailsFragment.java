package com.example.MAD;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CourseDetailsFragment extends Fragment {
    private static final String TAG = "CourseDetailsFragment";
    private static final String ARG_COURSE = "course";

    public static CourseDetailsFragment newInstance(Course course) {
        Log.d(TAG, "Creating new instance for course: " + course.getCourseName());
        CourseDetailsFragment fragment = new CourseDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_COURSE, course);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView started");
        View view = inflater.inflate(R.layout.fragment_course_details, container, false);

        try {
            Bundle args = getArguments();
            if (args == null) {
                Log.e(TAG, "Arguments bundle is null");
                showError("Error loading course details");
                return view;
            }

            Course course = (Course) args.getSerializable(ARG_COURSE);
            if (course == null) {
                Log.e(TAG, "Course object is null");
                showError("Course details not found");
                return view;
            }

            Log.d(TAG, "Received course: " + course.getCourseName());

            // Find all views
            ImageView courseImage = view.findViewById(R.id.course_image_details);
            TextView courseName = view.findViewById(R.id.course_name_details);
            TextView courseLevel = view.findViewById(R.id.course_level_details);
            TextView courseDuration = view.findViewById(R.id.course_duration_details);
            TextView courseContent = view.findViewById(R.id.course_content_details);
            TextView courseDescription = view.findViewById(R.id.course_description_details);
            TextView courseRating = view.findViewById(R.id.course_rating_details);
            Button enrollButton = view.findViewById(R.id.btn_enroll);
            ImageButton backButton = view.findViewById(R.id.back_button);

            // Check if views are found
            if (courseName == null || courseLevel == null || courseDuration == null ||
                    courseContent == null || courseDescription == null || courseRating == null ||
                    enrollButton == null || backButton == null) {
                Log.e(TAG, "One or more views not found in layout");
                showError("Error loading course details");
                return view;
            }

            // Set the data
            courseImage.setImageResource(course.getImageResId());
            courseName.setText(course.getCourseName());
            courseLevel.setText(course.getLevel());
            courseDuration.setText(course.getDuration());
            courseContent.setText(course.getContentDetails());
            courseDescription.setText(course.getDescription());
            courseRating.setText(String.format("%.1f / 10", course.getRating()));

            // Set click listeners
            backButton.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });

            enrollButton.setOnClickListener(v -> {
                String courseUrl = course.getUrl();
                if (courseUrl != null && !courseUrl.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(courseUrl));
                    startActivity(intent);
                } else {
                    showError("Course URL not available");
                }
            });

            Log.d(TAG, "Successfully populated all views with course data");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView: " + e.getMessage());
            e.printStackTrace();
            showError("Error loading course details");
        }

        return view;
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}