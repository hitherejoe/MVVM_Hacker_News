package com.hitherejoe.hackernews.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.model.Comment;
import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.util.ViewUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_COMMENT = 0;
    private static final int VIEW_TYPE_HEADER = 1;

    Post mPost;
    List<Comment> mComments;

    public CommentAdapter(Post post, List<Comment> comments) {
        mPost = post;
        mComments = comments;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_COMMENT) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comments_list, parent, false);
            return new CommentViewHolder(parent.getContext(), v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_story_text_list, parent, false);
            return new StoryTextViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0 && postHasText()) {
            ((StoryTextViewHolder) holder).setPost(mPost);
        } else {
            int actualPosition = (postHasText()) ? position - 1 : position;
            Comment comment = mComments.get(actualPosition);
            ((CommentViewHolder) holder).setComment(comment, actualPosition);
        }
    }

    @Override
    public int getItemCount() {
        if (postHasText()) {
            return mComments.size() + 1;
        } else {
            return mComments.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && postHasText()) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_COMMENT;
        }
    }

    private boolean postHasText() {
        return (mPost.text != null && !mPost.text.equals(""));
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        Context mContext;
        TextView mCommentText;
        TextView mCommentAuthor;
        TextView mCommentDate;
        RelativeLayout mContainer;
        LinearLayout mComment;

        public CommentViewHolder(Context context, View view) {
            super(view);
            mContext = context;
            mCommentText = (TextView)view.findViewById(R.id.text_comment);
            mCommentAuthor = (TextView)view.findViewById(R.id.text_post_author);
            mCommentDate = (TextView)view.findViewById(R.id.text_post_date);
            mContainer = (RelativeLayout)view.findViewById(R.id.container_item);
            mComment = (LinearLayout)view.findViewById(R.id.comment_item);
        }

        public void setComment(Comment comment, int position) {
            if (comment.text != null) mCommentText.setText(Html.fromHtml(comment.text.trim()));
            if (comment.by != null) mCommentAuthor.setText(comment.by);
            long millisecond = comment.time * 1000;
            mCommentDate.setText(new PrettyTime().format(new Date(millisecond)));
            setCommentIndent(comment.depth);

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)
                    mContainer.getLayoutParams();
            Resources resources = mContext.getResources();
            float horizontalMargin = resources.getDimension(R.dimen.activity_horizontal_margin);
            float topMargin = (position == 0) ? resources.getDimension(R.dimen.activity_vertical_margin) : 0;
            layoutParams.setMargins((int) horizontalMargin, (int) topMargin, (int) horizontalMargin, 0);
            mContainer.setLayoutParams(layoutParams);
        }

        private void setCommentIndent(int depth) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    mComment.getLayoutParams();
            float margin = ViewUtils.convertPixelsToDp(depth * 20, mContext);
            layoutParams.setMargins((int) margin, 0, 0, 0);
            mComment.setLayoutParams(layoutParams);
        }
    }

    public class StoryTextViewHolder extends RecyclerView.ViewHolder {
        TextView mPostText;
        TextView mPostAuthor;
        TextView mPostDate;

        public StoryTextViewHolder(View view) {
            super(view);
            mPostText = (TextView)view.findViewById(R.id.text_post_text);
            mPostAuthor = (TextView)view.findViewById(R.id.text_post_author);
            mPostDate = (TextView)view.findViewById(R.id.text_post_date);
        }

        public void setPost(Post post) {
            if (post.text != null) mPostText.setText(Html.fromHtml(post.text.trim()));
            if (post.by != null) mPostAuthor.setText(post.by);
            long millisecond = post.time * 1000;
            mPostDate.setText(new PrettyTime().format(new Date(millisecond)));
        }
    }
}
