package com.example.myapplication;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class JobSeekerSignUpFragment extends Fragment {

    // Parameters
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public JobSeekerSignUpFragment() {
        // Required empty public constructor
    }

    public static JobSeekerSignUpFragment newInstance(String param1, String param2) {
        JobSeekerSignUpFragment fragment = new JobSeekerSignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_seeker_sign_up, container, false);

        // Initialize the back button after the view is created
        ImageView backIcon = view.findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed(); // Use onBackPressed to go back
            }
        });

        // Initialize Spinner
        Spinner occupationSpinner = view.findViewById(R.id.occupationSpinner);

        // Set up the adapter with the custom layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                requireContext(),
                R.layout.spinner_item, // Custom layout for spinner items
                getResources().getStringArray(R.array.occupation_array)
        );

        // Set custom layout for dropdown items (optional, can reuse the same layout)
        adapter.setDropDownViewResource(R.layout.spinner_item);

        // Assign adapter to the Spinner
        occupationSpinner.setAdapter(adapter);

        // Handle item selection
        occupationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (position > 0) { // Ignore the first item (hint text)
                    Toast.makeText(requireContext(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action required
            }
        });

        TextView signin = view.findViewById(R.id.signin);

        signin.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: // Finger or mouse down
                case MotionEvent.ACTION_HOVER_ENTER: // Mouse enters
                    signin.setPaintFlags(signin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    break;

                case MotionEvent.ACTION_UP: // Finger or mouse up
                case MotionEvent.ACTION_HOVER_EXIT: // Mouse exits
                    signin.setPaintFlags(signin.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));

                    // Use requireContext() to start the activity
                    Intent intent = new Intent(requireContext(), Login.class);
                    startActivity(intent);
                    break;
            }
            return true;
        });

        return view;
    }
}