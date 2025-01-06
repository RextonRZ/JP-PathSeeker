package com.example.MAD;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CourseDetailsActivity extends AppCompatActivity {
    private static final String TAG = "CourseDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        Course course = (Course) getIntent().getSerializableExtra("course");

        if (course == null) {
            Log.e(TAG, "Course object is null");
            return;
        }

        ImageView courseImage = findViewById(R.id.course_image_details);
        TextView courseName = findViewById(R.id.course_name_details);
        TextView courseLevel = findViewById(R.id.course_level_details);
        TextView courseDuration = findViewById(R.id.course_duration_details);
        TextView courseContent = findViewById(R.id.course_content_details);
        TextView courseDescription = findViewById(R.id.course_description_details);
        TextView courseRating = findViewById(R.id.course_rating_details);

        courseImage.setImageResource(course.getImageResId());
        courseName.setText(course.getCourseName());
        courseLevel.setText("Level: " + course.getLevel());
        courseDuration.setText("Duration: " + course.getDuration());
        courseContent.setText("Content: " + course.getContentDetails());
        courseDescription.setText("Description: " + course.getDescription());
        courseRating.setText("Rating: " + course.getRating() + "/10");
    }
}
