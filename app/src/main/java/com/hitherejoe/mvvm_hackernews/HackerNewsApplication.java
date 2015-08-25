package com.hitherejoe.mvvm_hackernews;

import android.app.Application;
import android.content.Context;

import com.hitherejoe.mvvm_hackernews.injection.component.ApplicationComponent;
import com.hitherejoe.mvvm_hackernews.injection.component.DaggerApplicationComponent;
import com.hitherejoe.mvvm_hackernews.injection.module.ApplicationModule;

import timber.log.Timber;


public class HackerNewsApplication extends Application {

    ApplicationComponent mApplicationComponent;

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

}
