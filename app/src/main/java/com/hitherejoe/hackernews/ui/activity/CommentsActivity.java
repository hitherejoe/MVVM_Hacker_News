package com.hitherejoe.hackernews.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.DataManager;
import com.hitherejoe.hackernews.data.model.Comment;
import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.ui.adapter.CommentAdapter;
import com.hitherejoe.hackernews.util.DataUtils;
import com.hitherejoe.hackernews.util.DialogFactory;
import com.hitherejoe.hackernews.util.SnackbarFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class CommentsActivity extends BaseActivity {

    @Bind(R.id.layout_comments)
    RelativeLayout mCommentsLayout;

    @Bind(R.id.progress_indicator)
    LinearLayout mProgressBar;

    @Bind(R.id.layout_offline)
    LinearLayout mOfflineLayout;

    @Bind(R.id.recycler_comments)
    RecyclerView mCommentsRecycler;

    @Bind(R.id.text_no_comments)
    TextView mNoCommentsText;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public static final String EXTRA_POST =
            "com.hitherejoe.HackerNews.ui.activity.CommentsActivity.EXTRA_POST";

    private Post mPost;
    private DataManager mDataManager;
    private List<Subscription> mSubscriptions;
    private CommentAdapter mCommentsAdapter;
    private ArrayList<Comment> mComments;

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
        mSubscriptions = new ArrayList<>();
        mComments = new ArrayList<>();
        setupToolbar();
        setupRecyclerView();
        loadStoriesIfNetworkConnected();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Subscription subscription : mSubscriptions) subscription.unsubscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bookmark:
                addBookmark();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        mCommentsAdapter = new CommentAdapter(mPost, mComments);
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

    private void addBookmark() {
        mSubscriptions.add(mDataManager.addBookmark(this, mPost)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Observer<Post>() {

                    private Post bookmarkResult;

                    @Override
                    public void onCompleted() {
                        SnackbarFactory.createSnackbar(
                                CommentsActivity.this,
                                mCommentsLayout,
                                bookmarkResult == null
                                        ? getString(R.string.bookmark_exists)
                                        : getString(R.string.bookmark_added)
                        ).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("There was an error bookmarking the story " + e);
                        DialogFactory.createSimpleOkErrorDialog(
                                CommentsActivity.this,
                                getString(R.string.bookmark_error)
                        ).show();
                    }

                    @Override
                    public void onNext(Post story) {
                        bookmarkResult = story;
                    }
                }));
    }

    private void showHideOfflineLayout(boolean isOffline) {
        mOfflineLayout.setVisibility(isOffline ? View.VISIBLE : View.GONE);
        mCommentsRecycler.setVisibility(isOffline ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(isOffline ? View.GONE : View.VISIBLE);
    }
}
