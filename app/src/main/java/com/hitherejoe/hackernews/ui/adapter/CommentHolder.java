package com.hitherejoe.hackernews.ui.adapter;

import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.model.Comment;
import com.hitherejoe.hackernews.util.ViewUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.item_comments_list)
public class CommentHolder extends ItemViewHolder<Comment> {

    @ViewId(R.id.text_comment)
    TextView mCommentText;

    @ViewId(R.id.text_post_author)
    TextView mCommentAuthor;

    @ViewId(R.id.text_post_date)
    TextView mCommentPoints;

    public CommentHolder(View view) {
        super(view);
    }

    @Override
    public void onSetValues(Comment comment, PositionInfo positionInfo) {
        if (comment.text != null) mCommentText.setText(Html.fromHtml(comment.text.trim()));
        if (comment.by != null) mCommentAuthor.setText(comment.by);
        long millisecond = comment.time * 1000;
        mCommentPoints.setText(new PrettyTime().format(new Date(millisecond)));
        setCommentIndent(comment.depth);
    }

    private void setCommentIndent(int depth) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        float margin = ViewUtils.convertPixelsToDp(depth * 20, getContext());
        layoutParams.setMargins((int) margin, 0, 0, 0);
        this.getView().setLayoutParams(layoutParams);
    }


}