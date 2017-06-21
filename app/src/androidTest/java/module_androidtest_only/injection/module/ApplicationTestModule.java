package module_androidtest_only.injection.module;

import android.app.Application;

import com.hitherejoe.mvvm_hackernews.data.DataManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import module_androidtest_only.util.TestDataManager;

/**
 * Provides application-level dependencies for an app running on a testing environment
 * This allows injecting mocks if necessary.
 */
@Module
public class ApplicationTestModule {
    private final Application mApplication;

    public ApplicationTestModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    DataManager provideDataManager() {
        return new TestDataManager(mApplication);
    }

}