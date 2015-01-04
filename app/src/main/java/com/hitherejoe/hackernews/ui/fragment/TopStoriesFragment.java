package com.hitherejoe.hackernews.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.firebase.client.Firebase;
import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.data.model.Story;
import com.hitherejoe.hackernews.data.remote.FirebaseHelper;
import com.hitherejoe.hackernews.ui.adapter.StoriesHolder;
import com.hitherejoe.hackernews.util.ViewUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.co.ribot.easyadapter.EasyRecyclerAdapter;

public class TopStoriesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.list_stories)
    RecyclerView mListPosts;

    @InjectView(R.id.progress_indicator)
    ProgressBar mProgressBar;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.layout_no_connection)
    LinearLayout mOfflineContainer;

    private EasyRecyclerAdapter mEasyRecycleAdapter;
    private FirebaseHelper mFirebaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseHelper = HackerNewsApplication.get().getFireBaseHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_top_stories, container, false);
        ButterKnife.inject(this, fragmentView);
        Firebase.setAndroidContext(getActivity());
        mSwipeRefreshLayout.setOnRefreshListener(this);
        setupRecyclerView();
        checkCanLoadTopStories();
        return fragmentView;
    }

    @OnClick(R.id.button_try_again)
    public void onTryAgainClick() {
        checkCanLoadTopStories();
    }

    private void checkCanLoadTopStories() {
        if (ViewUtils.isNetworkAvailable(getActivity())) {
            showHideOfflineLayout(false);
            getTopStories();
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
        mEasyRecycleAdapter = new EasyRecyclerAdapter<Post>(getActivity(), StoriesHolder.class);
        mListPosts.setAdapter(mEasyRecycleAdapter);
        mListPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListPosts.setItemAnimator(new DefaultItemAnimator());
    }

    private void getTopStories() {
        String endpoint = FirebaseHelper.ENDPOINT_TOP_STORIES;
        mFirebaseHelper.getData(endpoint, new FirebaseHelper.DataRetrievedListener() {
            @Override
            public void onDataRetrieved(ArrayList<Long> ids) {
                for (Long id : ids) {
                    mFirebaseHelper.getItem(String.valueOf(id), new FirebaseHelper.ItemRetrievedListener() {
                        @Override
                        public void onItemRetrieved(Post post) {
                            if (post instanceof Story) {
                                mEasyRecycleAdapter.addItem(post);
                                hideLoadingViews();
                            }
                        }

                        @Override
                        public void onItemNotValid() {
                            hideLoadingViews();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        mEasyRecycleAdapter.setItems(new ArrayList());
        getTopStories();
    }

    private void hideLoadingViews() {
        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
