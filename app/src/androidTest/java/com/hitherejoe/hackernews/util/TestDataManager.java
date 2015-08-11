package com.hitherejoe.hackernews.util;

import android.content.Context;

import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.data.DataManager;
import com.hitherejoe.hackernews.data.remote.HackerNewsService;
import com.hitherejoe.hackernews.injection.component.DaggerDataManagerTestComponent;
import com.hitherejoe.hackernews.injection.component.TestComponent;
import com.hitherejoe.hackernews.injection.module.DataManagerTestModule;

/**
 * Extension of DataManager to be used on a testing environment.
 * It uses DataManagerTestComponent to inject dependencies that are different to the
 * normal runtime ones. e.g. mock objects etc.
 * It also exposes some helpers like the DatabaseHelper or the Retrofit service that are helpful
 * during testing.
 */
public class TestDataManager extends DataManager {

    public TestDataManager(Context context) {
        super(context);
    }

    @Override
    protected void injectDependencies(Context context) {
        TestComponent testComponent = (TestComponent)
                HackerNewsApplication.get(context).getComponent();
        DaggerDataManagerTestComponent.builder()
                .testComponent(testComponent)
                .dataManagerTestModule(new DataManagerTestModule(context))
                .build()
                .inject(this);
    }

    public HackerNewsService getWatchTowerService() {
        return mHackerNewsService;
    }

}