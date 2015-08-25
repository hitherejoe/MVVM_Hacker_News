package com.hitherejoe.mvvm_hackernews.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hitherejoe.mvvm_hackernews.HackerNewsApplication;
import com.hitherejoe.mvvm_hackernews.R;
import com.hitherejoe.mvvm_hackernews.data.DataManager;
import com.hitherejoe.mvvm_hackernews.model.Comment;
import com.hitherejoe.mvvm_hackernews.model.Post;
import com.hitherejoe.mvvm_hackernews.view.adapter.CommentAdapter;
import com.hitherejoe.mvvm_hackernews.util.DataUtils;
import com.hitherejoe.mvvm_hackernews.util.DialogFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class CommentsActivity extends BaseActivity {

    @Bind(R.id.progress_indicator)
    LinearLayout mProgressBar;

    @Bind(R.id.layout_offline)
    LinearLayout mOfflineLayout;

    @Bind(R.id.recycler_comments)
    RecyclerView mCommentsRecycler;

    @Bind(R.id.layout_comments)
    RelativeLayout mCommentsLayout;

    @Bind(R.id.text_no_comments)
    TextView mNoCommentsText;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public static final String EXTRA_POST =
            "com.hitherejoe.mvvm_hackernews.ui.activity.CommentsActivity.EXTRA_POST";

    private ArrayList<Comment> mComments;
    private CommentAdapter mCommentsAdapter;
    private DataManager mDataManager;
    private CompositeSubscription mSubscriptions;
    private Post mPost;

    public static Intent getStartIntent(Context context, Post post) {
        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtra(EXTRA_POST, post);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.bind(this);
        mPost = getIntent().getParcelableExtra(EXTRA_POST);
        if (mPost == null) throw new IllegalArgumentException("CommentsActivity requires a Post object!");
        mDataManager = HackerNewsApplication.get(this).getComponent().dataManager();
        mSubscriptions = new CompositeSubscription();
        mComments = new ArrayList<>();
        setupToolbar();
        setupRecyclerView();
        loadStoriesIfNetworkConnected();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    @OnClick(R.id.button_try_again)
    public void onTryAgainClick() {
        loadStoriesIfNetworkConnected();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            String title = mPost.title;
            if (title != null) actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mCommentsAdapter = new CommentAdapter(this, mPost, mComments);
        mCommentsRecycler.setAdapter(mCommentsAdapter);
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
        if (commentIds != null && !commentIds.isEmpty()) {
            mSubscriptions.add(mDataManager.getPostComments(commentIds, 0)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(mDataManager.getScheduler())
                    .subscribe(new Subscriber<Comment>() {
                        @Override
                        public void onCompleted() {
                            mProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mProgressBar.setVisibility(View.GONE);
                            Timber.e("There was an error retrieving the comments " + e);
                            DialogFactory.createSimpleOkErrorDialog(
                                    CommentsActivity.this,
                                    getString(R.string.error_comments)
                            ).show();
                        }

                        @Override
                        public void onNext(Comment comment) {
                            addCommentViews(comment);
                        }
                    }));
        } else {
            mProgressBar.setVisibility(View.GONE);
            mCommentsRecycler.setVisibility(View.GONE);
            mNoCommentsText.setVisibility(View.VISIBLE);
        }
    }

    private void addCommentViews(Comment comment) {
        mComments.add(comment);
        mComments.addAll(comment.comments);
        mCommentsAdapter.notifyDataSetChanged();
    }

    private void showHideOfflineLayout(boolean isOffline) {
        mOfflineLayout.setVisibility(isOffline ? View.VISIBLE : View.GONE);
        mCommentsRecycler.setVisibility(isOffline ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(isOffline ? View.GONE : View.VISIBLE);
    }
}
