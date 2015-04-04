package com.hitherejoe.hackernews.ui.adapter;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.hitherejoe.hackernews.BuildConfig;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.data.remote.AnalyticsHelper;
import com.hitherejoe.hackernews.ui.activity.CommentsActivity;
import com.hitherejoe.hackernews.ui.activity.UserActivity;
import com.hitherejoe.hackernews.ui.activity.ViewStoryActivity;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.item_stories_list)
public class StoriesHolder extends ItemViewHolder<Post> {

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

    public StoriesHolder(View view) {
        super(view);
    }

    @Override
    public void onSetValues(Post story, PositionInfo positionInfo) {
        mPostTitle.setText(story.title);
        mPostAuthor.setText(Html.fromHtml(getContext().getString(R.string.story_by) + " " + "<u>" + story.by + "</u>"));
        mPostPoints.setText(story.score + " " + getContext().getString(R.string.story_points));
        if (getItem().postType == Post.PostType.ASK && story.kids == null) {
            mPostComments.setVisibility(View.GONE);
        } else {
            mPostComments.setVisibility(View.VISIBLE);
        }
        mViewPost.setVisibility(story.postType == Post.PostType.LINK ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onSetListeners() {
        mPostAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BuildConfig.DEBUG) AnalyticsHelper.trackUserNameClicked();
                Intent intent = new Intent(getContext(), UserActivity.class);
                intent.putExtra(UserActivity.EXTRA_USER, getItem().by);
                getContext().startActivity(intent);
            }
        });
        mPostComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BuildConfig.DEBUG) AnalyticsHelper.trackViewCommentsClicked();
                launchCommentsActivity();
            }
        });
        mViewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BuildConfig.DEBUG) AnalyticsHelper.trackViewStoryClicked();
                launchStoryActivity();
            }
        });
        mPostTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BuildConfig.DEBUG) AnalyticsHelper.trackStoryCardClicked();
            if (getItem().postType == Post.PostType.ASK) {
                launchCommentsActivity();
            } else {
                launchStoryActivity();
            }
            }
        });
    }

    private void launchStoryActivity() {
        Intent intent = new Intent(getContext(), ViewStoryActivity.class);
        intent.putExtra(ViewStoryActivity.EXTRA_POST, getItem());
        getContext().startActivity(intent);
    }

    private void launchCommentsActivity() {
        Intent intent = new Intent(getContext(), CommentsActivity.class);
        intent.putExtra(CommentsActivity.EXTRA_POST, getItem());
        getContext().startActivity(intent);
    }
}