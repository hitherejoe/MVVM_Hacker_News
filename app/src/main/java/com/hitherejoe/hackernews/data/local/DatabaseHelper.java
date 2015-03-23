package com.hitherejoe.hackernews.data.local;

import android.content.Context;
import static nl.qbusict.cupboard.CupboardFactory.cupboard;

import com.hitherejoe.hackernews.data.model.Bookmark;
import com.hitherejoe.hackernews.data.model.Story;
import com.hitherejoe.hackernews.data.remote.AnalyticsHelper;
import com.hitherejoe.hackernews.util.DataUtils;

import java.util.List;

public class DatabaseHelper {

    private CupboardSQLiteOpenHelper mCupboardSQLiteOpenHelper;

    public DatabaseHelper(Context context) {
        mCupboardSQLiteOpenHelper = new CupboardSQLiteOpenHelper(context);
    }

    public void bookmarkStory(Story story) {
        AnalyticsHelper.trackBookmarkAdded();
        cupboard().withDatabase(
                mCupboardSQLiteOpenHelper.getWritableDatabase()).put(DataUtils.createBookmarkObject(story));
    }

    public void deleteBookmark(Bookmark story) {
        AnalyticsHelper.trackBookmarkRemoved();
        cupboard().withDatabase(
                mCupboardSQLiteOpenHelper.getWritableDatabase()).delete(Bookmark.class, story._id);
    }

    public List<Bookmark> getBookmarkedStories() {
        return cupboard().withDatabase(
                mCupboardSQLiteOpenHelper.getWritableDatabase()).query(Bookmark.class).list();
    }

    public boolean doesBookmarkExist(Story story) {
        return cupboard().withDatabase(
                mCupboardSQLiteOpenHelper.getWritableDatabase()).get(Bookmark.class, story.id) != null;
    }

}
