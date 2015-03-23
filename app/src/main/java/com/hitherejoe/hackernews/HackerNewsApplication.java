package com.hitherejoe.hackernews;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.hitherejoe.hackernews.data.DataManager;

import rx.schedulers.Schedulers;

public class HackerNewsApplication extends Application {

    private static HackerNewsApplication sHackerNewsApplication;
    private DataManager mDataManager;
    private Tracker mAnalyticsTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        sHackerNewsApplication = this;
        mDataManager = new DataManager(this, Schedulers.io());
    }

    @Override
    public void onTerminate() {
        sHackerNewsApplication = null;
        super.onTerminate();
    }

    public static HackerNewsApplication get() {
        return sHackerNewsApplication;
    }

    public DataManager getDataManager() { return mDataManager; }

    public synchronized Tracker getAnalyticsTrackerTracker() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        if (mAnalyticsTracker == null) mAnalyticsTracker = analytics.newTracker(R.xml.app_tracker);
        return mAnalyticsTracker;
    }
}
