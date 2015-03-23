package com.hitherejoe.hackernews.ui.widget;

import android.content.Context;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.model.Comment;
import com.hitherejoe.hackernews.util.ViewUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ItemCommentThread extends RelativeLayout {

    @InjectView(R.id.text_post_title)
    TextView mPostTitle;

    @InjectView(R.id.text_post_author)
    TextView mPostAuthor;

    @InjectView(R.id.text_post_date)
    TextView mPostPoints;

    @InjectView(R.id.container_item)
    LinearLayout mComment;

    public ItemCommentThread(Context context, Comment comment) {
        super(context);
        init();
        setCommentIndent(comment.depth);
        setupViewData(comment);
    }

    private void init() {
        inflate(getContext(), R.layout.item_comments_list, this);
        ButterKnife.inject(this);
    }

    private void setCommentIndent(int depth) {
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        float margin = ViewUtils.convertPixelsToDp(depth * 20, getContext());
        layoutParams.setMargins((int) margin, 0, 0, 0);
        mComment.setLayoutParams(layoutParams);
    }

    public void setupViewData(Comment mComment) {
        if (mComment.text != null) mPostTitle.setText(Html.fromHtml(mComment.text.trim()));
        if (mComment.by != null) mPostAuthor.setText(mComment.by);
        long millisecond = mComment.time * 1000;
        PrettyTime prettyTime = new PrettyTime();
        mPostPoints.setText(prettyTime.format(new Date(millisecond)));
    }

}
