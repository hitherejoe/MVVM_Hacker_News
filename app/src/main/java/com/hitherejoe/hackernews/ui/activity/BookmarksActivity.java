package com.hitherejoe.hackernews.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.local.DatabaseHelper;
import com.hitherejoe.hackernews.data.model.Bookmark;
import com.hitherejoe.hackernews.ui.adapter.BookmarkedStoriesHolder;
import com.hitherejoe.hackernews.ui.adapter.BookmarkedStoriesHolder.RemovedListener;
import com.hitherejoe.hackernews.util.ToastFactory;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.ribot.easyadapter.EasyRecyclerAdapter;

public class BookmarksActivity extends BaseActivity {

    @InjectView(R.id.list_stories)
    RecyclerView mStoriesList;

    @InjectView(R.id.text_no_bookmarks)
    TextView mNoBookmarksText;

    @InjectView(R.id.progress_indicator)
    ProgressBar mProgressBar;

    private EasyRecyclerAdapter<Bookmark> mEasyRecycleAdapter;
    private DatabaseHelper mDatabaseHelper;
    private List<Bookmark> mBookmarkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        ButterKnife.inject(this);
        mDatabaseHelper = HackerNewsApplication.get().getDataManager().getDatabaseHelper();
        setupActionBar();
        setupRecyclerView();
        getBookmarkedStories();
    }

    private void setupActionBar() {
        getSupportActionBar().setTitle(getString(R.string.bookmarks));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView() {
        mStoriesList.setHasFixedSize(true);
        mEasyRecycleAdapter = new EasyRecyclerAdapter<>(this, BookmarkedStoriesHolder.class, mBookmarkremovedListener);
        mStoriesList.setAdapter(mEasyRecycleAdapter);
        mStoriesList.setLayoutManager(new LinearLayoutManager(this));
        mStoriesList.setItemAnimator(new DefaultItemAnimator());
    }

    private void getBookmarkedStories() {
        mBookmarkList = mDatabaseHelper.getBookmarkedStories();
        if (mBookmarkList != null && !mBookmarkList.isEmpty()) {
            mNoBookmarksText.setVisibility(View.GONE);
            mEasyRecycleAdapter.setItems(mBookmarkList);
        } else {
            mStoriesList.setVisibility(View.GONE);
        }
        mProgressBar.setVisibility(View.GONE);
    }

    private RemovedListener mBookmarkremovedListener = new RemovedListener() {
        @Override
        public void onBookmarkRemoved(Bookmark bookmark) {
            mDatabaseHelper.deleteBookmark(bookmark);
            mBookmarkList.remove(bookmark);
            mEasyRecycleAdapter.notifyDataSetChanged();
            if (mBookmarkList.isEmpty()) mNoBookmarksText.setVisibility(View.VISIBLE);
            ToastFactory.createToast(
                    getApplicationContext(), getString(R.string.bookmark_removed)).show();
        }
    };
}
