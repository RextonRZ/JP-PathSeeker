package com.example.myapplication;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RecruiterSignUpFragment extends Fragment {

    // Parameters
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public RecruiterSignUpFragment() {
        // Required empty public constructor
    }

    public static RecruiterSignUpFragment newInstance(String param1, String param2) {
        RecruiterSignUpFragment fragment = new RecruiterSignUpFragment();
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
        View view = inflater.inflate(R.layout.fragment_recruiter_sign_up, container, false);

        // Initialize the back button after the view is created
        ImageView backIcon = view.findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed(); // Use onBackPressed to go back
            }
        });
        TextView signin = view.findViewById(R.id.signinre);

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
