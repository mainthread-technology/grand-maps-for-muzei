package technology.mainthread.apps.grandmaps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Map;

import javax.inject.Inject;

import timber.log.Timber;

public class AppUpdateReceiver extends BroadcastReceiver {

    @Inject
    SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        GrandMapsApp.get(context).inject(this);

        Timber.d("On app updated");

        String keyFrequency = context.getResources().getString(R.string.key_frequency);
        if (preferences.contains(keyFrequency)) {
            Map<String, ?> keys = preferences.getAll();

            // Convert frequency preference value to string
            for (Map.Entry<String, ?> entry : keys.entrySet()) {

                if (entry.getKey().equals(keyFrequency)
                        && entry.getValue().getClass().equals(Integer.class)) {
                    int value = (Integer) entry.getValue();
                    preferences.edit()
                            .remove(keyFrequency)
                            .putString(keyFrequency, Integer.toString(value))
                            .commit();
                    break;
                }
            }
        }
    }
}
