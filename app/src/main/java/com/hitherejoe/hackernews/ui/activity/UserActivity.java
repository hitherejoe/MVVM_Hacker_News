package com.hitherejoe.hackernews.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

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
        addStoriesFragment(username);
    }

    private void addStoriesFragment(String username) {
        Fragment storiesFragment = new StoriesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StoriesFragment.ARG_USER, username);
        storiesFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, storiesFragment)
                .commit();
    }
}
