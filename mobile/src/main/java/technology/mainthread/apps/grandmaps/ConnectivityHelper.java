package technology.mainthread.apps.grandmaps;

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
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }
}
