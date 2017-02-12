package technology.mainthread.apps.grandmaps.injector;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.answers.Answers;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import technology.mainthread.apps.grandmaps.service.Analytics;
import technology.mainthread.apps.grandmaps.service.AnswersAnalytics;

@Module
public class ServiceModule {

    private final Context context;

    public ServiceModule(Application application) {
        this.context = application.getApplicationContext();
    }

    @Provides
    @Singleton
    Analytics provideAnalytics() {
        return new AnswersAnalytics(Answers.getInstance());
    }

}
