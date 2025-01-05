package com.example.MAD;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ArticleFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<String> titles;
    private List<Integer> mImages;
    private List<String> subtitle;
    private ArticleAdapter adapter;
    private SearchView searchView;
    private List<Article> articleList;
    private TabLayout tabLayout;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public ArticleFragment() {
    }

    public static ArticleFragment newInstance(String param1, String param2) {
        ArticleFragment fragment = new ArticleFragment();
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
        subtitle = new ArrayList<>();
        articleList = new ArrayList<>();

        // Initialize adapter with empty lists
        adapter = new ArticleAdapter(requireContext(), titles, subtitle, mImages, articleList);

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.fetchArticle(new FirebaseHelper.ArticleCallback() {
            @Override
            public void onSuccess(List<Article> fetchedArticle) {
                if (!isAdded()) {
                    return; // Fragment not attached to activity
                }

                articleList.clear();
                articleList.addAll(fetchedArticle);
                titles.clear();
                mImages.clear();
                subtitle.clear();

                for (Article article : fetchedArticle) {
                    if (article != null) {
                        titles.add(article.getTitle());
                        int imageResId = getValidImageResourceId(article.getImage());
                        mImages.add(imageResId);
                        subtitle.add(article.getSubtitle());
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Failed to fetch articles: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerview3);
        searchView = view.findViewById(R.id.searchView3);

        // Add this line to set the LayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ArticleAdapter(requireContext(), titles, subtitle, mImages, articleList);
        mRecyclerView.setAdapter(adapter);

        ImageView backIcon = view.findViewById(R.id.back_icon);
        backIcon.setOnClickListener(v -> requireActivity().onBackPressed());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });

        tabLayout = view.findViewById(R.id.tablayout);
        tabLayout.selectTab(tabLayout.getTabAt(0));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) { // Articles
                    Navigation.findNavController(requireView()).navigate(R.id.healthFragment);
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
        // Remove tab selection here - no need to reselect current tab
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
            return R.drawable.article1;
        }

        int resId = getResources().getIdentifier(resourceName, "drawable", requireContext().getPackageName());
        if (resId == 0) {
            // Return a default placeholder if the resource is not found
            return R.drawable.article1;
        }

        return resId;
    }

    private void filterList(String text) {
        List<String> filteredTitles = new ArrayList<>();
        List<Integer> filteredImages = new ArrayList<>();
        List<String> filteredSubtitle = new ArrayList<>();
        List<Article> filteredArticles = new ArrayList<>();

        String lowerCaseText = text.toLowerCase();

        for (int i = 0; i < titles.size(); i++) {
            String title = titles.get(i).toLowerCase();
            String description = subtitle.get(i).toLowerCase();

            if (title.contains(lowerCaseText) || description.contains(lowerCaseText)) {
                filteredTitles.add(titles.get(i));
                filteredImages.add(mImages.get(i));
                filteredSubtitle.add(subtitle.get(i));
                filteredArticles.add(articleList.get(i));
            }
        }

        if (filteredTitles.isEmpty()) {
            Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show();
        }

        adapter.setFilteredList(filteredTitles, filteredSubtitle, filteredImages, filteredArticles);
    }
}