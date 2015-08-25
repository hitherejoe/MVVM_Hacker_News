package com.hitherejoe.mvvm_hackernews.injection.module;

import com.hitherejoe.mvvm_hackernews.data.remote.HackerNewsService;
import com.hitherejoe.mvvm_hackernews.data.remote.RetrofitHelper;
import com.hitherejoe.mvvm_hackernews.injection.scope.PerDataManager;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Provide dependencies to the DataManager, mainly Helper classes and Retrofit services.
 */
@Module
public class DataManagerModule {

    public DataManagerModule() {

    }

    @Provides
    @PerDataManager
    HackerNewsService provideHackerNewsService() {
        return new RetrofitHelper().newHackerNewsService();
    }

    @Provides
    @PerDataManager
    Scheduler provideSubscribeScheduler() {
        return Schedulers.io();
    }
}