package com.hitherejoe.hackernews.data;

import android.content.Context;

import com.hitherejoe.hackernews.data.local.DatabaseHelper;
import com.hitherejoe.hackernews.data.local.PreferencesHelper;
import com.hitherejoe.hackernews.data.model.Comment;
import com.hitherejoe.hackernews.data.model.Story;
import com.hitherejoe.hackernews.data.model.User;
import com.hitherejoe.hackernews.data.remote.HackerNewsService;
import com.hitherejoe.hackernews.data.remote.RetrofitHelper;

import java.util.List;

import rx.Observable;
import rx.Scheduler;
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

    public DatabaseHelper getDatabaseHelper() {
        return mDatabaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Scheduler getScheduler() {
        return mScheduler;
    }

    public Observable<Story> getTopStories() {
        return mHackerNewsService.getTopStories()
                .concatMap(new Func1<List<Long>, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(List<Long> longs) {
                        return Observable.from(longs);
                    }
                }).concatMap(new Func1<Long, Observable<Story>>() {
                    @Override
                    public Observable<Story> call(Long aLong) {
                        return mHackerNewsService.getStoryItem(String.valueOf(aLong));
                    }
                });
    }

    public Observable<Story> getUserStories(String user) {
        return mHackerNewsService.getUser(user)
                .concatMap(new Func1<User, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(User user) {
                        return Observable.from(user.submitted);
                    }
                }).concatMap(new Func1<Long, Observable<Story>>() {
                    @Override
                    public Observable<Story> call(Long aLong) {
                        return mHackerNewsService.getStoryItem(String.valueOf(aLong));
                    }
                }).filter(new Func1<Story, Boolean>() {
                    @Override
                    public Boolean call(Story story) {
                        return story.type.equals("story");
                    }
                });
    }

    public Observable<Comment> getStoryComments(final List<Long> commentIds, final int depth) {
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
                                    .mergeWith(getStoryComments(comment.kids, depth + 1));
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

    public Observable<Story> getBookmarks() {
        return mDatabaseHelper.getBookmarkedStories();
    }

    public Observable<Story> addBookmark(final Story story) {
        return doesBookmarkExist(story)
                .flatMap(new Func1<Boolean, Observable<Story>>() {
                    @Override
                    public Observable<Story> call(Boolean doesExist) {
                        if (!doesExist) return mDatabaseHelper.bookmarkStory(story);
                        return Observable.empty();
                    }
                });
    }

    public Observable<Void> deleteBookmark(Story story) {
        return mDatabaseHelper.deleteBookmark(story);
    }

    public Observable<Boolean> doesBookmarkExist(Story story) {
        return mDatabaseHelper.doesBookmarkExist(story);
    }

}
