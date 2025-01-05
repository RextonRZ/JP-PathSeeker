package com.example.MAD;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;

public class ForumFragment extends Fragment {

    private WebView webView;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        webView = view.findViewById(R.id.webView);
        toolbar = view.findViewById(R.id.toolbar);

        ImageView backIcon = view.findViewById(R.id.back_icon);
        backIcon.setOnClickListener(v -> {
            // Safely handle back press
            Activity activity = getActivity();
            if (activity != null) {
                activity.onBackPressed();
            }
        });

        setUpWebView();
        setUpToolbar(view);

        return view;
    }

    private void setUpWebView() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (isAdded() && progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.setTitle("Loading...");
                    }
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (isAdded() && progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.setTitle("Finished");
                    }
                }
            }
        });

        webView.loadUrl("https://forum-pathseeker.blogspot.com/");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    private void setUpToolbar(View view) {
        ImageButton refresh = view.findViewById(R.id.refresh);
        refresh.setOnClickListener(v -> restartFragment());
    }

    private void restartFragment() {
        if (isAdded() && getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
        }
        webView = null;
        progressBar = null;
        toolbar = null;
    }

    public boolean handleBackPress(KeyEvent event) {
        if (webView != null && event.getAction() == KeyEvent.ACTION_DOWN && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }
}
