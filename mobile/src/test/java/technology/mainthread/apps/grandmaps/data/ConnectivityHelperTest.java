package technology.mainthread.apps.grandmaps.data;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConnectivityHelperTest {

    @Mock
    private ConnectivityManager connectivityManager;
    @Mock
    private NetworkInfo networkInfo;

    private ConnectivityHelper sut;

    @Before
    public void setUp() throws Exception {
        sut = new ConnectivityHelper(connectivityManager);

        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
    }

    @Test
    public void isConnectedToWifiTrue() throws Exception {
        // Given
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_WIFI);
        when(networkInfo.isConnected()).thenReturn(true);

        // When
        boolean isConnected = sut.isConnectedToWifi();

        // Then
        assertTrue(isConnected);
    }

    @Test
    public void isConnectedToWifiNotConnected() throws Exception {
        // Given
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_WIFI);
        when(networkInfo.isConnected()).thenReturn(false);

        // When
        boolean isConnected = sut.isConnectedToWifi();

        // Then
        assertFalse(isConnected);
    }

    @Test
    public void isConnectedToWifiMobileNetwork() throws Exception {
        // Given
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.isConnected()).thenReturn(true);

        // When
        boolean isConnected = sut.isConnectedToWifi();

        // Then
        assertFalse(isConnected);
    }

    @Test
    public void isConnectedToWifiNoActiveNetwork() throws Exception {
        // Given
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(null);

        // When
        boolean isConnected = sut.isConnectedToWifi();

        // Then
        assertFalse(isConnected);
    }
}
