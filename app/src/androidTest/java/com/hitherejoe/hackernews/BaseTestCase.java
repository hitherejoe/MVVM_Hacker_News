package com.hitherejoe.hackernews;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.data.remote.HackerNewsService;

import rx.schedulers.Schedulers;

import static org.mockito.Mockito.mock;

public class BaseTestCase<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    protected HackerNewsService mHackerNewsService;

    public BaseTestCase(Class<T> cls) {
        super(cls);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //Clear Data
        HackerNewsApplication.get().getDataManager().getPreferencesHelper().clear();
        HackerNewsApplication.get().getDataManager().getDatabaseHelper().clearBookmarks().subscribe();

        //Set up data manager for tests
        mHackerNewsService = mock(HackerNewsService.class);
        HackerNewsApplication.get().getDataManager().setHackerNewsService(mHackerNewsService);
        HackerNewsApplication.get().getDataManager().setScheduler(Schedulers.immediate());
    }

}