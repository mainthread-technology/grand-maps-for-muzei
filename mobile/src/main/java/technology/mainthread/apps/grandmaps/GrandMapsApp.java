package technology.mainthread.apps.grandmaps;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import technology.mainthread.apps.grandmaps.data.ConvertPreferences;
import technology.mainthread.apps.grandmaps.data.CrashlyticsTree;
import technology.mainthread.apps.grandmaps.injector.GrandMapsComponent;
import timber.log.Timber;

public class GrandMapsApp extends Application {

    @Inject
    ConvertPreferences convertPreferences;

    private GrandMapsComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = GrandMapsComponent.Initializer.init(this);
        component.inject(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashlyticsTree());
        }

        convertPreferences.checkAndFixPreferences();
    }

    public static GrandMapsComponent get(Context context) {
        return ((GrandMapsApp) context.getApplicationContext()).component;
    }

}
