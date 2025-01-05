package com.example.MAD;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HealthFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<String> titles;
    private List<Integer> mImages;
    private List<String> descriptions;
    private MyAdapter adapter;
    private SearchView searchView;
    private List<Mentor> expertsList;
    private TabLayout tabLayout;

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public HealthFragment() {
        // Required empty public constructor
    }

    public static HealthFragment newInstance(String param1, String param2) {
        HealthFragment fragment = new HealthFragment();
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
        expertsList = new ArrayList<>();

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.fetchHealthExperts(new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Mentor> fetchedExperts) {
                Log.d("HealthFragment", "Fetched experts: " + fetchedExperts.size());
                expertsList.clear();
                expertsList.addAll(fetchedExperts);
                titles.clear();
                mImages.clear();
                descriptions.clear();

                for (Mentor expert : fetchedExperts) {
                    Log.d("HealthFragment", "Expert: " + expert.mentor_name);
                    titles.add(expert.mentor_name);
                    mImages.add(getValidImageResourceId(expert.mentor_profilepic));
                    descriptions.add(expert.mentor_title);
                }

                if (adapter == null) {
                    adapter = new MyAdapter(requireContext(), titles, mImages, descriptions, expertsList);
                    mRecyclerView.setAdapter(adapter);
                    Log.d("HealthFragment", "Adapter set");
                } else {
                    adapter.notifyDataSetChanged();
                    Log.d("HealthFragment", "Adapter updated");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("HealthFragment", "Failed to fetch health experts: " + e.getMessage());
            }
        });


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerview2);
        searchView = view.findViewById(R.id.searchView2);
        adapter = new MyAdapter(requireContext(), titles, mImages, descriptions, expertsList);

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
        tabLayout = view.findViewById(R.id.tablayout);
        tabLayout.selectTab(tabLayout.getTabAt(1));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) { // Articles
                    Navigation.findNavController(requireView()).navigate(R.id.articleFragment);
                }
                // Don't add else case - no need to navigate when Wellness Expert tab is selected
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    private void goToArticleFragment() {
        if (isAdded()) {
            Navigation.findNavController(requireView()).navigate(R.id.articleFragment);
        }
    }

    private void goToHealthFragment() {
        if (isAdded()) {
            Navigation.findNavController(requireView()).navigate(R.id.healthFragment);
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.removeHealthExpertsListener(); // Clean up the listener
    }

}
