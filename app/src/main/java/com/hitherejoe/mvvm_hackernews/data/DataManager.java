package com.hitherejoe.mvvm_hackernews.data;

import android.content.Context;

import com.hitherejoe.mvvm_hackernews.HackerNewsApplication;
import com.hitherejoe.mvvm_hackernews.data.remote.HackerNewsService;
import com.hitherejoe.mvvm_hackernews.injection.component.DaggerDataManagerComponent;
import com.hitherejoe.mvvm_hackernews.injection.module.DataManagerModule;
import com.hitherejoe.mvvm_hackernews.model.Comment;
import com.hitherejoe.mvvm_hackernews.model.Post;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;

public class DataManager {

    @Inject protected HackerNewsService mHackerNewsService;
    @Inject protected Scheduler mSubscribeScheduler;

    public DataManager(Context context) {
        injectDependencies(context);
    }

    /* This constructor is provided so we can set up a DataManager with mocks from unit test.
     * At the moment this is not possible to do with Dagger because the Gradle APT plugin doesn't
     * work for the unit test variant, plus Dagger 2 doesn't provide a nice way of overriding
     * modules */
    public DataManager(HackerNewsService watchTowerService,
                       Scheduler subscribeScheduler) {
        mHackerNewsService = watchTowerService;
        mSubscribeScheduler = subscribeScheduler;
    }

    protected void injectDependencies(Context context) {
        DaggerDataManagerComponent.builder()
                .applicationComponent(HackerNewsApplication.get(context).getComponent())
                .dataManagerModule(new DataManagerModule())
                .build()
                .inject(this);
    }

    public Scheduler getScheduler() {
        return mSubscribeScheduler;
    }

    public Observable<Post> getTopStories() {
        return mHackerNewsService.getTopStories()
                .concatMap(this::getPostsFromIds);
    }

    public Observable<Post> getUserPosts(String user) {
        return mHackerNewsService.getUser(user)
                .concatMap(user1 -> getPostsFromIds(user1.submitted));
    }

    public Observable<Post> getPostsFromIds(List<Long> storyIds) {
        return Observable.from(storyIds)
                .concatMap(aLong -> mHackerNewsService.getStoryItem(String.valueOf(aLong)))
                .flatMap(post -> post.title != null ? Observable.just(post) : Observable.<Post>empty());
    }

    public Observable<Comment> getPostComments(final List<Long> commentIds, final int depth) {
        return Observable.from(commentIds)
                .concatMap(aLong -> mHackerNewsService.getCommentItem(String.valueOf(aLong)))
                .concatMap(comment -> {
                    comment.depth = depth;
                    if (comment.kids == null || comment.kids.isEmpty()) {
                        return Observable.just(comment);
                    } else {
                        return Observable.just(comment)
                                .mergeWith(getPostComments(comment.kids, depth + 1));
                    }
                })
                .filter(comment -> (comment.by != null && !comment.by.trim().isEmpty()
                        && comment.text != null && !comment.text.trim().isEmpty()));
    }

}
