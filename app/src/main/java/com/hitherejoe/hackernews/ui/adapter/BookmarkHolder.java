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
import com.hitherejoe.hackernews.ui.activity.ViewStoryActivity;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.item_bookmarks_list)
public class BookmarkHolder extends ItemViewHolder<Post> {

    @ViewId(R.id.text_post_title)
    TextView mPostTitle;

    @ViewId(R.id.text_post_author)
    TextView mPostAuthor;

    @ViewId(R.id.text_post_points)
    TextView mPostPoints;

    @ViewId(R.id.text_view_post)
    TextView mViewPost;

    @ViewId(R.id.text_remove_bookmark)
    TextView mRemoveBookmark;


    public BookmarkHolder(View view) {
        super(view);
    }

    @Override
    public void onSetValues(Post post, PositionInfo positionInfo) {
        mPostTitle.setText(post.title);
        mPostAuthor.setText(Html.fromHtml(getContext().getString(R.string.story_by) + " " + post.by));
        mPostPoints.setText(post.score + " " + getContext().getString(R.string.story_points));
    }

    @Override
    public void onSetListeners() {
        mViewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BuildConfig.DEBUG) AnalyticsHelper.trackViewStoryClicked();
                launchActivity();
            }
        });
        mRemoveBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemovedListener listener = getListener(RemovedListener.class);
                if (listener != null) listener.onBookmarkRemoved(getItem());
            }
        });
        mPostTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BuildConfig.DEBUG) AnalyticsHelper.trackStoryCardClicked();
                launchActivity();
            }
        });
    }

    private void launchActivity() {
        if (getItem().postType == Post.PostType.ASK) {
            launchCommentsActivity();
        } else {
            launchStoryActivity();
        }
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

    public interface RemovedListener {
        public void onBookmarkRemoved(Post bookmark);
    }

}