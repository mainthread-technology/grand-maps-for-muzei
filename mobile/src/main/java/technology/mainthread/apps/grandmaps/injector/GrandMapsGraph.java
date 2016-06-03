package technology.mainthread.apps.grandmaps.injector;

import technology.mainthread.apps.grandmaps.GrandMapsApp;
import technology.mainthread.apps.grandmaps.service.AppUpdateReceiver;
import technology.mainthread.apps.grandmaps.service.GrandMapsArtSource;
import technology.mainthread.apps.grandmaps.view.SettingsActivity;

public interface GrandMapsGraph {

    void inject(GrandMapsApp grandMapsApp);

    void inject(GrandMapsArtSource grandMapsArtSource);

    void inject(SettingsActivity settingsActivity);

    void inject(AppUpdateReceiver appUpdateReceiver);

}
