package technology.mainthread.apps.grandmaps.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiArtSource;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import com.google.android.apps.muzei.api.UserCommand;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Response;
import technology.mainthread.apps.grandmaps.BuildConfig;
import technology.mainthread.apps.grandmaps.R;
import technology.mainthread.apps.grandmaps.data.Clock;
import technology.mainthread.apps.grandmaps.data.ConnectivityHelper;
import technology.mainthread.apps.grandmaps.data.GrandMapsApi;
import technology.mainthread.apps.grandmaps.data.GrandMapsPreferences;
import technology.mainthread.apps.grandmaps.data.model.GrandMapsResponse;
import technology.mainthread.apps.grandmaps.data.model.RefreshType;
import technology.mainthread.apps.grandmaps.data.model.UpdateArtResponse;
import timber.log.Timber;

public class GrandMapsArtSourceService implements ArtSourceService {

    private static final String PREF_SCHEDULED_UPDATE_TIME_MILLIS = "scheduled_update_time_millis";

    // Distribute update requests over a period of 5 minutes.
    private static final int FIVE_MINS_IN_MILLI_SECONDS = 5 * 60 * 1000;
    private static final int TWELVE_HRS_IN_MILLI_SECONDS = 12 * 60 * 60 * 1000;
    private static final int DEFAULT_REFRESH_TIME = 7200000;

    private final Context context;
    private final Resources resources;
    private final Handler mainThreadHandler;
    private final GrandMapsPreferences preferences;
    private final GrandMapsApi api;
    private final ConnectivityHelper connectivityHelper;
    private final Clock clock;
    private final Random random;

    public GrandMapsArtSourceService(
            Context context,
            Resources resources,
            Handler mainThreadHandler,
            GrandMapsPreferences preferences,
            GrandMapsApi api,
            ConnectivityHelper connectivityHelper,
            Clock clock) {
        this.context = context;
        this.resources = resources;
        this.mainThreadHandler = mainThreadHandler;
        this.preferences = preferences;
        this.api = api;
        this.connectivityHelper = connectivityHelper;
        this.clock = clock;
        this.random = new Random();
    }

    @Override
    public List<UserCommand> getUserCommands() {
        List<UserCommand> commands = new ArrayList<>();

        if (BuildConfig.DEBUG) {
            // Displays the next scheduled update time.
            commands.add(new UserCommand(COMMAND_ID_DEBUG_INFO, "Update Info"));
        }

        commands.add(new UserCommand(COMMAND_ID_SHARE, resources.getString(R.string.action_share)));

        if (RefreshType.RANDOM.equals(preferences.getRefreshType())) {
            commands.add(new UserCommand(MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK));
        }
        return commands;
    }

    @Override
    public long getNewRandomUpdateTime() {
        if (RefreshType.FEATURED.equals(preferences.getRefreshType())) {
            throw new RuntimeException("Cannot request random update time if using featured mode");
        }

        return getNextUpdateTime(0L);
    }

    @Override
    public UpdateArtResponse updateArt(int reason, Artwork currentArtwork) throws RemoteMuzeiArtSource.RetryException {
        if (reason != MuzeiArtSource.UPDATE_REASON_USER_NEXT && preferences.onlyUpdateOnWifi()
                && !connectivityHelper.isConnectedToWifi()) {
            Timber.d("only update on wifi, skipping");
            return UpdateArtResponse.builder()
                    .nextUpdateTime(clock.currentTimeMillis() + DEFAULT_REFRESH_TIME)
                    .build();
        }

        try {
            Response<GrandMapsResponse> response;
            if (RefreshType.RANDOM.equals(preferences.getRefreshType())) {
                String currentToken = currentArtwork != null ? currentArtwork.getToken() : "";
                response = api.getRandom(currentToken).execute();
            } else {
                response = api.getFeatured().execute();
            }

            if (response.isSuccessful()) {
                GrandMapsResponse responseBody = response.body();
                if (responseBody == null || responseBody.getImageAddress() == null) {
                    return handleError();
                }

                preferences.resetRetryCount();

                return UpdateArtResponse.builder()
                        .artwork(convertResponseToArtwork(responseBody))
                        .nextUpdateTime(getNextUpdateTime(responseBody.getNextUpdate() * 1000L))
                        .build();
            } else {
                Timber.w("Network error, code: %d, message: %s", response.code(), response.message());
                return handleError();
            }
        } catch (IOException e) {
            Timber.e(e, "Retrofit Error");
            return handleError();
        }
    }

    @Override
    public void shareArtwork(Artwork currentArtwork) {
        if (currentArtwork == null || currentArtwork.getViewIntent() == null || currentArtwork.getByline() == null) {
            Timber.w("No current artwork, can't share.");
            displayToast(resources.getString(R.string.error_no_map_to_share));
        } else {
            String detailUrl = currentArtwork.getViewIntent().getDataString();
            String artist = currentArtwork.getByline().replaceFirst("\\.\\s*($|\\n).*", "").trim();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.share_intent_extra_text, currentArtwork.getTitle().trim(), artist, detailUrl));
            shareIntent = Intent.createChooser(shareIntent, resources.getString(R.string.share_intent_title));
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(shareIntent);
        }
    }

    @Override
    public void displayRefreshInfo(SharedPreferences sharedPreferences) {
        final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.UK);
        final long nextUpdateTimeMillis = sharedPreferences.getLong(PREF_SCHEDULED_UPDATE_TIME_MILLIS, 0);

        displayToast("Next update time: " + dateFormat.format(new Date(nextUpdateTimeMillis)));
    }

    private Artwork convertResponseToArtwork(GrandMapsResponse responseBody) {
        return new Artwork.Builder()
                .title(responseBody.getTitle())
                .byline(String.format(Locale.ENGLISH, "%s, %d", responseBody.getAuthor(), responseBody.getYear()))
                .token(responseBody.getId())
                .imageUri(Uri.parse(responseBody.getImageAddress()))
                .viewIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(responseBody.getReferenceAddress())))
                .build();
    }

    private UpdateArtResponse handleError() throws RemoteMuzeiArtSource.RetryException {
        int retryCount = preferences.getRetryCount();
        if (retryCount < 4) {
            throw new RemoteMuzeiArtSource.RetryException();
        } else {
            int nextRetryFromNow = random.nextInt(FIVE_MINS_IN_MILLI_SECONDS) * 5;
            if (retryCount > 11) {
                nextRetryFromNow = TWELVE_HRS_IN_MILLI_SECONDS * 2; // 24 hours
            } else if (retryCount > 7) {
                nextRetryFromNow = TWELVE_HRS_IN_MILLI_SECONDS;
            }

            return UpdateArtResponse.builder()
                    .nextUpdateTime(clock.currentTimeMillis() + nextRetryFromNow)
                    .build();
        }
    }

    private long getNextUpdateTime(long nextUpdateTimeMillis) {
        switch (preferences.getRefreshType()) {
            case RefreshType.FEATURED:
                nextUpdateTimeMillis += random.nextInt(FIVE_MINS_IN_MILLI_SECONDS);
                break;
            case RefreshType.RANDOM:
                nextUpdateTimeMillis = preferences.getNextRandomUpdateTime();
                break;
        }

        return nextUpdateTimeMillis;
    }

    private void displayToast(final String text) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }
        });
    }
}
