package com.hitherejoe.mvvm_hackernews;

import com.hitherejoe.mvvm_hackernews.data.DataManager;
import com.hitherejoe.mvvm_hackernews.model.Post;
import com.hitherejoe.mvvm_hackernews.model.User;
import com.hitherejoe.mvvm_hackernews.data.remote.HackerNewsService;
import com.hitherejoe.mvvm_hackernews.util.DefaultConfig;
import com.hitherejoe.mvvm_hackernews.util.MockModelsUtil;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = DefaultConfig.EMULATE_SDK)
public class DataManagerTest {

    private DataManager mDataManager;
    private HackerNewsService mMockHackerNewsService;

    @Before
    public void setUp() {
        mMockHackerNewsService = mock(HackerNewsService.class);
        mDataManager = new DataManager(mMockHackerNewsService, Schedulers.immediate());
    }

    @Test
    public void shouldGetTopStories() throws Exception {
        User mockUser = MockModelsUtil.createMockUser();
        Post mockStoryOne = MockModelsUtil.createMockStoryWithId(mockUser.submitted.get(0));
        Post mockStoryTwo = MockModelsUtil.createMockStoryWithId(mockUser.submitted.get(1));
        Post mockStoryThree = MockModelsUtil.createMockStoryWithId(mockUser.submitted.get(2));
        Post mockStoryFour = MockModelsUtil.createMockStoryWithId(mockUser.submitted.get(3));

        when(mMockHackerNewsService.getStoryItem(String.valueOf(mockUser.submitted.get(0))))
                .thenReturn(Observable.just(mockStoryOne));
        when(mMockHackerNewsService.getStoryItem(String.valueOf(mockUser.submitted.get(1))))
                .thenReturn(Observable.just(mockStoryTwo));
        when(mMockHackerNewsService.getStoryItem(String.valueOf(mockUser.submitted.get(2))))
                .thenReturn(Observable.just(mockStoryThree));
        when(mMockHackerNewsService.getStoryItem(String.valueOf(mockUser.submitted.get(3))))
                .thenReturn(Observable.just(mockStoryFour));

        final List<Long> storyIds = new ArrayList<>();
        storyIds.add(mockUser.submitted.get(0));
        storyIds.add(mockUser.submitted.get(1));
        storyIds.add(mockUser.submitted.get(2));
        storyIds.add(mockUser.submitted.get(3));

        final List<Post> stories = new ArrayList<>();

        mDataManager.getPostsFromIds(storyIds).subscribe(new Action1<Post>() {
            @Override
            public void call(Post story) {
                stories.add(story);
            }
        });

        Assert.assertEquals(4, stories.size());

        Assert.assertTrue(stories.contains(mockStoryOne));
        Assert.assertTrue(stories.contains(mockStoryTwo));
        Assert.assertTrue(stories.contains(mockStoryThree));
        Assert.assertTrue(stories.contains(mockStoryFour));
    }

    @Test
    public void shouldGetUserStories() throws Exception {
        User mockUser = MockModelsUtil.createMockUser();
        when(mMockHackerNewsService.getUser(any(String.class)))
                .thenReturn(Observable.just(mockUser));
        Post mockStoryOne = MockModelsUtil.createMockStoryWithId(mockUser.submitted.get(0));
        Post mockStoryTwo = MockModelsUtil.createMockStoryWithId(mockUser.submitted.get(1));
        Post mockStoryThree = MockModelsUtil.createMockStoryWithId(mockUser.submitted.get(2));
        Post mockStoryFour = MockModelsUtil.createMockStoryWithId(mockUser.submitted.get(3));

        when(mMockHackerNewsService.getStoryItem(String.valueOf(mockUser.submitted.get(0))))
                .thenReturn(Observable.just(mockStoryOne));
        when(mMockHackerNewsService.getStoryItem(String.valueOf(mockUser.submitted.get(1))))
                .thenReturn(Observable.just(mockStoryTwo));
        when(mMockHackerNewsService.getStoryItem(String.valueOf(mockUser.submitted.get(2))))
                .thenReturn(Observable.just(mockStoryThree));
        when(mMockHackerNewsService.getStoryItem(String.valueOf(mockUser.submitted.get(3))))
                .thenReturn(Observable.just(mockStoryFour));

        final List<Post> userStories = new ArrayList<>();

        mDataManager.getUserPosts(mockUser.id).subscribe(new Action1<Post>() {
            @Override
            public void call(Post story) {
                userStories.add(story);
            }
        });

        Assert.assertEquals(4, userStories.size());

        Assert.assertTrue(userStories.contains(mockStoryOne));
        Assert.assertTrue(userStories.contains(mockStoryTwo));
        Assert.assertTrue(userStories.contains(mockStoryThree));
        Assert.assertTrue(userStories.contains(mockStoryFour));
    }

}
