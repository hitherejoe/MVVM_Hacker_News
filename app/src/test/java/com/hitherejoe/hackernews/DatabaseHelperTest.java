package com.hitherejoe.hackernews;


import android.database.Cursor;

import com.hitherejoe.hackernews.data.local.DatabaseHelper;
import com.hitherejoe.hackernews.data.local.Db;
import com.hitherejoe.hackernews.data.model.Post;
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

import rx.functions.Action1;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = DefaultConfig.EMULATE_SDK)
public class DatabaseHelperTest {

    private DatabaseHelper mDatabaseHelper;
    private Post mStory;
    private Boolean mDoesBookmarkExist;

    @Before
    public void setUp() {
        mDatabaseHelper = new DatabaseHelper(Robolectric.application);
    }

    @Test
    public void shouldAddBookmark() throws Exception {
        Post mockStory = MockModelsUtil.createMockStory();

        mDatabaseHelper.bookmarkStory(mockStory).subscribe(new Action1<Post>() {
            @Override
            public void call(Post story) {
                mStory = story;
            }
        });
        Assert.assertEquals(mockStory, mStory);

        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(Db.BookmarkTable.TABLE_NAME,
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

        mDatabaseHelper.bookmarkStory(mockStory).subscribe(new Action1<Post>() {
            @Override
            public void call(Post story) {
                mStory = story;
            }
        });

        mDatabaseHelper.doesBookmarkExist(mockStory).subscribe(new Action1<Boolean>() {
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

        mDatabaseHelper.doesBookmarkExist(mockStory).subscribe(new Action1<Boolean>() {
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

        mDatabaseHelper.bookmarkStory(mockStory).subscribe(new Action1<Post>() {
            @Override
            public void call(Post story) {
                mStory = story;
            }
        });

        Cursor addBookmarkCursor = mDatabaseHelper.getReadableDatabase().query(Db.BookmarkTable.TABLE_NAME,
                null,
                Db.BookmarkTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(mockStory.id)},
                null, null, null);
        addBookmarkCursor.moveToFirst();
        Post addResult = Db.BookmarkTable.parseCursor(addBookmarkCursor);
        Assert.assertEquals(mockStory, addResult);
        addBookmarkCursor.close();

        mDatabaseHelper.deleteBookmark(mockStory).subscribe();

        Cursor removeBookmarkCursor = mDatabaseHelper.getReadableDatabase().query(Db.BookmarkTable.TABLE_NAME,
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
        mDatabaseHelper.bookmarkStory(mockStoryOne).subscribe();
        mDatabaseHelper.bookmarkStory(mockStoryTwo).subscribe();
        mDatabaseHelper.bookmarkStory(mockStoryThree).subscribe();

        mDatabaseHelper.getBookmarkedStories().subscribe(new Action1<Post>() {
            @Override
            public void call(Post story) {
                storyList.add(story);
            }
        });

        Assert.assertTrue(storyList.contains(mockStoryOne));
        Assert.assertTrue(storyList.contains(mockStoryTwo));
        Assert.assertTrue(storyList.contains(mockStoryThree));

        List<Post> cursorResultList = new ArrayList<>();
        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(Db.BookmarkTable.TABLE_NAME,
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