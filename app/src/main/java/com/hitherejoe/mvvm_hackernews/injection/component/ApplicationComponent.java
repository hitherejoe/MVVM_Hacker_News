package com.hitherejoe.mvvm_hackernews.injection.component;

import android.app.Application;

import com.hitherejoe.mvvm_hackernews.data.DataManager;
import com.hitherejoe.mvvm_hackernews.injection.module.ApplicationModule;
import com.hitherejoe.mvvm_hackernews.view.activity.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(MainActivity mainActivity);

    Application application();
    DataManager dataManager();
}