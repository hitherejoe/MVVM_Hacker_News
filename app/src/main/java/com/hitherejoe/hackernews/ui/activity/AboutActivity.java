package com.hitherejoe.hackernews.ui.activity;

import android.os.Bundle;

import com.hitherejoe.hackernews.R;

public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupActionBar();
    }

    private void setupActionBar() {
        getSupportActionBar().setTitle(getString(R.string.about));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
