package technology.mainthread.apps.grandmaps.data;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.inject.Inject;

public class ConnectivityHelper {

    private final ConnectivityManager connectivityManager;

    @Inject
    public ConnectivityHelper(ConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;
    }

    public boolean isConnectedToWifi() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI
                && activeNetwork.isConnected();
    }
}
