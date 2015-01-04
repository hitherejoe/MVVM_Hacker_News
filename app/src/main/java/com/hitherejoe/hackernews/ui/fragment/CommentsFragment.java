package com.hitherejoe.hackernews.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.firebase.client.Firebase;
import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.model.Comment;
import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.data.model.Story;
import com.hitherejoe.hackernews.data.remote.FirebaseHelper;
import com.hitherejoe.hackernews.ui.widget.ItemCommentContainer;
import com.hitherejoe.hackernews.util.ViewUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CommentsFragment extends Fragment {

    @InjectView(R.id.list_stories)
    LinearLayout mListPosts;

    @InjectView(R.id.progress_indicator)
    LinearLayout mProgressBar;

    @InjectView(R.id.layout_no_connection)
    LinearLayout mOfflineContainer;

    public static final String ARG_COMMENTS = "ARG_COMMENTS";
    private Story mPost;
    private FirebaseHelper mFirebaseHelper;
    private Context mContext;
    private int mCommentCount;
    private int mCompleteRequests;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPost = getArguments().getParcelable(ARG_COMMENTS);
        mFirebaseHelper = HackerNewsApplication.get().getFireBaseHelper();
        mContext = getActivity();
        mCommentCount = 0;
        mCompleteRequests = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_post_comments, container, false);
        ButterKnife.inject(this, fragmentView);
        Firebase.setAndroidContext(getActivity());
        checkCanLoadComments();
        return fragmentView;
    }

    @OnClick(R.id.button_try_again)
    public void onTryAgainClick() {
        checkCanLoadComments();
    }

    private void checkCanLoadComments() {
        if (ViewUtils.isNetworkAvailable(getActivity())) {
            showHideOfflineLayout(false);
            retrieveRecursiveComments(mPost);
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

    private void retrieveRecursiveComments(Post post) {
        if (post.kids != null) {
            mCommentCount = post.kids.size();
            for (Long id : post.kids) {
                mFirebaseHelper.getItem(String.valueOf(id), new FirebaseHelper.ItemRetrievedListener() {
                    @Override
                    public void onItemRetrieved(Post comment) {
                        if (comment instanceof Comment) {
                            ItemCommentContainer itemCommentContainer = new ItemCommentContainer(mContext, (Comment) comment, mCommentsCompleteListener);
                            mListPosts.addView(itemCommentContainer);
                            mCompleteRequests++;
                            checkCompleteComments();
                        }
                    }

                    @Override
                    public void onItemNotValid() {
                        mCommentCount--;
                        checkCompleteComments();
                    }
                });
            }
        }
    }

    private void checkCompleteComments() {
        if (mCompleteRequests == mCommentCount) mProgressBar.setVisibility(View.GONE);
    }

    private ItemCommentContainer.CommentsCompleteListener mCommentsCompleteListener = new ItemCommentContainer.CommentsCompleteListener() {
        @Override
        public void onComplete() {
            mCompleteRequests++;
            checkCompleteComments();
        }
    };

}
