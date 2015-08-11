package com.hitherejoe.hackernews.injection.component;


import com.hitherejoe.hackernews.injection.module.DataManagerTestModule;
import com.hitherejoe.hackernews.injection.scope.PerDataManager;

import dagger.Component;

@PerDataManager
@Component(dependencies = TestComponent.class, modules = DataManagerTestModule.class)
public interface DataManagerTestComponent extends DataManagerComponent {
}