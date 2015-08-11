package com.hitherejoe.hackernews.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.DataManager;
import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.ui.adapter.BookmarkHolder;
import com.hitherejoe.hackernews.ui.adapter.BookmarkHolder.RemovedListener;
import com.hitherejoe.hackernews.util.ToastFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import uk.co.ribot.easyadapter.EasyRecyclerAdapter;

public class BookmarksActivity extends BaseActivity {

    @Bind(R.id.recycler_bookmarks)
    RecyclerView mBookmarksRecycler;

    @Bind(R.id.text_no_bookmarks)
    TextView mNoBookmarksText;

    @Bind(R.id.progress_indicator)
    ProgressBar mProgressBar;

    private static final String TAG = "BookmarksActivity";
    private EasyRecyclerAdapter<Post> mEasyRecycleAdapter;
    private DataManager mDataManager;
    private List<Post> mBookmarkList;
    private List<Subscription> mSubscriptions;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, BookmarksActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        ButterKnife.bind(this);
        mDataManager = HackerNewsApplication.get(this).getComponent().dataManager();
        mBookmarkList = new ArrayList<>();
        mSubscriptions = new ArrayList<>();
        setupActionBar();
        setupRecyclerView();
        getBookmarkedStories();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Subscription subscription : mSubscriptions) subscription.unsubscribe();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.bookmarks));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        mBookmarksRecycler.setLayoutManager(new LinearLayoutManager(this));
        mEasyRecycleAdapter = new EasyRecyclerAdapter<>(this, BookmarkHolder.class, mBookmarkList, mBookmarkRemovedListener);
        mBookmarksRecycler.setAdapter(mEasyRecycleAdapter);
    }

    private void getBookmarkedStories() {
        mSubscriptions.add(mDataManager.getBookmarks()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Observer<Post>() {
                    @Override
                    public void onCompleted() {
                        mProgressBar.setVisibility(View.GONE);
                        mNoBookmarksText.setVisibility(mBookmarkList.isEmpty() ? View.VISIBLE : View.GONE);
                        mBookmarksRecycler.setVisibility(mBookmarkList.isEmpty() ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressBar.setVisibility(View.GONE);
                        Log.e(TAG, "There was an error retrieving the bookmarks " + e);
                        ToastFactory.createToast(
                                BookmarksActivity.this,
                                getString(R.string.error_getting_bookmarks)
                        ).show();
                    }

                    @Override
                    public void onNext(Post story) {
                        mBookmarkList.add(story);
                    }
                }));
    }

    private void removeBookmark(final Post story) {
        mSubscriptions.add(mDataManager.deleteBookmark(this, story)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {
                        mBookmarkList.remove(story);
                        mEasyRecycleAdapter.notifyDataSetChanged();
                        mNoBookmarksText.setVisibility(mBookmarkList.isEmpty() ? View.VISIBLE : View.GONE);
                        ToastFactory.createToast(
                                BookmarksActivity.this,
                                getString(R.string.bookmark_removed)
                        ).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "There was an error removing the bookmark " + e);
                        ToastFactory.createToast(
                                BookmarksActivity.this,
                                getString(R.string.error_removing_bookmark)
                        ).show();
                    }

                    @Override
                    public void onNext(Void aVoid) { }
                }));
    }

    private RemovedListener mBookmarkRemovedListener = new RemovedListener() {
        @Override
        public void onBookmarkRemoved(Post bookmark) {
            removeBookmark(bookmark);
        }
    };
}
