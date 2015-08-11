package com.hitherejoe.hackernews.injection.component;

import com.hitherejoe.hackernews.data.DataManager;
import com.hitherejoe.hackernews.injection.module.DataManagerModule;
import com.hitherejoe.hackernews.injection.scope.PerDataManager;

import dagger.Component;

@PerDataManager
@Component(dependencies = ApplicationComponent.class, modules = DataManagerModule.class)
public interface DataManagerComponent {

    void inject(DataManager dataManager);
}