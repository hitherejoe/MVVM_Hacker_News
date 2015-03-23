package com.hitherejoe.hackernews.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.ui.fragment.StoriesFragment;

public class UserActivity extends BaseActivity {

    public static final String EXTRA_USER =
            "com.hitherejoe.HackerNews.ui.activity.UserActivity.EXTRA_USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Bundle bundle = getIntent().getExtras();
        String username = bundle.getString(EXTRA_USER);
        setupActionBar(username);
        setFragment(username);
    }

    private void setupActionBar(String username) {
        getSupportActionBar().setTitle(username);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setFragment(String username) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new StoriesFragment();
        Bundle b = new Bundle();
        b.putString(StoriesFragment.ARG_USER, username);
        fragment.setArguments(b);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }
}
