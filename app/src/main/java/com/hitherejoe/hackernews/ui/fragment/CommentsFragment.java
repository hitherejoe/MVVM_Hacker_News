package com.hitherejoe.hackernews.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.client.Firebase;
import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.DataManager;
import com.hitherejoe.hackernews.data.model.Comment;
import com.hitherejoe.hackernews.data.model.Story;
import com.hitherejoe.hackernews.ui.widget.ItemCommentThread;
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

public class CommentsFragment extends Fragment {

    @InjectView(R.id.list_stories)
    LinearLayout mListPosts;

    @InjectView(R.id.progress_indicator)
    LinearLayout mProgressBar;

    @InjectView(R.id.layout_no_connection)
    LinearLayout mOfflineContainer;

    public static final String ARG_COMMENTS = "ARG_COMMENTS";
    private Story mPost;
    private DataManager mDataManager;
    private List<Subscription> mSubscriptions;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPost = getArguments().getParcelable(ARG_COMMENTS);
        mSubscriptions = new ArrayList<>();
        mDataManager = HackerNewsApplication.get().getDataManager();
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_post_comments, container, false);
        ButterKnife.inject(this, fragmentView);
        Firebase.setAndroidContext(getActivity());
        checkCanLoadComments();
        return fragmentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Subscription subscription : mSubscriptions) subscription.unsubscribe();
    }

    @OnClick(R.id.button_try_again)
    public void onTryAgainClick() {
        checkCanLoadComments();
    }

    private void checkCanLoadComments() {
        if (ViewUtils.isNetworkAvailable(getActivity())) {
            showHideOfflineLayout(false);
            retrieveRecursiveCommentsNew(mPost.kids);
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

    private void retrieveRecursiveCommentsNew(final List<Long> kids) {
        if (kids != null) {
            mSubscriptions.add(AppObservable.bindFragment(this,
                    mDataManager.getStoryComments(kids, 0))
                    .subscribeOn(mDataManager.getScheduler())
                    .subscribe(new Subscriber<Comment>() {
                        @Override
                        public void onCompleted() {
                            mProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("ERROR", e + "");
                            e.printStackTrace();
                            mProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNext(Comment comment) {
                            addCommentViews(comment);
                        }
                    }));
        }
    }

    private void addCommentViews(Comment comment) {
        mListPosts.addView(new ItemCommentThread(mContext, comment));
        for (Comment innerComment : comment.comments) {
            mListPosts.addView(new ItemCommentThread(mContext, innerComment));
        }
    }

}