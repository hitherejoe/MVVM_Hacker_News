package com.hitherejoe.hackernews;

import android.app.Application;

import com.hitherejoe.hackernews.data.local.DatabaseHelper;
import com.hitherejoe.hackernews.data.remote.FirebaseHelper;

public class HackerNewsApplication extends Application {

    private static HackerNewsApplication sHackerNewsApplication;
    private FirebaseHelper mFireBaseHelper;
    private DatabaseHelper mDatabaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        sHackerNewsApplication = this;
        mFireBaseHelper = new FirebaseHelper(this);
        mDatabaseHelper = new DatabaseHelper(this);
    }

    @Override
    public void onTerminate() {
        sHackerNewsApplication = null;
        super.onTerminate();
    }

    public static HackerNewsApplication get() {
        return sHackerNewsApplication;
    }

    public FirebaseHelper getFireBaseHelper() {
        return mFireBaseHelper;
    }

    public DatabaseHelper getDatabaseHelper() { return mDatabaseHelper; }
}
