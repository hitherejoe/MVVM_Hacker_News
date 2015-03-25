package com.hitherejoe.hackernews.data.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hitherejoe.hackernews.data.model.Story;

import rx.Observable;
import rx.Subscriber;

public class DatabaseHelper {

    private DbOpenHelper mDatabaseOpenHelper;

    public DatabaseHelper(Context context) {
        mDatabaseOpenHelper = new DbOpenHelper(context);
    }

    public SQLiteDatabase getReadableDatabase() {
        return mDatabaseOpenHelper.getReadableDatabase();
    }

    public Observable<Void> deleteBookmark(final Story story) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
                db.delete(Db.BookmarkTable.TABLE_NAME, Db.BookmarkTable.COLUMN_ID + " = ?",
                        new String[]{String.valueOf(story.id)});
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Story> getBookmarkedStories() {
        return Observable.create(new Observable.OnSubscribe<Story>() {
            @Override
            public void call(Subscriber<? super Story> subscriber) {
                SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
                Cursor bookmarkCursor =
                        db.rawQuery("SELECT * FROM " + Db.BookmarkTable.TABLE_NAME, null);
                while (bookmarkCursor.moveToNext()) {
                    subscriber.onNext(Db.BookmarkTable.parseCursor(bookmarkCursor));
                }
                bookmarkCursor.close();
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Story> bookmarkStory(final Story story) {
        return Observable.create(new Observable.OnSubscribe<Story>() {
            @Override
            public void call(Subscriber<? super Story> subscriber) {
                SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
                db.insertOrThrow(Db.BookmarkTable.TABLE_NAME, null, Db.BookmarkTable.toContentValues(story));
                subscriber.onNext(story);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Boolean> doesBookmarkExist(final Story story) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
                Cursor bookmarkCursor =
                        db.rawQuery("SELECT * FROM " + Db.BookmarkTable.TABLE_NAME +
                                " WHERE " + Db.BookmarkTable.COLUMN_ID + " = ?"
                                , new String[]{String.valueOf(story.id)});
                subscriber.onNext(bookmarkCursor.moveToNext());
                bookmarkCursor.close();
                subscriber.onCompleted();
            }
        });
    }

}
