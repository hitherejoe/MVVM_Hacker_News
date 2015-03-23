package com.hitherejoe.hackernews.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.local.DatabaseHelper;
import com.hitherejoe.hackernews.data.model.Bookmark;
import com.hitherejoe.hackernews.ui.adapter.BookmarkedStoriesHolder;
import com.hitherejoe.hackernews.ui.adapter.BookmarkedStoriesHolder.RemovalListener;
import com.hitherejoe.hackernews.util.ToastFactory;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.ribot.easyadapter.EasyRecyclerAdapter;

public class BookmarksActivity extends BaseActivity {

    @InjectView(R.id.list_stories)
    RecyclerView mListPosts;

    @InjectView(R.id.layout_no_bookmarks)
    LinearLayout mNoBookmarksContainer;

    @InjectView(R.id.progress_indicator)
    ProgressBar mProgressBar;

    private EasyRecyclerAdapter mEasyRecycleAdapter;
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
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView() {
        mListPosts.setHasFixedSize(true);
        mEasyRecycleAdapter = new EasyRecyclerAdapter<Bookmark>(this, BookmarkedStoriesHolder.class, mListener);
        mListPosts.setAdapter(mEasyRecycleAdapter);
        mListPosts.setLayoutManager(new LinearLayoutManager(this));
        mListPosts.setItemAnimator(new DefaultItemAnimator());
    }

    private void getBookmarkedStories() {
        mBookmarkList = mDatabaseHelper.getBookmarkedStories();
        if (mBookmarkList != null && !mBookmarkList.isEmpty()) {
            mNoBookmarksContainer.setVisibility(View.GONE);
            mEasyRecycleAdapter.setItems(mBookmarkList);
        } else {
            mListPosts.setVisibility(View.GONE);
        }
        mProgressBar.setVisibility(View.GONE);
    }

    private RemovalListener mListener = new RemovalListener() {
        @Override
        public void onBookmarkRemoved(Bookmark bookmark) {
            mDatabaseHelper.deleteBookmark(bookmark);
            mBookmarkList.remove(bookmark);
            mEasyRecycleAdapter.setItems(mBookmarkList);
            if (mBookmarkList.isEmpty()) {
                mNoBookmarksContainer.setVisibility(View.VISIBLE);
            }
            ToastFactory.createToast(getApplicationContext(), getString(R.string.bookmark_removed)).show();
        }
    };
}
