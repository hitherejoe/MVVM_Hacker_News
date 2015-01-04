package com.hitherejoe.hackernews.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.hitherejoe.hackernews.ui.adapter.UserStoriesHolder;
import com.hitherejoe.hackernews.util.ViewUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
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
    private String mUser;
    private FirebaseHelper mFirebaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = getArguments().getString(ARG_USER);
        mFirebaseHelper = HackerNewsApplication.get().getFireBaseHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_user_stories, container, false);
        ButterKnife.inject(this, fragmentView);
        Firebase.setAndroidContext(getActivity());
        setupRecyclerView();
        checkCanLoadUserStories();
        return fragmentView;
    }

    @OnClick(R.id.button_try_again)
    public void onTryAgainClick() {
        checkCanLoadUserStories();
    }

    private void checkCanLoadUserStories() {
        if (ViewUtils.isNetworkAvailable(getActivity())) {
            showHideOfflineLayout(false);
            getUserStories();
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

    private void getUserStories() {
        String endpoint = FirebaseHelper.ENDPOINT_USER + mUser;
        mFirebaseHelper.getData(endpoint, new FirebaseHelper.DataRetrievedListener() {
            @Override
            public void onDataRetrieved(ArrayList<Long> ids) {
                for (Long snap : ids) {
                    mFirebaseHelper.getItem(String.valueOf(snap), new FirebaseHelper.ItemRetrievedListener() {
                        @Override
                        public void onItemRetrieved(Post post) {
                            if (post instanceof Story) {
                                mEasyRecycleAdapter.addItem(post);
                            }
                        }

                        @Override
                        public void onItemNotValid() { }
                    });
                }
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }
}
