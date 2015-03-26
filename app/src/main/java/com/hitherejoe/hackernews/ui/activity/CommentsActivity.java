package com.hitherejoe.hackernews.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.DataManager;
import com.hitherejoe.hackernews.data.model.Comment;
import com.hitherejoe.hackernews.data.model.Story;
import com.hitherejoe.hackernews.ui.adapter.CommentHolder;
import com.hitherejoe.hackernews.util.DataUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.android.app.AppObservable;
import uk.co.ribot.easyadapter.EasyRecyclerAdapter;

public class CommentsActivity extends BaseActivity {

    @InjectView(R.id.progress_indicator)
    LinearLayout mProgressBar;

    @InjectView(R.id.layout_offline)
    LinearLayout mOfflineLayout;

    @InjectView(R.id.recycler_comments)
    RecyclerView mCommentsRecycler;

    private static final String TAG = "CommentsActivity";
    public static final String EXTRA_POST =
            "com.hitherejoe.HackerNews.ui.activity.CommentsActivity.EXTRA_POST";

    private Story mPost;
    private DataManager mDataManager;
    private List<Subscription> mSubscriptions;
    private EasyRecyclerAdapter<Comment> mEasyRecycleAdapter;
    private ArrayList<Comment> mComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.inject(this);
        mSubscriptions = new ArrayList<>();
        mComments = new ArrayList<>();
        mPost = getIntent().getParcelableExtra(EXTRA_POST);
        mDataManager = HackerNewsApplication.get().getDataManager();
        setupActionbar();
        setupRecyclerView();
        loadStoriesIfNetworkConnected();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Subscription subscription : mSubscriptions) subscription.unsubscribe();
    }

    @OnClick(R.id.button_try_again)
    public void onTryAgainClick() {
        loadStoriesIfNetworkConnected();
    }

    private void setupActionbar() {
        String title = mPost.title;
        if (title != null) getSupportActionBar().setTitle(title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView() {
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mEasyRecycleAdapter = new EasyRecyclerAdapter<>(this, CommentHolder.class, mComments);
        mCommentsRecycler.setAdapter(mEasyRecycleAdapter);
    }

    private void loadStoriesIfNetworkConnected() {
        if (DataUtils.isNetworkAvailable(this)) {
            showHideOfflineLayout(false);
            getStoryComments(mPost.kids);
        } else {
            showHideOfflineLayout(true);
        }
    }

    private void getStoryComments(List<Long> commentIds) {
        if (commentIds != null) {
            mSubscriptions.add(AppObservable.bindActivity(this,
                    mDataManager.getStoryComments(commentIds, 0))
                    .subscribeOn(mDataManager.getScheduler())
                    .subscribe(new Subscriber<Comment>() {
                        @Override
                        public void onCompleted() {
                            mProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mProgressBar.setVisibility(View.GONE);
                            Log.e(TAG, "There was an error retrieving the comments " + e);
                        }

                        @Override
                        public void onNext(Comment comment) {
                            addCommentViews(comment);
                        }
                    }));
        }
    }

    private void addCommentViews(Comment comment) {
        mComments.add(comment);
        mComments.addAll(comment.comments);
        mEasyRecycleAdapter.notifyDataSetChanged();
    }

    private void showHideOfflineLayout(boolean isOffline) {
        mOfflineLayout.setVisibility(isOffline ? View.VISIBLE : View.GONE);
        mCommentsRecycler.setVisibility(isOffline ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(isOffline ? View.GONE : View.VISIBLE);
    }
}
