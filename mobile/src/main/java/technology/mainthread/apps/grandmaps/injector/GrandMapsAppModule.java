package technology.mainthread.apps.grandmaps.injector;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GrandMapsAppModule {

    private final Context context;
    private final Resources resources;

    public GrandMapsAppModule(Application application) {
        this.context = application.getApplicationContext();
        this.resources = application.getResources();
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return context;
    }

    @Provides
    @Singleton
    Resources provideApplicationResources() {
        return resources;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    ConnectivityManager provideConnectivityManger() {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    Handler provideMainThreadHandler() {
        return new Handler(context.getMainLooper());
    }


}
