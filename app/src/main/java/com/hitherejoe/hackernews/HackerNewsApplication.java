package com.hitherejoe.hackernews;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.hitherejoe.hackernews.data.local.DatabaseHelper;
import com.hitherejoe.hackernews.data.local.PreferencesHelper;
import com.hitherejoe.hackernews.data.remote.FirebaseHelper;

public class HackerNewsApplication extends Application {

    private static HackerNewsApplication sHackerNewsApplication;
    private FirebaseHelper mFireBaseHelper;
    private DatabaseHelper mDatabaseHelper;
    private PreferencesHelper mPreferencesHelper;
    private Tracker mAnalyticsTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        sHackerNewsApplication = this;
        mFireBaseHelper = new FirebaseHelper(this);
        mDatabaseHelper = new DatabaseHelper(this);
        mPreferencesHelper = new PreferencesHelper(this);
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

    public PreferencesHelper getPreferencesHelper() { return mPreferencesHelper; }

    public synchronized Tracker getAnalyticsTrackerTracker() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        if (mAnalyticsTracker == null) mAnalyticsTracker = analytics.newTracker(R.xml.app_tracker);
        return mAnalyticsTracker;
    }
}
