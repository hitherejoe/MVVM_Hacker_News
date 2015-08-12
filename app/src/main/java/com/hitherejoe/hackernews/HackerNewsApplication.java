package com.hitherejoe.hackernews;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.hitherejoe.hackernews.injection.component.ApplicationComponent;
import com.hitherejoe.hackernews.injection.component.DaggerApplicationComponent;
import com.hitherejoe.hackernews.injection.module.ApplicationModule;

import timber.log.Timber;


public class HackerNewsApplication extends Application {

    ApplicationComponent mApplicationComponent;

    private Tracker mAnalyticsTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public static HackerNewsApplication get(Context context) {
        return (HackerNewsApplication) context.getApplicationContext();
    }

    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }

    public synchronized Tracker getAnalyticsTrackerTracker() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        if (mAnalyticsTracker == null) mAnalyticsTracker = analytics.newTracker(R.xml.app_tracker);
        return mAnalyticsTracker;
    }
}
