package com.hitherejoe.hackernews.data.model;

import android.content.Context;
import android.databinding.BaseObservable;
import android.text.Html;
import android.view.View;

import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.ui.activity.CommentsActivity;
import com.hitherejoe.hackernews.ui.activity.UserActivity;
import com.hitherejoe.hackernews.ui.activity.ViewStoryActivity;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

public class PostViewModel extends BaseObservable {

    private Context context;
    private Post post;
    private Boolean isUserPosts;

    public PostViewModel(Context context, Post post, boolean isUserPosts) {
        this.context = context;
        this.post = post;
        this.isUserPosts = isUserPosts;
    }

    public Long getPostId() {
        return post.id;
    }

    public String getPostUrl() {
        return post.url;
    }

    public String getPostScore() {
        return String.valueOf(post.score) + " " + context.getString(R.string.story_points);
    }

    public String getPostText() {
        return post.text;
    }

    public Post.PostType getPostType() {
        return post.postType;
    }

    public String getPostTitle() {
        return post.title;
    }

    public String getPostAuthor() {
        String byText = context.getString(R.string.story_by);
        if (isUserPosts) {
            return Html.fromHtml(byText + " " + "<u>" + post.by + "</u>").toString();
        } else {
            return Html.fromHtml(byText + " " + post.by).toString();
        }
    }

    public String getPostDate() {
        return new PrettyTime().format(new Date(post.time * 1000));
    }

    public List<Long> getPostKids() {
        return post.kids;
    }

    public int getCommentsVisibility() {
        return  post.postType == Post.PostType.STORY && post.kids == null ? View.GONE : View.VISIBLE;
    }

    public View.OnClickListener onClickPost() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post.PostType postType = post.postType;
                if (postType == Post.PostType.JOB || postType == Post.PostType.STORY) {
                    launchStoryActivity();
                } else if (postType == Post.PostType.ASK) {
                    launchCommentsActivity();
                }
            }
        };
    }

    public View.OnClickListener onClickAuthor() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(UserActivity.getStartIntent(context, post.by));
            }
        };
    }

    public View.OnClickListener onClickComments() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCommentsActivity();
            }
        };
    }

    private void launchStoryActivity() {
        context.startActivity(ViewStoryActivity.getStartIntent(context, post));
    }

    private void launchCommentsActivity() {
        context.startActivity(CommentsActivity.getStartIntent(context, post));
    }
}
