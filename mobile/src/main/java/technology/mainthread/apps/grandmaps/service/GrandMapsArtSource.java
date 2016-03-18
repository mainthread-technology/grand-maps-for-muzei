package technology.mainthread.apps.grandmaps.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import com.google.android.apps.muzei.api.UserCommand;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import retrofit2.Response;
import technology.mainthread.apps.grandmaps.BuildConfig;
import technology.mainthread.apps.grandmaps.ConnectivityHelper;
import technology.mainthread.apps.grandmaps.GrandMapsApp;
import technology.mainthread.apps.grandmaps.R;
import technology.mainthread.apps.grandmaps.RefreshType;
import technology.mainthread.apps.grandmaps.data.GrandMapsApi;
import technology.mainthread.apps.grandmaps.data.GrandMapsResponse;
import technology.mainthread.apps.grandmaps.settings.GrandMapsPreferences;
import timber.log.Timber;

/**
 * Muzei Grand map art source
 * DO NOT RENAME, REFACTOR, OR MOVE THE PACKAGE OF THIS CLASS.
 * IT WILL REMOVE THE USERS MUZEI SELECTION OF US!!
 */
public class GrandMapsArtSource extends RemoteMuzeiArtSource {

    private static final String SOURCE_NAME = GrandMapsArtSource.class.getSimpleName();
    private static final String PREF_SCHEDULED_UPDATE_TIME_MILLIS = "scheduled_update_time_millis";

    private static final int COMMAND_ID_SHARE = 1;
    private static final int COMMAND_ID_DEBUG_INFO = 51;

    // Distribute update requests over a period of 5 minutes.
    private static final int MAX_JITTER_MILLIS = 5 * 60 * 1000;
    private static final int DEFAULT_REFRESH_TIME = 7200000;

    @Inject
    GrandMapsPreferences preferences;
    @Inject
    GrandMapsApi grandMapsApi;
    @Inject
    ConnectivityHelper connectivityHelper;

    public GrandMapsArtSource() {
        super(SOURCE_NAME);
    }

    public static Intent getGrandMapArtSourceIntent(Context context) {
        return new Intent(context, GrandMapsArtSource.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GrandMapsApp.get(this).inject(this);

        List<UserCommand> commands = new ArrayList<>();

        if (BuildConfig.DEBUG) {
            // Displays the next scheduled update time.
            commands.add(new UserCommand(COMMAND_ID_DEBUG_INFO, "Update Info"));
        }

        commands.add(new UserCommand(COMMAND_ID_SHARE, getString(R.string.action_share)));

        if (preferences.getRefreshType() == RefreshType.TYPE_RANDOM) {
            commands.add(new UserCommand(BUILTIN_COMMAND_ID_NEXT_ARTWORK));
        }

        setUserCommands(commands);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);

        String action = intent.getAction();
        if (action != null && action.equals(RefreshType.TYPE_RANDOM.name())) {
            scheduleNext(0);
        }
    }

    @Override
    protected void onCustomCommand(int id) {
        super.onCustomCommand(id);

        switch (id) {
            case COMMAND_ID_SHARE:
                shareArtwork();
                break;
            case COMMAND_ID_DEBUG_INFO:
                displayRefreshInfo();
                break;
        }
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {
        if (reason != UPDATE_REASON_USER_NEXT && preferences.onlyUpdateOnWifi()
                && !connectivityHelper.isConnectedToWifi()) {
            Timber.d("only update on wifi, skipping");
            scheduleUpdate(System.currentTimeMillis() + DEFAULT_REFRESH_TIME);
            return;
        }

        String currentToken = (getCurrentArtwork() != null) ? getCurrentArtwork().getToken() : "";

        Response<GrandMapsResponse> response = null;
        switch (preferences.getRefreshType()) {
            case TYPE_FEATURED:
                response = grandMapsApi.getFeatured();
                break;
            case TYPE_RANDOM:
                response = grandMapsApi.getRandom(currentToken);
                break;
        }
        if (response.isSuccessful()) {
            GrandMapsResponse responseBody = response.body();
            if (responseBody == null || responseBody.getImageAddress() == null) {
                throw new RetryException();
            }

            publishArtwork(new Artwork.Builder()
                    .title(responseBody.getTitle())
                    .byline(String.format(Locale.ENGLISH, "%s, %d", responseBody.getAuthor(), responseBody.getYear()))
                    .token(responseBody.getId())
                    .imageUri(Uri.parse(responseBody.getImageAddress()))
                    .viewIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(responseBody.getReferenceAddress())))
                    .build());

            scheduleNext(responseBody.getNextUpdate() * 1000L);
        } else {
            // If server error then retry
            int statusCode = response.code();
            if (500 <= statusCode && statusCode < 600) {
                Timber.w("Network error, code: %d, message: %s", response.code(), response.message());
                throw new RetryException();
            }

            Timber.d("Wallpaper update failed, retrying in %d minutes", MAX_JITTER_MILLIS * 2 / (60 * 1000));
            scheduleUpdate(System.currentTimeMillis() + MAX_JITTER_MILLIS * 2);
        }

    }

    private void scheduleNext(long nextTimeMillis) {
        switch (preferences.getRefreshType()) {
            case TYPE_FEATURED:
                Random random = new Random();
                nextTimeMillis += random.nextInt(MAX_JITTER_MILLIS);
                break;
            case TYPE_RANDOM:
                nextTimeMillis = preferences.getNextRandomUpdateTime();
                break;
        }

        scheduleUpdate(nextTimeMillis);
    }

    private void shareArtwork() {
        Artwork currentArtwork = getCurrentArtwork();

        if (currentArtwork == null) {
            Timber.w("No current artwork, can't share.");
            displayToast(getString(R.string.error_no_map_to_share));
        } else {
            String detailUrl = currentArtwork.getViewIntent().getDataString();
            String artist = currentArtwork.getByline().replaceFirst("\\.\\s*($|\\n).*", "").trim();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_intent_extra_text, currentArtwork.getTitle().trim(), artist, detailUrl));
            shareIntent = Intent.createChooser(shareIntent, getString(R.string.share_intent_title));
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(shareIntent);
        }
    }

    private void displayRefreshInfo() {
        final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.UK);
        final long nextUpdateTimeMillis = getSharedPreferences().getLong(PREF_SCHEDULED_UPDATE_TIME_MILLIS, 0);

        displayToast("Next update time: " + dateFormat.format(new Date(nextUpdateTimeMillis)));
    }

    private void displayToast(final String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GrandMapsArtSource.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }
}
