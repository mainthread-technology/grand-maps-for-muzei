package technology.mainthread.apps.grandmaps.injector;

import javax.inject.Singleton;

import dagger.Component;
import technology.mainthread.apps.grandmaps.GrandMapsApp;
import technology.mainthread.apps.grandmaps.service.AppUpdateReceiver;
import technology.mainthread.apps.grandmaps.service.GrandMapsArtSource;
import technology.mainthread.apps.grandmaps.view.SettingsActivity;

@Singleton
@Component(modules = {GrandMapsAppModule.class, GrandMapsApiModule.class, ServiceModule.class})
public interface GrandMapsComponent {

    final class Initializer {
        public static GrandMapsComponent init(GrandMapsApp app) {
            return DaggerGrandMapsComponent.builder()
                    .grandMapsAppModule(new GrandMapsAppModule(app))
                    .grandMapsApiModule(new GrandMapsApiModule(app))
                    .serviceModule(new ServiceModule(app))
                    .build();
        }

        private Initializer() {
        } // No instances.
    }

    void inject(GrandMapsApp grandMapsApp);

    void inject(GrandMapsArtSource grandMapsArtSource);

    void inject(SettingsActivity settingsActivity);

    void inject(AppUpdateReceiver appUpdateReceiver);
}
