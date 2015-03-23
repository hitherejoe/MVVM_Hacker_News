package com.hitherejoe.hackernews.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import rx.schedulers.Schedulers;
import uk.co.ribot.easyadapter.EasyRecyclerAdapter;

public class UserStoriesFragment extends Fragment {

    @InjectView(R.id.list_stories)
    RecyclerView mListPosts;

    @InjectView(R.id.progress_indicator)
    ProgressBar mProgressBar;

    @InjectView(R.id.layout_no_connection)
    LinearLayout mOfflineContainer;

    public static final String ARG_USER = "ARG_USER";
    private EasyRecyclerAdapter mEasyRecycleAdapter;
    private DataManager mDataManager;
    private String mUser;
    private List<Subscription> mSubscriptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscriptions = new ArrayList<>();
        mDataManager = HackerNewsApplication.get().getDataManager();
        mUser = getArguments().getString(ARG_USER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_user_stories, container, false);
        ButterKnife.inject(this, fragmentView);
        setupRecyclerView();
        checkCanLoadUserStories();
        return fragmentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Subscription subscription : mSubscriptions) subscription.unsubscribe();
    }

    @OnClick(R.id.button_try_again)
    public void onTryAgainClick() {
        checkCanLoadUserStories();
    }

    private void checkCanLoadUserStories() {
        if (ViewUtils.isNetworkAvailable(getActivity())) {
            showHideOfflineLayout(false);
            getUserStoriesNew();
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
        mEasyRecycleAdapter = new EasyRecyclerAdapter<Post>(getActivity(), UserStoriesHolder.class);
        mListPosts.setAdapter(mEasyRecycleAdapter);
        mListPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListPosts.setItemAnimator(new DefaultItemAnimator());
    }

    private void getUserStoriesNew() {
        mSubscriptions.add(AppObservable.bindFragment(this,
                mDataManager.getUserStories(mUser))
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Subscriber<Post>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ERROR", e + "");
                        e.printStackTrace();
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(Post post) {
                        mEasyRecycleAdapter.addItem(post);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }));
    }
}
