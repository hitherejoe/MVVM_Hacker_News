package com.hitherejoe.module_androidtest_only.util;

import android.content.Context;

import com.hitherejoe.module_androidtest_only.injection.component.DaggerDataManagerTestComponent;
import com.hitherejoe.module_androidtest_only.injection.component.TestComponent;
import com.hitherejoe.module_androidtest_only.injection.module.DataManagerTestModule;
import com.hitherejoe.mvvm_hackernews.HackerNewsApplication;
import com.hitherejoe.mvvm_hackernews.data.DataManager;
import com.hitherejoe.mvvm_hackernews.data.remote.HackerNewsService;

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
                .dataManagerTestModule(new DataManagerTestModule())
                .build()
                .inject(this);
    }

    public HackerNewsService getWatchTowerService() {
        return mHackerNewsService;
    }

}