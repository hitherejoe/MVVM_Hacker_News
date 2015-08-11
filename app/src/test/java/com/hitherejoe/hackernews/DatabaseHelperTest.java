package com.hitherejoe.hackernews;

import android.database.Cursor;

import com.hitherejoe.hackernews.data.DataManager;
import com.hitherejoe.hackernews.data.local.DatabaseHelper;
import com.hitherejoe.hackernews.data.local.Db;
import com.hitherejoe.hackernews.data.local.PreferencesHelper;
import com.hitherejoe.hackernews.data.model.Post;
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

import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = DefaultConfig.EMULATE_SDK)
public class DatabaseHelperTest {

    private DataManager mDataManager;
    private HackerNewsService mMockHackerNewsService;
    private Post mPost;
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

        mDataManager.getDatabaseHelper().bookmarkStory(mockStory).subscribe(new Action1<Post>() {
            @Override
            public void call(Post story) {
                mPost = story;
            }
        });
        Assert.assertEquals(mockStory, mPost);

        Cursor cursor = mDataManager.getDatabaseHelper().getReadableDatabase().query(Db.BookmarkTable.TABLE_NAME,
                null,
                Db.BookmarkTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(mockStory.id)},
                null, null, null);
        cursor.moveToFirst();
        Post storyResult = Db.BookmarkTable.parseCursor(cursor);
        Assert.assertEquals(mockStory, storyResult);
        cursor.close();
    }

    @Test
    public void shouldReturnBookmarkExists() throws Exception {
        Post mockStory = MockModelsUtil.createMockStory();

        mDataManager.getDatabaseHelper().bookmarkStory(mockStory).subscribe(new Action1<Post>() {
            @Override
            public void call(Post story) {
                mPost = story;
            }
        });

        mDataManager.getDatabaseHelper().doesBookmarkExist(mockStory).subscribe(new Action1<Boolean>() {
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

        mDataManager.getDatabaseHelper().doesBookmarkExist(mockStory).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                mDoesBookmarkExist = result;
            }
        });
        Assert.assertEquals(Boolean.FALSE, mDoesBookmarkExist);
    }

    @Test
    public void shouldDeleteBookmark() throws Exception {
        Post mockStory = MockModelsUtil.createMockStory();

        mDataManager.getDatabaseHelper().bookmarkStory(mockStory).subscribe(new Action1<Post>() {
            @Override
            public void call(Post story) {
                mPost = story;
            }
        });

        Cursor addBookmarkCursor = mDataManager.getDatabaseHelper().getReadableDatabase().query(Db.BookmarkTable.TABLE_NAME,
                null,
                Db.BookmarkTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(mockStory.id)},
                null, null, null);
        addBookmarkCursor.moveToFirst();
        Post addResult = Db.BookmarkTable.parseCursor(addBookmarkCursor);
        Assert.assertEquals(mockStory, addResult);
        addBookmarkCursor.close();

        mDataManager.getDatabaseHelper().deleteBookmark(mockStory).subscribe();

        Cursor removeBookmarkCursor = mDataManager.getDatabaseHelper().getReadableDatabase().query(Db.BookmarkTable.TABLE_NAME,
                null,
                Db.BookmarkTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(mockStory.id)},
                null, null, null);
        Assert.assertEquals(removeBookmarkCursor.getCount(), 0);
        removeBookmarkCursor.close();
    }

    @Test
    public void shouldGetBookmarks() throws Exception {
        Post mockStoryOne = MockModelsUtil.createMockStory();
        Post mockStoryTwo = MockModelsUtil.createMockStory();
        Post mockStoryThree = MockModelsUtil.createMockStory();

        final List<Post> storyList = new ArrayList<>();
        mDataManager.getDatabaseHelper().bookmarkStory(mockStoryOne).subscribe();
        mDataManager.getDatabaseHelper().bookmarkStory(mockStoryTwo).subscribe();
        mDataManager.getDatabaseHelper().bookmarkStory(mockStoryThree).subscribe();

        mDataManager.getDatabaseHelper().getBookmarkedStories().subscribe(new Action1<Post>() {
            @Override
            public void call(Post story) {
                storyList.add(story);
            }
        });

        Assert.assertTrue(storyList.contains(mockStoryOne));
        Assert.assertTrue(storyList.contains(mockStoryTwo));
        Assert.assertTrue(storyList.contains(mockStoryThree));

        List<Post> cursorResultList = new ArrayList<>();
        Cursor cursor = mDataManager.getDatabaseHelper().getReadableDatabase().query(Db.BookmarkTable.TABLE_NAME,
                null, null, null, null, null, null);
        Assert.assertEquals(cursor.getCount(), 3);

        while (cursor.moveToNext()) {
            cursorResultList.add(Db.BookmarkTable.parseCursor(cursor));
        }
        cursor.close();

        Assert.assertTrue(cursorResultList.contains(mockStoryOne));
        Assert.assertTrue(cursorResultList.contains(mockStoryTwo));
        Assert.assertTrue(cursorResultList.contains(mockStoryThree));
    }

}