package com.hitherejoe.module_androidtest_only.injection.component;


import com.hitherejoe.module_androidtest_only.injection.module.ApplicationTestModule;
import com.hitherejoe.mvvm_hackernews.injection.component.ApplicationComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends ApplicationComponent {

}
