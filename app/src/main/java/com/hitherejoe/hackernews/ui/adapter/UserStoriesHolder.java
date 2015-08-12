package com.hitherejoe.hackernews.ui.adapter;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.ui.activity.CommentsActivity;
import com.hitherejoe.hackernews.ui.activity.ViewStoryActivity;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.item_story)
public class UserStoriesHolder extends ItemViewHolder<Post> {

    @ViewId(R.id.text_post_title)
    TextView mPostTitle;

    @ViewId(R.id.text_post_author)
    TextView mPostAuthor;

    @ViewId(R.id.text_post_points)
    TextView mPostPoints;

    @ViewId(R.id.text_view_comments)
    TextView mPostComments;

    @ViewId(R.id.text_view_post)
    TextView mViewPost;

    private Post mPost;

    public UserStoriesHolder(View view) {
        super(view);
    }

    @Override
    public void onSetValues(Post post, PositionInfo positionInfo) {
        mPost = post;
            mPostTitle.setText(post.title);
            mPostAuthor.setText(Html.fromHtml(getContext().getString(R.string.story_by) + " " + post.by));
            mPostPoints.setText(post.score + " " + getContext().getString(R.string.story_points));
            if (post.kids == null) mPostComments.setVisibility(View.GONE);
    }

    @Override
    public void onSetListeners() {
        mPostComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CommentsActivity.class);
                intent.putExtra(CommentsActivity.EXTRA_POST, mPost);
                getContext().startActivity(intent);
            }
        });
        mViewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchStoryActivity();
            }
        });
        mPostTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchStoryActivity();
            }
        });
    }

    private void launchStoryActivity() {
        Intent intent = new Intent(getContext(), ViewStoryActivity.class);
        intent.putExtra(ViewStoryActivity.EXTRA_POST, mPost);
        getContext().startActivity(intent);
    }
}