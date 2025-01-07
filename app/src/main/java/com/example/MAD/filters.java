package com.example.MAD;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class filters extends Fragment {
    private double lat, lng;

    private TextView dropdownJobTypeHeader, dropdownRemoteHeader, dropdownExperienceHeader, dropdownJobCategoryHeader;
    private RecyclerView dropdownJobTypeRecyclerView, dropdownRemoteRecyclerView, dropdownExperienceRecyclerView, dropdownJobCategoryRecyclerView;
    private boolean isJobTypeExpanded = false, isRemoteExpanded = false, isExperienceExpanded = false, isJobCategoryExpanded = false;

    // Data lists for dropdowns
    List<String> jobTypes = new ArrayList<>(Arrays.asList("Full-Time", "Part-Time", "Contract", "Temporary", "Internship"));
    List<Boolean> jobTypeSelections = new ArrayList<>(Arrays.asList(false, false, false, false, false));

    List<String> remoteOptions = new ArrayList<>(Arrays.asList("On-Site", "Remote", "Hybrid"));
    List<Boolean> remoteSelections = new ArrayList<>(Arrays.asList(false, false, false));

    List<String> experienceLevels = new ArrayList<>(Arrays.asList("Internship", "Entry-Level", "Mid-Level", "Senior-Level", "Managerial", "Executive", "Freelance"));
    List<Boolean> experienceSelections = new ArrayList<>(Arrays.asList(false, false, false, false, false, false, false));

    List<String> jobCategory = new ArrayList<>(Arrays.asList("Technology", "Engineering", "Healthcare", "Business", "Education", "Creative", "Retail", "Food", "Transportation", "Administrative", "Law", "Science", "Others"));
    List<Boolean> jobCategorySelections = new ArrayList<>(Arrays.asList(false, false, false, false, false, false, false, false, false, false, false, false, false, false));

    private SeekBar radiusSeekBar;
    private TextView radiusValue;

    private double latitude;  // Variable to hold latitude
    private double longitude; // Variable to hold longitude

    public filters() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_filters, container, false);

        // Back button to return to previous page
        ImageButton btnBack = view.findViewById(R.id.IBback);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Navigate to Map when clicking Use Current Location
        TextView tvUseLoc = view.findViewById(R.id.TVuseLoc);
        tvUseLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.mapsFragment);
            }
        });

        // Job Type Dropdown
        dropdownJobTypeHeader = view.findViewById(R.id.dropdownJobTypeHeader);
        dropdownJobTypeRecyclerView = view.findViewById(R.id.dropdownJobTypeRecyclerView);
        setupDropdown(dropdownJobTypeHeader, dropdownJobTypeRecyclerView, jobTypes, jobTypeSelections, new boolean[]{isJobTypeExpanded});

        // Remote Dropdown
        dropdownRemoteHeader = view.findViewById(R.id.dropdownRemoteHeader);
        dropdownRemoteRecyclerView = view.findViewById(R.id.dropdownRemoteRecyclerView);
        setupDropdown(dropdownRemoteHeader, dropdownRemoteRecyclerView, remoteOptions, remoteSelections, new boolean[]{isRemoteExpanded});

        // Experience Dropdown
        dropdownExperienceHeader = view.findViewById(R.id.dropdownExperienceHeader);
        dropdownExperienceRecyclerView = view.findViewById(R.id.dropdownExperienceRecyclerView);
        setupDropdown(dropdownExperienceHeader, dropdownExperienceRecyclerView, experienceLevels, experienceSelections, new boolean[]{isExperienceExpanded});

        // Category Dropdown
        dropdownJobCategoryHeader = view.findViewById(R.id.dropdownJobCategoryHeader);
        dropdownJobCategoryRecyclerView = view.findViewById(R.id.dropdownJobCategoryRecyclerView);
        setupDropdown(dropdownJobCategoryHeader, dropdownJobCategoryRecyclerView, jobCategory, jobCategorySelections, new boolean[]{isJobCategoryExpanded});

        // Update the radius value display as the user moves the slider
        radiusSeekBar = view.findViewById(R.id.radiusSeekBar);
        radiusValue = view.findViewById(R.id.TVWithinKM);
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radiusValue.setText("Within " + progress + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        Button btnApplyFilters = view.findViewById(R.id.btnApply);
        btnApplyFilters.setOnClickListener(v -> {
            // Create a bundle to pass data to the jobSearchFragment
            Bundle bundle = new Bundle();

            // Collect filter data
            int selectedRadius = radiusSeekBar.getProgress();
            bundle.putInt("RADIUS", selectedRadius);
            bundle.putDouble("LATITUDE", latitude);
            bundle.putDouble("LONGITUDE", longitude);
            bundle.putStringArrayList("selectedJobTypes", getSelectedItems(jobTypes, jobTypeSelections));
            bundle.putStringArrayList("selectedRemoteOptions", getSelectedItems(remoteOptions, remoteSelections));
            bundle.putStringArrayList("selectedExperienceLevels", getSelectedItems(experienceLevels, experienceSelections));
            bundle.putStringArrayList("selectedJobCategory", getSelectedItems(jobCategory, jobCategorySelections));

            // Navigate to the jobSearchFragment with the bundle
            Navigation.findNavController(requireView()).navigate(R.id.jobSearchFragment, bundle);
        });

        return view;
    }

    private void setupDropdown(TextView header, RecyclerView recyclerView, List<String> items, List<Boolean> selections, boolean[] isExpanded) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DropdownAdapter adapter = new DropdownAdapter(items, selections, () -> updateHeaderText(header, items, selections));
        recyclerView.setAdapter(adapter);

        header.setOnClickListener(v -> toggleDropdown(recyclerView, header, isExpanded));
    }

    private void toggleDropdown(RecyclerView recyclerView, TextView header, boolean[] isExpanded) {
        boolean currentState = recyclerView.getVisibility() == View.VISIBLE;

        // Set visibility
        recyclerView.setVisibility(currentState ? View.GONE : View.VISIBLE);

        // Adjust height dynamically
        recyclerView.getLayoutParams().height = currentState ? 0 : RecyclerView.LayoutParams.WRAP_CONTENT;
        recyclerView.requestLayout();

        // Update arrow icon
        header.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, currentState ? R.drawable.baseline_keyboard_arrow_down_24 : R.drawable.baseline_keyboard_arrow_up_24, 0);

        isExpanded[0] = !currentState; // Toggle expansion state
    }

    private void updateHeaderText(TextView header, List<String> items, List<Boolean> selections) {
        StringBuilder selectedText = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (selections.get(i)) {
                selectedText.append(items.get(i)).append(", ");
            }
        }

        if (selectedText.length() > 0) {
            selectedText.setLength(selectedText.length() - 2); // Remove trailing comma
            header.setText(selectedText.toString());
        } else {
            // Reset to the default title if no items are selected
            header.setText(header.getHint()); // Ensure the `hint` attribute is set in XML
        }
    }

    private ArrayList<String> getSelectedItems(List<String> items, List<Boolean> selections) {
        ArrayList<String> selectedItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (selections.get(i)) {
                selectedItems.add(items.get(i));
            }
        }
        return selectedItems;
    }
}
