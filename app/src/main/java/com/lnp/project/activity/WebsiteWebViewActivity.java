package com.lnp.project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lnp.project.R;

public class WebsiteWebViewActivity extends AppCompatActivity {

    WebView websiteWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website_web_view);
        websiteWebView = findViewById(R.id.website_web_view);

        String url = getIntent().getStringExtra("website_url").toString().trim();

        if(url != null) {
            websiteWebView.getSettings().setJavaScriptEnabled(true);
            websiteWebView.setWebViewClient(new WebViewClient());
            websiteWebView.loadUrl(url);
        }
    }
}