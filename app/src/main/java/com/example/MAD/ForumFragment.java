package com.example.MAD;


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
        backIcon.setOnClickListener(v -> requireActivity().onBackPressed());

        setUpWebView();
        setUpToolbar(view);

        return view;
    }

    private void setUpWebView() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                requireActivity().setTitle("Loading...");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                requireActivity().setTitle("Finished");
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
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }

    public boolean handleBackPress(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }
}
