package com.hitherejoe.hackernews.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.DataManager;
import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.ui.adapter.StoriesHolder;
import com.hitherejoe.hackernews.ui.adapter.UserStoriesHolder;
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

public class StoriesFragment extends Fragment implements OnRefreshListener {

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.list_stories)
    RecyclerView mListPosts;

    @InjectView(R.id.layout_offline)
    LinearLayout mOfflineContainer;

    @InjectView(R.id.progress_indicator)
    ProgressBar mProgressBar;

    private static final String TAG = "StoriesFragment";
    public static final String ARG_USER = "ARG_USER";

    private DataManager mDataManager;
    private EasyRecyclerAdapter<Post> mEasyRecycleAdapter;
    private List<Subscription> mSubscriptions;
    private List<Post> mStories;
    private String mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscriptions = new ArrayList<>();
        mStories = new ArrayList<>();
        mDataManager = HackerNewsApplication.get().getDataManager();
        Bundle bundle = getArguments();
        if (bundle != null) mUser = bundle.getString(ARG_USER, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_stories, container, false);
        ButterKnife.inject(this, fragmentView);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.hn_orange);
        setupRecyclerView();
        loadStoriesIfNetworkConnected();
        return fragmentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Subscription subscription : mSubscriptions) subscription.unsubscribe();
    }

    @Override
    public void onRefresh() {
        for (Subscription subscription : mSubscriptions) subscription.unsubscribe();
        mEasyRecycleAdapter.setItems(new ArrayList<Post>());
        getTopStories();
    }

    @OnClick(R.id.button_try_again)
    public void onTryAgainClick() {
        loadStoriesIfNetworkConnected();
    }

    private void setupRecyclerView() {
        mListPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListPosts.setHasFixedSize(true);
        mEasyRecycleAdapter = new EasyRecyclerAdapter<>(
                getActivity(),
                mUser == null ? StoriesHolder.class : UserStoriesHolder.class,
                mStories
        );
        mListPosts.setAdapter(mEasyRecycleAdapter);
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
        mSubscriptions.add(AppObservable.bindFragment(this,
                mDataManager.getTopStories())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<Post>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingViews();
                        Log.e(TAG, "There was a problem loading the top stories " + e);
                    }

                    @Override
                    public void onNext(Post post) {
                        hideLoadingViews();
                        mEasyRecycleAdapter.addItem(post);
                    }
                }));
    }

    private void getUserStories() {
        mSubscriptions.add(AppObservable.bindFragment(this,
                mDataManager.getUserPosts(mUser))
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<Post>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingViews();
                        Log.e(TAG, "There was a problem loading the user stories " + e);
                    }

                    @Override
                    public void onNext(Post story) {
                        hideLoadingViews();
                        mEasyRecycleAdapter.addItem(story);
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
