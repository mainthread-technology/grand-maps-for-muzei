package technology.mainthread.apps.grandmaps.data;

import android.content.SharedPreferences;
import android.content.res.Resources;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;

import technology.mainthread.apps.grandmaps.R;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConvertPreferencesTest {

    private static final String KEY_FREQUENCY = "key_frequency";

    @Mock
    private Resources resources;
    @Mock
    private SharedPreferences preferences;
    @Mock
    private SharedPreferences.Editor editor;

    private ConvertPreferences sut;

    @Before
    public void setUp() throws Exception {
        sut = new ConvertPreferences(resources, preferences);

        when(resources.getString(R.string.key_frequency)).thenReturn(KEY_FREQUENCY);
        when(preferences.edit()).thenReturn(editor);
        when(editor.putBoolean(anyString(), anyBoolean())).thenReturn(editor);
        when(editor.remove(anyString())).thenReturn(editor);
    }

    @Test
    public void checkAndFixPreferencesIfAlreadyChecked() throws Exception {
        // given
        when(preferences.getBoolean("KEY_PREF_CHECKED", false)).thenReturn(true);

        // when
        sut.checkAndFixPreferences();

        // then
        verifyNoMoreInteractions(resources);
        verify(preferences, never()).contains(anyString());
    }

    @Test
    public void checkAndFixPreferencesIfNotCheckedAndDoesNotContainKey() throws Exception {
        // given
        when(preferences.getBoolean("KEY_PREF_CHECKED", false)).thenReturn(false);
        when(preferences.contains(KEY_FREQUENCY)).thenReturn(false);

        // when
        sut.checkAndFixPreferences();

        // then
        verify(resources).getString(R.string.key_frequency);
        verify(preferences).contains(KEY_FREQUENCY);
        verify(editor).putBoolean("KEY_PREF_CHECKED", true);
        verify(editor).apply();
    }

    @Test
    public void checkAndFixPreferencesIfNotCheckedAndContainsKeyAndIsInteger() throws Exception {
        // given
        when(preferences.getBoolean("KEY_PREF_CHECKED", false)).thenReturn(false);
        when(preferences.contains(KEY_FREQUENCY)).thenReturn(true);
        when(preferences.getAll()).thenAnswer(new Answer<Map<String, ?>>() {
            @Override
            public Map<String, ?> answer(InvocationOnMock invocation) throws Throwable {
                return getMap(Integer.valueOf(123));
            }
        });

        // when
        sut.checkAndFixPreferences();

        // then
        verify(resources).getString(R.string.key_frequency);
        verify(preferences).contains(KEY_FREQUENCY);
        verify(editor).remove(KEY_FREQUENCY);
        verify(editor).putString(eq(KEY_FREQUENCY), eq("123"));

        verify(editor).putBoolean("KEY_PREF_CHECKED", true);
        verify(editor).apply();
    }

    @Test
    public void checkAndFixPreferencesIfNotCheckedAndContainsKeyAndIsNotAnInteger() throws Exception {
        // given
        when(preferences.getBoolean("KEY_PREF_CHECKED", false)).thenReturn(false);
        when(preferences.contains(KEY_FREQUENCY)).thenReturn(true);
        when(preferences.getAll()).thenAnswer(new Answer<Map<String, ?>>() {
            @Override
            public Map<String, ?> answer(InvocationOnMock invocation) throws Throwable {
                return getMap("123");
            }
        });

        // when
        sut.checkAndFixPreferences();

        // then
        verify(resources).getString(R.string.key_frequency);
        verify(preferences).contains(KEY_FREQUENCY);
        verify(editor, never()).remove(KEY_FREQUENCY);
        verify(editor, never()).putString(eq(KEY_FREQUENCY), eq("123"));

        verify(editor).putBoolean("KEY_PREF_CHECKED", true);
        verify(editor).apply();
    }

    private Map<String, ?> getMap(Object value) {
        HashMap<String, Object> map = new HashMap<>(1);
        map.put(KEY_FREQUENCY, value);
        return map;
    }

}