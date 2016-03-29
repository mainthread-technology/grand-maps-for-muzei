package technology.mainthread.apps.grandmaps.service;

import android.content.Context;
import android.content.Intent;

import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;

import javax.inject.Inject;

import technology.mainthread.apps.grandmaps.GrandMapsApp;
import technology.mainthread.apps.grandmaps.data.model.UpdateArtResponse;

/**
 * Muzei Grand map art source
 * DO NOT RENAME, REFACTOR, OR MOVE THE PACKAGE OF THIS CLASS.
 * IT WILL REMOVE THE USERS MUZEI SELECTION OF US!!
 */
public class GrandMapsArtSource extends RemoteMuzeiArtSource {

    public static final String ACTION_QUEUE_RANDOM_REFRESH = "action_queue_random_refresh";

    @Inject
    ArtSourceService artSourceService;

    public GrandMapsArtSource() {
        super(GrandMapsArtSource.class.getSimpleName());
    }

    public static Intent getGrandMapArtSourceIntent(Context context) {
        return new Intent(context, GrandMapsArtSource.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GrandMapsApp.get(this).inject(this);

        // Setup commands the user can perform in the Muzei app.
        setUserCommands(artSourceService.getUserCommands());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);

        // TODO: Use extra from intent?
        String action = intent.getAction();
        if (ACTION_QUEUE_RANDOM_REFRESH.equals(action)) {
            scheduleUpdate(artSourceService.getNewRandomUpdateTime());
        }
    }

    @Override
    protected void onCustomCommand(int id) {
        super.onCustomCommand(id);

        switch (id) {
            case ArtSourceService.COMMAND_ID_SHARE:
                artSourceService.shareArtwork(getCurrentArtwork());
                break;
            case ArtSourceService.COMMAND_ID_DEBUG_INFO:
                artSourceService.displayRefreshInfo(getSharedPreferences());
                break;
        }
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {
        UpdateArtResponse response = artSourceService.updateArt(reason, getCurrentArtwork());

        if (response.getArtwork() != null) {
            publishArtwork(response.getArtwork());
        }

        scheduleUpdate(response.getNextUpdateTime());
    }
}
