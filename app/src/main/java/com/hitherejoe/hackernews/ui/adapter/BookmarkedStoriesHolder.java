package com.hitherejoe.hackernews.ui.adapter;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.model.Story;
import com.hitherejoe.hackernews.ui.activity.WebPageActivity;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.item_bookmarks_list)
public class BookmarkedStoriesHolder extends ItemViewHolder<Story> {

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

    private Story mPost;

    public BookmarkedStoriesHolder(View view) {
        super(view);
    }

    @Override
    public void onSetValues(Story post, PositionInfo positionInfo) {
        mPost = post;
        mPostTitle.setText(mPost.title);
        mPostAuthor.setText(Html.fromHtml(getContext().getString(R.string.story_by) + " " + post.by));
        mPostPoints.setText(mPost.score + " " + getContext().getString(R.string.story_points));
    }

    @Override
    public void onSetListeners() {
        mViewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WebPageActivity.class);
                intent.putExtra(WebPageActivity.EXTRA_POST_URL, mPost);
                getContext().startActivity(intent);
            }
        });
        mRemoveBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemovedListener listener = getListener(RemovedListener.class);
                if (listener != null) listener.onBookmarkRemoved(getItem());
            }
        });
    }

    public interface RemovedListener {
        public void onBookmarkRemoved(Story bookmark);
    }

}