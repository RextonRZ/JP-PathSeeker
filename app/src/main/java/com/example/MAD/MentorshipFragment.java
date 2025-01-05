package com.example.MAD;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MentorshipFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<String> titles;
    private List<Integer> mImages;
    private List<String> descriptions;
    private MyAdapter adapter;
    private SearchView searchView;
    private List<Mentor> mentorsList;

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MentorshipFragment() {
        // Required empty public constructor
    }

    public static MentorshipFragment newInstance(String param1, String param2) {
        MentorshipFragment fragment = new MentorshipFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titles = new ArrayList<>();
        mImages = new ArrayList<>();
        descriptions = new ArrayList<>();
        mentorsList = new ArrayList<>(); // Initialize mentorsList

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.fetchMentors(new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Mentor> fetchedMentors) {
                mentorsList.clear();
                mentorsList.addAll(fetchedMentors);
                titles.clear();
                mImages.clear();
                descriptions.clear();

                for (Mentor mentor : fetchedMentors) {
                    titles.add(mentor.mentor_name);

                    // Safely retrieve the image resource ID
                    int imageResId = getValidImageResourceId(mentor.mentor_profilepic);
                    mImages.add(imageResId);

                    descriptions.add(mentor.mentor_title);
                }

                adapter = new MyAdapter(requireContext(), titles, mImages, descriptions, mentorsList);
                mRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to fetch mentors: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mentorship, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerview);
        searchView = view.findViewById(R.id.searchView);
        adapter = new MyAdapter(requireContext(), titles, mImages, descriptions, mentorsList);

        ImageView backIcon = view.findViewById(R.id.back_icon);
        backIcon.setOnClickListener(v -> requireActivity().onBackPressed());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle when the search is submitted
                filterList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle live text changes
                filterList(newText);
                return false;
            }
        });


        return view;
    }

    private int getValidImageResourceId(String resourceName) {
        if (resourceName == null || resourceName.isEmpty()) {
            // Return a default placeholder image resource ID
            return R.drawable.expert1;
        }

        int resId = getResources().getIdentifier(resourceName, "drawable", requireContext().getPackageName());
        if (resId == 0) {
            // Return a default placeholder if the resource is not found
            return R.drawable.expert1;
        }

        return resId;
    }

    private void filterList(String text) {
        List<String> filteredTitles = new ArrayList<>();
        List<Integer> filteredImages = new ArrayList<>();
        List<String> filteredDescriptions = new ArrayList<>();

        String lowerCaseText = text.toLowerCase();

        for (int i = 0; i < titles.size(); i++) {
            String title = titles.get(i).toLowerCase();
            String description = descriptions.get(i).toLowerCase();

            if (title.contains(lowerCaseText) || description.contains(lowerCaseText)) {
                filteredTitles.add(titles.get(i));
                filteredImages.add(mImages.get(i));
                filteredDescriptions.add(descriptions.get(i));
            }
        }

        if (filteredTitles.isEmpty()) {
            // Optionally, display an empty state
            Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show();
        }

        adapter.setFilteredList(filteredTitles, filteredImages, filteredDescriptions);
    }
}
