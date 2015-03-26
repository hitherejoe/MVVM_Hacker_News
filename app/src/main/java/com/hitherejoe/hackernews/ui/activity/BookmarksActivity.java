package com.hitherejoe.hackernews.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import uk.co.ribot.easyadapter.EasyRecyclerAdapter;

public class BookmarksActivity extends BaseActivity {

    @InjectView(R.id.list_stories)
    RecyclerView mStoriesList;

    @InjectView(R.id.text_no_bookmarks)
    TextView mNoBookmarksText;

    @InjectView(R.id.progress_indicator)
    ProgressBar mProgressBar;

    private static final String TAG = "BookmarksActivity";
    private EasyRecyclerAdapter<Post> mEasyRecycleAdapter;
    private DataManager mDataManager;
    private List<Post> mBookmarkList;
    private List<Subscription> mSubscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        ButterKnife.inject(this);
        mDataManager = HackerNewsApplication.get().getDataManager();
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
        getSupportActionBar().setTitle(getString(R.string.bookmarks));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView() {
        mStoriesList.setHasFixedSize(true);
        mEasyRecycleAdapter = new EasyRecyclerAdapter<>(this, BookmarkHolder.class, mBookmarkList, mBookmarkRemovedListener);
        mStoriesList.setAdapter(mEasyRecycleAdapter);
        mStoriesList.setLayoutManager(new LinearLayoutManager(this));
        mStoriesList.setItemAnimator(new DefaultItemAnimator());
    }

    private void getBookmarkedStories() {
        mSubscriptions.add(AppObservable.bindActivity(this,
                mDataManager.getBookmarks())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Observer<Post>() {
                    @Override
                    public void onCompleted() {
                        if (mEasyRecycleAdapter.getItemCount() == 0) {
                            mStoriesList.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressBar.setVisibility(View.GONE);
                        Log.e(TAG, "There was an error retrieving the bookmarks " + e);
                    }

                    @Override
                    public void onNext(Post story) {
                        mProgressBar.setVisibility(View.GONE);
                        mNoBookmarksText.setVisibility(View.GONE);
                        mBookmarkList.add(story);
                    }
                }));
    }

    private void removeBookmark(final Post story) {
        mSubscriptions.add(AppObservable.bindActivity(this,
                mDataManager.deleteBookmark(story))
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {
                        mBookmarkList.remove(story);
                        mEasyRecycleAdapter.notifyDataSetChanged();
                        if (mBookmarkList.isEmpty()) mNoBookmarksText.setVisibility(View.VISIBLE);
                        ToastFactory.createToast(
                                BookmarksActivity.this,
                                getString(R.string.bookmark_removed)
                        ).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "There was an error removing the bookmark " + e);
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
