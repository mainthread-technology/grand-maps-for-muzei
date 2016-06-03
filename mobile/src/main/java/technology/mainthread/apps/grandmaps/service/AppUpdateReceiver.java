package technology.mainthread.apps.grandmaps.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import technology.mainthread.apps.grandmaps.GrandMapsApp;
import technology.mainthread.apps.grandmaps.data.ConvertPreferences;
import timber.log.Timber;

public class AppUpdateReceiver extends BroadcastReceiver {

    @Inject
    ConvertPreferences convertPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            Timber.d("MY_PACKAGE_REPLACED");
            GrandMapsApp.get(context).inject(this);

            convertPreferences.checkAndFixPreferences();
        }
    }
}
