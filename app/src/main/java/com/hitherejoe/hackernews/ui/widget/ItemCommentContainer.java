package com.hitherejoe.hackernews.ui.widget;

import android.content.Context;
import android.widget.LinearLayout;

import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.model.Comment;
import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.data.remote.FirebaseHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ItemCommentContainer extends LinearLayout {

    @InjectView(R.id.layout_comment_thread)
    LinearLayout mCommentThread;

    private Comment mComment;
    private FirebaseHelper mFirebaseHelper;
    private Context mContext;
    private int mDepth;
    private int mCommentCount;
    private CommentsCompleteListener mCommentsCompleteListener;

    public ItemCommentContainer(Context context, Comment comment, CommentsCompleteListener commentsCompleteListener) {
        super(context);
        mComment = comment;
        mContext = context;
        mFirebaseHelper = HackerNewsApplication.get().getFireBaseHelper();
        mCommentsCompleteListener = commentsCompleteListener;
        mDepth = 0;
        mCommentCount = 0;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_comment_thread, this);
        ButterKnife.inject(this);
        final ItemCommentThread itemCommentThread = new ItemCommentThread(mContext, 0);
        mCommentThread.addView(itemCommentThread);
        itemCommentThread.setupViewData(mComment);
        retrieveRecursiveComments(mComment);
    }

    private void retrieveRecursiveComments(Post post) {
        if (post.kids != null) {
            mDepth++;
            mCommentCount = mCommentCount + post.kids.size();
            for (Long id : post.kids) {
                mFirebaseHelper.getItem(String.valueOf(id), new FirebaseHelper.ItemRetrievedListener() {
                    @Override
                    public void onItemRetrieved(Post comment) {
                        if (comment instanceof Comment) {
                            final ItemCommentThread itemCommentThread = new ItemCommentThread(mContext, mDepth);
                            mCommentThread.addView(itemCommentThread);
                            itemCommentThread.setupViewData((Comment) comment);
                            retrieveRecursiveComments(comment);
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
        if (mCommentThread.getChildCount() == mCommentCount) mCommentsCompleteListener.onComplete();
    }

    public interface CommentsCompleteListener {
        public void onComplete();
    }

}
