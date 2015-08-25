package com.hitherejoe.mvvm_hackernews.viewModel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.text.Html;

import com.hitherejoe.mvvm_hackernews.R;
import com.hitherejoe.mvvm_hackernews.model.Post;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

public class CommentHeaderViewModel extends BaseObservable {

    private Context context;
    private Post post;

    public CommentHeaderViewModel(Context context, Post post) {
        this.context = context;
        this.post = post;
    }

    public String getCommentText() {
        return Html.fromHtml(post.text.trim()).toString();
    }

    public String getCommentAuthor() {
        return context.getResources().getString(R.string.text_comment_author, post.by);
    }

    public String getCommentDate() {
        return new PrettyTime().format(new Date(post.time * 1000));
    }

}
