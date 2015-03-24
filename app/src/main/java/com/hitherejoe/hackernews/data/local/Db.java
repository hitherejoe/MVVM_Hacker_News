package com.hitherejoe.hackernews.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import com.hitherejoe.hackernews.data.model.Story;

public class Db {

    public Db() { }

    public static abstract class BookmarkTable {
        public static final String TABLE_NAME = "bookmarks";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_BY = "by";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_URL = "url";

        public static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY," +
                        COLUMN_BY + " TEXT NOT NULL," +
                        COLUMN_TITLE + " TEXT NOT NULL," +
                        COLUMN_SCORE + " INTEGER NOT NULL," +
                        COLUMN_URL + " TEXT NOT NULL" +
                        " ); ";

        public static ContentValues toContentValues(Story story) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, story.id);
            values.put(COLUMN_BY, story.by);
            values.put(COLUMN_TITLE, story.title);
            values.put(COLUMN_SCORE, story.score);
            values.put(COLUMN_URL, story.url);
            return values;
        }

        public static Story parseCursor(Cursor cursor) {
            Story story = new Story();
            story.id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
            story.by = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BY));
            story.title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            story.score = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
            story.url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL));
            return story;
        }
    }
}
