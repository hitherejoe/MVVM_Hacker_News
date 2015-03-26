package com.hitherejoe.hackernews;

import com.hitherejoe.hackernews.data.DataManager;
import com.hitherejoe.hackernews.data.model.Story;
import com.hitherejoe.hackernews.data.model.User;
import com.hitherejoe.hackernews.data.remote.HackerNewsService;
import com.hitherejoe.hackernews.espresso.util.DefaultConfig;
import com.hitherejoe.hackernews.util.MockModelsUtil;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
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
@Config(emulateSdk = DefaultConfig.EMULATE_SDK)
public class DataManagerTest {

    private DataManager mDataManager;
    private HackerNewsService mHackerNewsService;
    private Story mStory;
    private Boolean mDoesBookmarkExist;

    @Before
    public void setUp() {
        mDataManager = new DataManager(Robolectric.application, Schedulers.immediate());
        mHackerNewsService = mock(HackerNewsService.class);
        mDataManager.setHackerNewsService(mHackerNewsService);
    }

    @Test
    public void shouldAddBookmark() throws Exception {
        Story mockStory = MockModelsUtil.createMockStory();
        mDataManager.addBookmark(mockStory).subscribe(new Action1<Story>() {
            @Override
            public void call(Story story) {
                mStory = story;
            }
        });
        Assert.assertEquals(mockStory, mStory);

        final List<Story> storyList = new ArrayList<>();

        mDataManager.getBookmarks().subscribe(new Action1<Story>() {
            @Override
            public void call(Story story) {
                storyList.add(story);
            }
        });

        Assert.assertEquals(1, storyList.size());
        Assert.assertEquals(mockStory, storyList.get(0));
    }

    @Test
    public void shouldRemoveBookmark() throws Exception {
        Story mockStory = MockModelsUtil.createMockStory();
        mDataManager.addBookmark(mockStory).subscribe(new Action1<Story>() {
            @Override
            public void call(Story story) {
                mStory = story;
            }
        });
        Assert.assertEquals(mockStory, mStory);

        mDataManager.deleteBookmark(mockStory).subscribe();

        final List<Story> storyList = new ArrayList<>();

        mDataManager.getBookmarks().subscribe(new Action1<Story>() {
            @Override
            public void call(Story story) {
                storyList.add(story);
            }
        });

        Assert.assertEquals(0, storyList.size());
    }

    @Test
    public void shouldReturnBookmarkExists() throws Exception {
        Story mockStory = MockModelsUtil.createMockStory();
        mDataManager.addBookmark(mockStory).subscribe(new Action1<Story>() {
            @Override
            public void call(Story story) {
                mStory = story;
            }
        });
        Assert.assertEquals(mockStory, mStory);

        mDataManager.doesBookmarkExist(mockStory).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                mDoesBookmarkExist = result;
            }
        });
        Assert.assertEquals(Boolean.TRUE, mDoesBookmarkExist);
    }

    @Test
    public void shouldReturnBookmarkDoesNotExist() throws Exception {
        Story mockStory = MockModelsUtil.createMockStory();

        mDataManager.doesBookmarkExist(mockStory).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                mDoesBookmarkExist = result;
            }
        });
        Assert.assertEquals(Boolean.FALSE, mDoesBookmarkExist);
    }

    @Test
    public void shouldGetBookmarks() throws Exception {
        Story mockStoryOne = MockModelsUtil.createMockStory();
        Story mockStoryTwo = MockModelsUtil.createMockStory();
        Story mockStoryThree = MockModelsUtil.createMockStory();
        mDataManager.addBookmark(mockStoryOne).subscribe();
        mDataManager.addBookmark(mockStoryTwo).subscribe();
        mDataManager.addBookmark(mockStoryThree).subscribe();

        final List<Story> storyList = new ArrayList<>();

        mDataManager.getBookmarks().subscribe(new Action1<Story>() {
            @Override
            public void call(Story story) {
                storyList.add(story);
            }
        });
        Assert.assertEquals(3, storyList.size());
        Assert.assertTrue(storyList.contains(mockStoryOne));
        Assert.assertTrue(storyList.contains(mockStoryTwo));
        Assert.assertTrue(storyList.contains(mockStoryThree));
    }

    @Test
    public void shouldGetTopStories() throws Exception {
        User mockUser = MockModelsUtil.createMockUser();
        Story mockStoryOne = MockModelsUtil.createMockStoryWithId(mockUser.submitted.get(0));
        Story mockStoryTwo = MockModelsUtil.createMockStoryWithId(mockUser.submitted.get(1));
        Story mockStoryThree = MockModelsUtil.createMockStoryWithId(mockUser.submitted.get(2));
        Story mockStoryFour = MockModelsUtil.createMockStoryWithId(mockUser.submitted.get(3));

        when(mHackerNewsService.getStoryItem(String.valueOf(mockUser.submitted.get(0))))
                .thenReturn(Observable.just(mockStoryOne));
        when(mHackerNewsService.getStoryItem(String.valueOf(mockUser.submitted.get(1))))
                .thenReturn(Observable.just(mockStoryTwo));
        when(mHackerNewsService.getStoryItem(String.valueOf(mockUser.submitted.get(2))))
                .thenReturn(Observable.just(mockStoryThree));
        when(mHackerNewsService.getStoryItem(String.valueOf(mockUser.submitted.get(3))))
                .thenReturn(Observable.just(mockStoryFour));

        final List<Long> storyIds = new ArrayList<>();
        storyIds.add(mockUser.submitted.get(0));
        storyIds.add(mockUser.submitted.get(1));
        storyIds.add(mockUser.submitted.get(2));
        storyIds.add(mockUser.submitted.get(3));

        final List<Story> stories = new ArrayList<>();

        mDataManager.getStoriesFromIds(storyIds).subscribe(new Action1<Story>() {
            @Override
            public void call(Story story) {
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
        when(mHackerNewsService.getUser(any(String.class)))
                .thenReturn(Observable.just(mockUser));
        Story mockStoryOne = MockModelsUtil.createMockStoryWithId(mockUser.submitted.get(0));
        Story mockStoryTwo = MockModelsUtil.createMockStoryWithId(mockUser.submitted.get(1));
        Story mockStoryThree = MockModelsUtil.createMockStoryWithId(mockUser.submitted.get(2));
        Story mockStoryFour = MockModelsUtil.createMockStoryWithId(mockUser.submitted.get(3));

        when(mHackerNewsService.getStoryItem(String.valueOf(mockUser.submitted.get(0))))
                .thenReturn(Observable.just(mockStoryOne));
        when(mHackerNewsService.getStoryItem(String.valueOf(mockUser.submitted.get(1))))
                .thenReturn(Observable.just(mockStoryTwo));
        when(mHackerNewsService.getStoryItem(String.valueOf(mockUser.submitted.get(2))))
                .thenReturn(Observable.just(mockStoryThree));
        when(mHackerNewsService.getStoryItem(String.valueOf(mockUser.submitted.get(3))))
                .thenReturn(Observable.just(mockStoryFour));

        final List<Story> userStories = new ArrayList<>();

        mDataManager.getUserStories(mockUser.id).subscribe(new Action1<Story>() {
            @Override
            public void call(Story story) {
                userStories.add(story);
            }
        });

        Assert.assertEquals(4, userStories.size());

        Assert.assertTrue(userStories.contains(mockStoryOne));
        Assert.assertTrue(userStories.contains(mockStoryTwo));
        Assert.assertTrue(userStories.contains(mockStoryThree));
        Assert.assertTrue(userStories.contains(mockStoryFour));
    }

    //TODO: COMMENT TESTS

}
