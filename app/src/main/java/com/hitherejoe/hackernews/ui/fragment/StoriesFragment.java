package com.hitherejoe.hackernews.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.hitherejoe.hackernews.data.model.Story;
import com.hitherejoe.hackernews.ui.adapter.StoriesHolder;
import com.hitherejoe.hackernews.ui.adapter.UserStoriesHolder;
import com.hitherejoe.hackernews.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.android.app.AppObservable;
import uk.co.ribot.easyadapter.EasyRecyclerAdapter;

public class StoriesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.list_stories)
    RecyclerView mListPosts;

    @InjectView(R.id.progress_indicator)
    ProgressBar mProgressBar;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.layout_no_connection)
    LinearLayout mOfflineContainer;

    public static final String ARG_USER = "ARG_USER";

    private EasyRecyclerAdapter mEasyRecycleAdapter;
    private DataManager mDataManager;
    private List<Subscription> mSubscriptions;
    private List<Story> mStories;

    private String mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscriptions = new ArrayList<>();
        mStories = new ArrayList<>(500);
        mDataManager = HackerNewsApplication.get().getDataManager();
        Bundle bundle = getArguments();
        if (bundle != null) mUser = bundle.getString(ARG_USER, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_top_stories, container, false);
        ButterKnife.inject(this, fragmentView);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        setupRecyclerView();
        checkCanLoadStories();
        return fragmentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Subscription subscripton : mSubscriptions) subscripton.unsubscribe();
    }

    @OnClick(R.id.button_try_again)
    public void onTryAgainClick() {
        checkCanLoadStories();
    }

    private void checkCanLoadStories() {
        if (ViewUtils.isNetworkAvailable(getActivity())) {
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

    private void showHideOfflineLayout(boolean isOffline) {
        if (isOffline) {
            mOfflineContainer.setVisibility(View.VISIBLE);
            mListPosts.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mOfflineContainer.setVisibility(View.GONE);
            mListPosts.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void setupRecyclerView() {
        mListPosts.setHasFixedSize(true);
        mEasyRecycleAdapter = new EasyRecyclerAdapter<Post>(
                getActivity(),
                mUser == null ? StoriesHolder.class : UserStoriesHolder.class,
                mStories
        );
        mListPosts.setAdapter(mEasyRecycleAdapter);
        mListPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListPosts.setItemAnimator(new DefaultItemAnimator());
    }

    private void getTopStories() {
        mSubscriptions.add(AppObservable.bindFragment(this,
                mDataManager.getTopStories())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<Story>() {
                    @Override
                    public void onCompleted() {
                        hideLoadingViews();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ERROR", e + "");
                        e.printStackTrace();
                        hideLoadingViews();
                    }

                    @Override
                    public void onNext(Story post) {
                        mEasyRecycleAdapter.addItem(post);
                    }
                }));
    }

    private void getUserStories() {
        mSubscriptions.add(AppObservable.bindFragment(this,
                mDataManager.getUserStories(mUser))
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<Story>() {
                    @Override
                    public void onCompleted() {
                        hideLoadingViews();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ERROR", e + "");
                        e.printStackTrace();
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(Story story) {
                        mEasyRecycleAdapter.addItem(story);
                    }
                }));
    }

    @Override
    public void onRefresh() {
        for (Subscription subscripton : mSubscriptions) subscripton.unsubscribe();
        mEasyRecycleAdapter.setItems(new ArrayList());
        getTopStories();
    }

    private void hideLoadingViews() {
        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
