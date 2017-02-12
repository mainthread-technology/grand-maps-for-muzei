package technology.mainthread.apps.grandmaps.view;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import de.psdev.licensesdialog.LicensesDialog;
import technology.mainthread.apps.grandmaps.BuildConfig;
import technology.mainthread.apps.grandmaps.R;
import technology.mainthread.apps.grandmaps.service.GrandMapsArtSource;

public class SettingsFragment extends PreferenceFragment {

    private int originalFrequency;
    private boolean frequencyChanged;
    private final Preference.OnPreferenceChangeListener frequencyPrefChangedListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String newValueString = (String) newValue;
            onFrequencyPreferenceChanged(preference, newValueString);
            setFrequencyChanged(Integer.parseInt(newValueString));
            return true;
        }
    };

    public static Fragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        initializePreferences();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (frequencyChanged) {
            getActivity().startService(GrandMapsArtSource.getGrandMapArtSourceIntent(getActivity(), true));
        }
    }

    private void initializePreferences() {
        ListPreference frequencyPref = (ListPreference) findPreference(getResources().getString(R.string.key_frequency));
        originalFrequency = Integer.parseInt(frequencyPref.getValue());
        onFrequencyPreferenceChanged(frequencyPref, frequencyPref.getValue());

        frequencyPref.setOnPreferenceChangeListener(frequencyPrefChangedListener);
        findPreference(getString(R.string.key_os_licences)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showLicencesDialog();
                return true;
            }
        });

        setupVersionName();
    }

    private void setupVersionName() {
        Preference buildNumber = findPreference(getResources().getString(R.string.key_build_number));
        buildNumber.setSummary(BuildConfig.VERSION_NAME);
    }

    private void onFrequencyPreferenceChanged(Preference preference, String newValue) {
        int frequency = Integer.parseInt(newValue);
        preference.setSummary(getResources().getQuantityString(R.plurals.refresh_frequency_summary, frequency, frequency));
    }

    private void setFrequencyChanged(int newValue) {
        frequencyChanged = originalFrequency != newValue;
    }

    private void showLicencesDialog() {
        if (!isRemoving()) {
            new LicensesDialog.Builder(getActivity()).setNotices(R.raw.notices).setIncludeOwnLicense(true).build().show();
        }
    }
}