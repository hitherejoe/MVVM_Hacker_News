package com.hitherejoe.hackernews;

import com.hitherejoe.hackernews.data.DataManager;
import com.hitherejoe.hackernews.data.local.DatabaseHelper;
import com.hitherejoe.hackernews.data.local.PreferencesHelper;
import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.data.model.User;
import com.hitherejoe.hackernews.data.remote.HackerNewsService;
import com.hitherejoe.hackernews.util.DefaultConfig;
import com.hitherejoe.hackernews.util.MockModelsUtil;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
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
    private Post mStory;
    private Boolean mDoesBookmarkExist;

    @Before
    public void setUp() {
        mMockHackerNewsService = mock(HackerNewsService.class);
        DatabaseHelper databaseHelper = new DatabaseHelper(RuntimeEnvironment.application);
        PreferencesHelper preferencesHelper = new PreferencesHelper(RuntimeEnvironment.application);
        mDataManager = new DataManager(mMockHackerNewsService,
                databaseHelper,
                preferencesHelper,
                Schedulers.immediate());
    }

    @Test
    public void shouldAddBookmark() throws Exception {
        Post mockStory = MockModelsUtil.createMockStory();
        mDataManager.addBookmark(RuntimeEnvironment.application, mockStory).subscribe(new Action1<Post>() {
            @Override
            public void call(Post story) {
                mStory = story;
            }
        });
        Assert.assertEquals(mockStory, mStory);

        final List<Post> storyList = new ArrayList<>();

        mDataManager.getBookmarks().subscribe(new Action1<Post>() {
            @Override
            public void call(Post story) {
                storyList.add(story);
            }
        });

        Assert.assertEquals(1, storyList.size());
        Assert.assertEquals(mockStory, storyList.get(0));
    }

    @Test
    public void shouldRemoveBookmark() throws Exception {
        Post mockStory = MockModelsUtil.createMockStory();
        mDataManager.addBookmark(RuntimeEnvironment.application, mockStory).subscribe(new Action1<Post>() {
            @Override
            public void call(Post story) {
                mStory = story;
            }
        });
        Assert.assertEquals(mockStory, mStory);

        mDataManager.deleteBookmark(RuntimeEnvironment.application,mockStory).subscribe();

        final List<Post> storyList = new ArrayList<>();

        mDataManager.getBookmarks().subscribe(new Action1<Post>() {
            @Override
            public void call(Post story) {
                storyList.add(story);
            }
        });

        Assert.assertEquals(0, storyList.size());
    }

    @Test
    public void shouldReturnBookmarkExists() throws Exception {
        Post mockStory = MockModelsUtil.createMockStory();
        mDataManager.addBookmark(RuntimeEnvironment.application, mockStory).subscribe(new Action1<Post>() {
            @Override
            public void call(Post story) {
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
        Post mockStory = MockModelsUtil.createMockStory();

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
        Post mockStoryOne = MockModelsUtil.createMockStory();
        Post mockStoryTwo = MockModelsUtil.createMockStory();
        Post mockStoryThree = MockModelsUtil.createMockStory();
        mDataManager.addBookmark(RuntimeEnvironment.application, mockStoryOne).subscribe();
        mDataManager.addBookmark(RuntimeEnvironment.application, mockStoryTwo).subscribe();
        mDataManager.addBookmark(RuntimeEnvironment.application, mockStoryThree).subscribe();

        final List<Post> storyList = new ArrayList<>();

        mDataManager.getBookmarks().subscribe(new Action1<Post>() {
            @Override
            public void call(Post story) {
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

    //TODO: COMMENT TESTS

}
