package com.hitherejoe.mvvm_hackernews.viewModel;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;

import com.hitherejoe.mvvm_hackernews.R;
import com.hitherejoe.mvvm_hackernews.model.Post;
import com.hitherejoe.mvvm_hackernews.util.CustomTabsHelper;
import com.hitherejoe.mvvm_hackernews.util.ViewUtils;
import com.hitherejoe.mvvm_hackernews.view.activity.CommentsActivity;
import com.hitherejoe.mvvm_hackernews.view.activity.UserActivity;
import com.hitherejoe.mvvm_hackernews.view.activity.ViewStoryActivity;

public class PostViewModel extends BaseObservable {

    private Context context;
    private Post post;
    private Boolean isUserPosts;

    public PostViewModel(Context context, Post post, boolean isUserPosts) {
        this.context = context;
        this.post = post;
        this.isUserPosts = isUserPosts;
    }

    public String getPostScore() {
        return String.valueOf(post.score) + context.getString(R.string.story_points);
    }

    public String getPostTitle() {
        return post.title;
    }

    public Spannable getPostAuthor() {
        String author = context.getString(R.string.text_post_author, post.by);
        SpannableString content = new SpannableString(author);
        int index = author.indexOf(post.by);
        if (!isUserPosts) content.setSpan(new UnderlineSpan(), index, post.by.length() + index, 0);
        return content;
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
        String packageName = CustomTabsHelper.getPackageNameToUse(context);

        if (packageName == null) {
            context.startActivity(ViewStoryActivity.getStartIntent(context, post));
        } else {
            CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
            Intent intent = intentBuilder.build().intent;
            intent.setData(Uri.parse(post.url));
            context.startActivity(intent);
        }
    }

    private void launchCommentsActivity() {
        context.startActivity(CommentsActivity.getStartIntent(context, post));
    }
}
