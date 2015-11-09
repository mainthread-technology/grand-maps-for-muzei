package technology.mainthread.apps.grandmaps.settings;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import de.psdev.licensesdialog.LicensesDialog;
import technology.mainthread.apps.grandmaps.R;
import technology.mainthread.apps.grandmaps.RefreshType;

public class SettingsFragment extends PreferenceFragment {

    private final Preference.OnPreferenceChangeListener typePrefChangedListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            onTypePreferenceChanged((String) newValue);
            return true;
        }
    };

    private final Preference.OnPreferenceChangeListener frequencyPrefChangedListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            onFrequencyPreferenceChanged(preference, (String) newValue);
            return true;
        }
    };
    private ListPreference frequencyPref;

    public static Fragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        initializePreferences();
    }

    private void initializePreferences() {
        frequencyPref = (ListPreference) findPreference(getResources().getString(R.string.key_frequency));
        ListPreference typePref = (ListPreference) findPreference(getResources().getString(R.string.key_type));

        onTypePreferenceChanged(typePref.getValue());
        onFrequencyPreferenceChanged(frequencyPref, frequencyPref.getValue());

        typePref.setOnPreferenceChangeListener(typePrefChangedListener);
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
        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            buildNumber.setSummary(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            buildNumber.setSummary(getResources().getString(R.string.version_name_error));
        }
    }

    private void onTypePreferenceChanged(String newValue) {
        frequencyPref.setEnabled(!newValue.equals(RefreshType.TYPE_FEATURED.name()));
    }

    private void onFrequencyPreferenceChanged(Preference preference, String newValue) {
        int frequency = Integer.parseInt(newValue);
        preference.setSummary(getResources().getQuantityString(R.plurals.refresh_frequency_summary, frequency, frequency));
    }

    private void showLicencesDialog() {
        if (!isRemoving()) {
            new LicensesDialog.Builder(getActivity()).setNotices(R.raw.notices).setIncludeOwnLicense(true).build().show();
        }
    }
}