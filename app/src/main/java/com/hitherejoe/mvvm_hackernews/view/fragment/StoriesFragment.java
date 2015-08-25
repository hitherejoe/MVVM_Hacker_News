package com.hitherejoe.mvvm_hackernews.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.hitherejoe.mvvm_hackernews.HackerNewsApplication;
import com.hitherejoe.mvvm_hackernews.R;
import com.hitherejoe.mvvm_hackernews.data.DataManager;
import com.hitherejoe.mvvm_hackernews.model.Post;
import com.hitherejoe.mvvm_hackernews.view.adapter.PostAdapter;
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

public class StoriesFragment extends Fragment implements OnRefreshListener {

    @Bind(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.recycler_stories)
    RecyclerView mListPosts;

    @Bind(R.id.layout_offline)
    LinearLayout mOfflineContainer;

    @Bind(R.id.progress_indicator)
    ProgressBar mProgressBar;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public static final String ARG_USER = "ARG_USER";

    private DataManager mDataManager;
    private PostAdapter mPostAdapter;
    private CompositeSubscription mSubscriptions;
    private List<Post> mStories;
    private String mUser;

    public static StoriesFragment newInstance(String user) {
        StoriesFragment storiesFragment = new StoriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, user);
        storiesFragment.setArguments(args);
        return storiesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscriptions = new CompositeSubscription();
        mStories = new ArrayList<>();
        mDataManager = HackerNewsApplication.get(getActivity()).getComponent().dataManager();
        Bundle bundle = getArguments();
        if (bundle != null) mUser = bundle.getString(ARG_USER, null);
        mPostAdapter = new PostAdapter(getActivity(), mUser != null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_stories, container, false);
        ButterKnife.bind(this, fragmentView);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.hn_orange);
        setupToolbar();
        setupRecyclerView();
        loadStoriesIfNetworkConnected();
        return fragmentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    @Override
    public void onRefresh() {
        mSubscriptions.unsubscribe();
        if (mPostAdapter != null) mPostAdapter.setItems(new ArrayList<Post>());
        if (mUser != null) {
            getUserStories();
        } else {
            getTopStories();
        }
    }

    @OnClick(R.id.button_try_again)
    public void onTryAgainClick() {
        loadStoriesIfNetworkConnected();
    }

    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            if (mUser != null) {
                actionBar.setTitle(mUser);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void setupRecyclerView() {
        mListPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListPosts.setHasFixedSize(true);
        mPostAdapter.setItems(mStories);
        mListPosts.setAdapter(mPostAdapter);
    }

    private void loadStoriesIfNetworkConnected() {
        if (DataUtils.isNetworkAvailable(getActivity())) {
            showHideOfflineLayout(false);
            if (mUser != null) {
               getUserStories();
            } else {
               getTopStories();
            }
        } else {
            showHideOfflineLayout(true);
        }
    }

    private void getTopStories() {
        mSubscriptions.add(mDataManager.getTopStories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<Post>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingViews();
                        Timber.e("There was a problem loading the top stories " + e);
                        e.printStackTrace();
                        DialogFactory.createSimpleOkErrorDialog(
                                getActivity(),
                                getString(R.string.error_stories)
                        ).show();
                    }

                    @Override
                    public void onNext(Post post) {
                        hideLoadingViews();
                        mPostAdapter.addItem(post);
                    }
                }));
    }

    private void getUserStories() {
        mSubscriptions.add(mDataManager.getUserPosts(mUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<Post>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingViews();
                        Timber.e("There was a problem loading the user stories " + e);
                        DialogFactory.createSimpleOkErrorDialog(
                                getActivity(),
                                getString(R.string.error_stories)
                        ).show();
                    }

                    @Override
                    public void onNext(Post story) {
                        hideLoadingViews();
                        mPostAdapter.addItem(story);
                    }
                }));
    }

    private void hideLoadingViews() {
        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void showHideOfflineLayout(boolean isOffline) {
        mOfflineContainer.setVisibility(isOffline ? View.VISIBLE : View.GONE);
        mListPosts.setVisibility(isOffline ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(isOffline ? View.GONE : View.VISIBLE);
    }

}
