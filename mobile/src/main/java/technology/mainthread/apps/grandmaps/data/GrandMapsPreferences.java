package technology.mainthread.apps.grandmaps.data;

import android.content.SharedPreferences;
import android.content.res.Resources;

import javax.inject.Inject;
import javax.inject.Singleton;

import technology.mainthread.apps.grandmaps.R;

@Singleton
public class GrandMapsPreferences {

    private final SharedPreferences preferences;
    private final Resources resources;
    private final Clock clock;

    @Inject
    public GrandMapsPreferences(SharedPreferences preferences, Resources resources, Clock clock) {
        this.preferences = preferences;
        this.resources = resources;
        this.clock = clock;
    }

    public long getNextUpdateTime() {
        long refreshFrequencyHours = Integer.parseInt(preferences.getString(resources.getString(R.string.key_frequency),
                resources.getString(R.string.default_refresh_frequency)));

        return clock.currentTimeMillis() + refreshFrequencyHours * 60L * 60L * 1000L;
    }

    public boolean onlyUpdateOnWifi() {
        return preferences.getBoolean(resources.getString(R.string.key_wifi), false);
    }

    public int getRetryCount() {
        String key = resources.getString(R.string.key_retry);
        return incrementRetry(key);
    }

    public void resetRetryCount() {
        String key = resources.getString(R.string.key_retry);
        if (preferences.contains(key)) {
            preferences.edit().remove(key).apply();
        }
    }

    private int incrementRetry(String key) {
        int current = preferences.getInt(key, 0);
        current++;
        preferences.edit().putInt(key, current).apply();
        return current;
    }

}
