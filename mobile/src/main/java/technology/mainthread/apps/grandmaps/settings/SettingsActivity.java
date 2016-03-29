package technology.mainthread.apps.grandmaps.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.apps.muzei.api.MuzeiArtSource;

import javax.inject.Inject;

import technology.mainthread.apps.grandmaps.GrandMapsApp;
import technology.mainthread.apps.grandmaps.data.model.RefreshType;
import technology.mainthread.apps.grandmaps.service.GrandMapsArtSource;

import static com.google.android.apps.muzei.api.internal.ProtocolConstants.ACTION_HANDLE_COMMAND;
import static com.google.android.apps.muzei.api.internal.ProtocolConstants.EXTRA_COMMAND_ID;

public class SettingsActivity extends AppCompatActivity {

    @Inject
    GrandMapsPreferences preferences;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GrandMapsApp.get(this).inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
            //add fragment to placeholder
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, SettingsFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        startService(getIntentWithActionAndExtras());
        super.onDestroy();
    }

    private Intent getIntentWithActionAndExtras() {
        Intent intent = GrandMapsArtSource.getGrandMapArtSourceIntent(this);
        @RefreshType String type = preferences.getRefreshType();

        switch (type) {
            case RefreshType.RANDOM:
                intent.setAction(GrandMapsArtSource.ACTION_QUEUE_RANDOM_REFRESH);
                break;
            case RefreshType.FEATURED:
            default:
                intent.setAction(ACTION_HANDLE_COMMAND).putExtra(EXTRA_COMMAND_ID, MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK);
                break;
        }

        return intent;
    }
}
