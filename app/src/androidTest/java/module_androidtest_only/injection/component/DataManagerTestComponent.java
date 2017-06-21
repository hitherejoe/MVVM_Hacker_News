package module_androidtest_only.injection.component;

import com.hitherejoe.mvvm_hackernews.injection.component.DataManagerComponent;
import com.hitherejoe.mvvm_hackernews.injection.scope.PerDataManager;

import dagger.Component;
import module_androidtest_only.injection.module.DataManagerTestModule;

@PerDataManager
@Component(dependencies = TestComponent.class, modules = DataManagerTestModule.class)
public interface DataManagerTestComponent extends DataManagerComponent {
}