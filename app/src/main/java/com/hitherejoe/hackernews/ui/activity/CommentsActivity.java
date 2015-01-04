package com.hitherejoe.hackernews.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.ui.fragment.CommentsFragment;

public class CommentsActivity extends BaseActivity {

    public static final String EXTRA_COMMENTS = "com.hitherejoe.HackerNews.ui.activity.CommentsActivity.EXTRA_COMMENTS";
    public static final String EXTRA_TITLE = "com.hitherejoe.HackerNews.ui.activity.CommentsActivity.EXTRA_TITLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setupActionbar();
        setFragment();
    }

    private void setupActionbar() {
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (title != null) getSupportActionBar().setTitle(title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new CommentsFragment();
        Bundle b = new Bundle();
        b.putParcelable(CommentsFragment.ARG_COMMENTS, getIntent().getParcelableExtra(EXTRA_COMMENTS));
        fragment.setArguments(b);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }
}
