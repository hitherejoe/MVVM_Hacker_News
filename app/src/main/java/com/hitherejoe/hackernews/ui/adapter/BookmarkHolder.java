package com.hitherejoe.hackernews.ui.adapter;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.hitherejoe.hackernews.BuildConfig;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.ui.activity.CommentsActivity;
import com.hitherejoe.hackernews.ui.activity.ViewStoryActivity;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.item_bookmark)
public class BookmarkHolder extends ItemViewHolder<Post> {

    @ViewId(R.id.container_bookmark)
    View mBookmarkContainer;

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
        mBookmarkContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        getContext().startActivity(ViewStoryActivity.getStartIntent(getContext(), getItem()));
    }

    private void launchCommentsActivity() {
        getContext().startActivity(CommentsActivity.getStartIntent(getContext(), getItem()));
    }

    public interface RemovedListener {
        void onBookmarkRemoved(Post bookmark);
    }

}