package technology.mainthread.apps.grandmaps.injector;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import technology.mainthread.apps.grandmaps.GrandMapsApp;

@Module
public class GrandMapsAppModule {

    private final GrandMapsApp application;

    public GrandMapsAppModule(GrandMapsApp application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    Resources provideApplicationResources() {
        return application.getResources();
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    ConnectivityManager provideConnectivityManger() {
        return (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

}
