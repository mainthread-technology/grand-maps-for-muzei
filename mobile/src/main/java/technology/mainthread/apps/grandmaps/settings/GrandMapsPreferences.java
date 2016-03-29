package technology.mainthread.apps.grandmaps.settings;

import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import technology.mainthread.apps.grandmaps.R;
import technology.mainthread.apps.grandmaps.data.model.RefreshType;

@Singleton
public class GrandMapsPreferences {

    private final SharedPreferences preferences;
    private final Resources resources;

    @Inject
    public GrandMapsPreferences(SharedPreferences preferences, Resources resources) {
        this.preferences = preferences;
        this.resources = resources;
    }

    public @RefreshType String getRefreshType() {
        String type = preferences.getString(resources.getString(R.string.key_type), RefreshType.FEATURED);
        return RefreshType.RANDOM.equals(type) ? RefreshType.RANDOM : RefreshType.FEATURED;
    }

    public long getNextRandomUpdateTime() {
        int refreshFrequencyHours = Integer.parseInt(preferences.getString(resources.getString(R.string.key_frequency),
                resources.getString(R.string.default_refresh_frequency)));

        return System.currentTimeMillis() + refreshFrequencyHours * 60 * 60 * 1000;
    }

    public String getClientId() {
        String keyClientId = resources.getString(R.string.key_client_id);
        String id = preferences.getString(keyClientId, null);
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
            preferences.edit().putString(keyClientId, id).apply();
        }

        return id;
    }

    public boolean onlyUpdateOnWifi() {
        return preferences.getBoolean(resources.getString(R.string.key_wifi), false);
    }

}
