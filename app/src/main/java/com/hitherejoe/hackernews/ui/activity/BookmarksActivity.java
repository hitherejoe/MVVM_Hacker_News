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
import com.hitherejoe.hackernews.data.DataManager;
import com.hitherejoe.hackernews.data.model.Story;
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

    private EasyRecyclerAdapter<Story> mEasyRecycleAdapter;
    private DataManager mDataManager;
    private List<Story> mBookmarkList;
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
        mEasyRecycleAdapter = new EasyRecyclerAdapter<>(this, BookmarkHolder.class, mBookmarkList, mBookmarkremovedListener);
        mStoriesList.setAdapter(mEasyRecycleAdapter);
        mStoriesList.setLayoutManager(new LinearLayoutManager(this));
        mStoriesList.setItemAnimator(new DefaultItemAnimator());
    }

    private void getBookmarkedStories() {
        mSubscriptions.add(AppObservable.bindActivity(this,
                mDataManager.getBookmarks())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Observer<Story>() {
                    @Override
                    public void onCompleted() {
                        if (mEasyRecycleAdapter.getItemCount() == 0) {
                            mStoriesList.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Story story) {
                        mProgressBar.setVisibility(View.GONE);
                        mNoBookmarksText.setVisibility(View.GONE);
                        mBookmarkList.add(story);
                    }
                }));
    }

    private void removeBookmark(final Story story) {
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

                    }

                    @Override
                    public void onNext(Void aVoid) {
                    }
                }));
    }

    private RemovedListener mBookmarkremovedListener = new RemovedListener() {
        @Override
        public void onBookmarkRemoved(Story bookmark) {
            removeBookmark(bookmark);
        }
    };
}
