package technology.mainthread.apps.grandmaps.data;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.Map;

import javax.inject.Inject;

import technology.mainthread.apps.grandmaps.R;

public class ConvertPreferences {

    private static final String KEY_PREF_CHECKED = "KEY_PREF_CHECKED";

    private final Resources resources;
    private final SharedPreferences preferences;

    @Inject
    public ConvertPreferences(Resources resources, SharedPreferences preferences) {
        this.resources = resources;
        this.preferences = preferences;
    }

    public void checkAndFixPreferences() {
        if (!preferences.getBoolean(KEY_PREF_CHECKED, false)) {
            String keyFrequency = resources.getString(R.string.key_frequency);
            @SuppressLint("CommitPrefEdits")
            SharedPreferences.Editor editor = preferences.edit();
            if (preferences.contains(keyFrequency)) {
                Map<String, ?> keys = preferences.getAll();

                for (Map.Entry<String, ?> entry : keys.entrySet()) {
                    if (entry.getKey().equals(keyFrequency)
                            && entry.getValue().getClass().equals(Integer.class)) {

                        // Convert frequency preference value to string
                        int value = (Integer) entry.getValue();
                        editor.remove(keyFrequency)
                                .putString(keyFrequency, Integer.toString(value));
                        break;
                    }
                }
            }
            editor.putBoolean(KEY_PREF_CHECKED, true).apply();
        }
    }

}
