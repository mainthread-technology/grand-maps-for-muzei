package technology.mainthread.apps.grandmaps.data;

import android.content.SharedPreferences;
import android.content.res.Resources;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import technology.mainthread.apps.grandmaps.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GrandMapsPreferencesTest {

    private static final String KEY_FREQUENCY = "key_frequency";
    private static final String DEFAULT_REFRESH_FREQUENCY = "24";
    private static final String KEY_CLIENT_ID = "key_client_id";
    private static final String KEY_WIFI = "key_wifi";
    private static final String KEY_RETRY = "key_retry";

    @Mock
    private SharedPreferences preferences;
    @Mock
    private Resources resources;
    @Mock
    private Clock clock;
    @Mock
    private SharedPreferences.Editor editor;

    private GrandMapsPreferences sut;

    @Before
    public void setUp() throws Exception {

        when(resources.getString(R.string.key_frequency)).thenReturn(KEY_FREQUENCY);
        when(resources.getString(R.string.default_refresh_frequency)).thenReturn(DEFAULT_REFRESH_FREQUENCY);
        when(resources.getString(R.string.key_client_id)).thenReturn(KEY_CLIENT_ID);
        when(resources.getString(R.string.key_wifi)).thenReturn(KEY_WIFI);
        when(resources.getString(R.string.key_retry)).thenReturn(KEY_RETRY);
        when(preferences.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);
        when(editor.remove(anyString())).thenReturn(editor);
        when(editor.putInt(anyString(), anyInt())).thenReturn(editor);
        when(clock.currentTimeMillis()).thenReturn(0L);

        sut = new GrandMapsPreferences(preferences, resources, clock);
    }

    @Test
    public void getNextUpdateTime() throws Exception {
        // given
        when(preferences.getString(KEY_FREQUENCY, DEFAULT_REFRESH_FREQUENCY)).thenReturn("123");

        // when
        long result = sut.getNextUpdateTime();

        // then
        long expected = 123L * 60L * 60L * 1000L;
        assertEquals(expected, result);
    }

    @Test
    public void onlyUpdateOnWifiTrue() throws Exception {
        // given
        when(preferences.getBoolean(KEY_WIFI, false)).thenReturn(true);

        // when
        boolean result = sut.onlyUpdateOnWifi();

        // then
        assertTrue(result);
    }

    @Test
    public void onlyUpdateOnWifiFalse() throws Exception {
        // given
        when(preferences.getBoolean(KEY_WIFI, false)).thenReturn(false);

        // when
        boolean result = sut.onlyUpdateOnWifi();

        // then
        assertFalse(result);
    }

    @Test
    public void getRetryCount() throws Exception {
        // given
        when(preferences.getInt(KEY_RETRY, 0)).thenReturn(2);

        // when
        int result = sut.getRetryCount();

        // then
        verify(preferences).edit();
        verify(editor).putInt(KEY_RETRY, 3);
        verify(editor).apply();
        assertEquals(3, result);
    }

    @Test
    public void resetRetryCountContainsKey() throws Exception {
        // given
        when(preferences.contains(KEY_RETRY)).thenReturn(true);

        // when
        sut.resetRetryCount();

        // then
        verify(preferences).edit();
        verify(editor).remove(KEY_RETRY);
        verify(editor).apply();
    }

    @Test
    public void resetRetryCountDoesNotContainKey() throws Exception {
        // given
        when(preferences.contains(KEY_RETRY)).thenReturn(false);

        // when
        sut.resetRetryCount();

        // then
        verify(preferences, never()).edit();
        verify(editor, never()).remove(KEY_RETRY);
        verify(editor, never()).apply();
    }
}