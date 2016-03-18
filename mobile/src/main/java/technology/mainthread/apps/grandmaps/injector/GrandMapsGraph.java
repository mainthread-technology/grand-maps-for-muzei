package technology.mainthread.apps.grandmaps.injector;

import technology.mainthread.apps.grandmaps.AppUpdateReceiver;
import technology.mainthread.apps.grandmaps.service.GrandMapsArtSource;
import technology.mainthread.apps.grandmaps.settings.SettingsActivity;

public interface GrandMapsGraph {

    void inject(GrandMapsArtSource grandMapsArtSource);

    void inject(SettingsActivity settingsActivity);

    void inject(AppUpdateReceiver appUpdateReceiver);
}
