package technology.mainthread.apps.grandmaps;

import technology.mainthread.apps.grandmaps.service.GrandMapsArtSource;
import technology.mainthread.apps.grandmaps.settings.SettingsActivity;

public interface GrandMapsGraph {

    void inject(GrandMapsArtSource grandMapsArtSource);

    void inject(SettingsActivity settingsActivity);
}
