package com.hitherejoe.hackernews.data;

import android.content.Context;
import android.util.Patterns;

import com.hitherejoe.hackernews.BuildConfig;
import com.hitherejoe.hackernews.data.local.DatabaseHelper;
import com.hitherejoe.hackernews.data.local.PreferencesHelper;
import com.hitherejoe.hackernews.data.model.Comment;
import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.data.model.User;
import com.hitherejoe.hackernews.data.remote.AnalyticsHelper;
import com.hitherejoe.hackernews.data.remote.HackerNewsService;
import com.hitherejoe.hackernews.data.remote.RetrofitHelper;

import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action0;
import rx.functions.Func1;

public class DataManager {

    private HackerNewsService mHackerNewsService;
    private DatabaseHelper mDatabaseHelper;
    private PreferencesHelper mPreferencesHelper;
    private Scheduler mScheduler;

    public DataManager(Context context, Scheduler scheduler) {
        mHackerNewsService = new RetrofitHelper().setupHackerNewsService();
        mDatabaseHelper = new DatabaseHelper(context);
        mPreferencesHelper = new PreferencesHelper(context);
        mScheduler = scheduler;
    }

    public void setHackerNewsService(HackerNewsService hackerNewsService) {
        mHackerNewsService = hackerNewsService;
    }

    public void setScheduler(Scheduler scheduler) {
        mScheduler = scheduler;
    }

    public DatabaseHelper getDatabaseHelper() {
        return mDatabaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Scheduler getScheduler() {
        return mScheduler;
    }

    public Observable<Post> getTopStories() {
        return mHackerNewsService.getTopStories()
                .concatMap(new Func1<List<Long>, Observable<? extends Post>>() {
                    @Override
                    public Observable<? extends Post> call(List<Long> longs) {
                        return getPostsFromIds(longs);
                    }
                });
    }

    public Observable<Post> getUserPosts(String user) {
        return mHackerNewsService.getUser(user)
                .concatMap(new Func1<User, Observable<? extends Post>>() {
                    @Override
                    public Observable<? extends Post> call(User user) {
                        return getPostsFromIds(user.submitted);
                    }
                });
    }

    public Observable<Post> getPostsFromIds(List<Long> storyIds) {
        return Observable.from(storyIds)
                .concatMap(new Func1<Long, Observable<Post>>() {
                    @Override
                    public Observable<Post> call(Long aLong) {
                        return mHackerNewsService.getStoryItem(String.valueOf(aLong));
                    }
                }).flatMap(new Func1<Post, Observable<Post>>() {
                    @Override
                    public Observable<Post> call(Post story) {
                        if (Patterns.WEB_URL.matcher(story.url).matches()) {
                            story.postType = Post.PostType.LINK;
                        } else {
                            story.postType = Post.PostType.ASK;
                        }
                        return Observable.just(story);
                    }
                });
    }

    public Observable<Comment> getPostComments(final List<Long> commentIds, final int depth) {
        return Observable.from(commentIds)
                .concatMap(new Func1<Long, Observable<Comment>>() {
                    @Override
                    public Observable<Comment> call(Long aLong) {
                        return mHackerNewsService.getCommentItem(String.valueOf(aLong));
                    }
                }).concatMap(new Func1<Comment, Observable<Comment>>() {
                    @Override
                    public Observable<Comment> call(Comment comment) {
                        comment.depth = depth;
                        if (comment.kids == null || comment.kids.isEmpty()) {
                            return Observable.just(comment);
                        } else {
                            return Observable.just(comment)
                                    .mergeWith(getPostComments(comment.kids, depth + 1));
                        }
                    }
                }).filter(new Func1<Comment, Boolean>() {
                    @Override
                    public Boolean call(Comment comment) {
                        return (comment.by != null && !comment.by.trim().isEmpty()
                                && comment.text != null && !comment.text.trim().isEmpty());
                    }
                });
    }

    public Observable<Post> getBookmarks() {
        return mDatabaseHelper.getBookmarkedStories();
    }

    public Observable<Post> addBookmark(final Post story) {
        return doesBookmarkExist(story)
                .flatMap(new Func1<Boolean, Observable<Post>>() {
                    @Override
                    public Observable<Post> call(Boolean doesExist) {
                        if (!doesExist) return mDatabaseHelper.bookmarkStory(story);
                        return Observable.empty();
                    }
                }).doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        if (!BuildConfig.DEBUG) AnalyticsHelper.trackBookmarkAdded();
                    }
                });
    }

    public Observable<Void> deleteBookmark(Post story) {
        return mDatabaseHelper.deleteBookmark(story)
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        if (!BuildConfig.DEBUG) AnalyticsHelper.trackBookmarkRemoved();
                    }
                });
    }

    public Observable<Boolean> doesBookmarkExist(Post story) {
        return mDatabaseHelper.doesBookmarkExist(story);
    }

}
