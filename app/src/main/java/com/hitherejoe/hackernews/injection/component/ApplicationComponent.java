package com.hitherejoe.hackernews.injection.component;

import android.app.Application;

import com.hitherejoe.hackernews.data.DataManager;
import com.hitherejoe.hackernews.injection.module.ApplicationModule;
import com.hitherejoe.hackernews.ui.activity.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(MainActivity mainActivity);

    Application application();
    DataManager dataManager();
}