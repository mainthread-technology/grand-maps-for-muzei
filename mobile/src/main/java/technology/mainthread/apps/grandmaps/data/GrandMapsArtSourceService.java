package technology.mainthread.apps.grandmaps.data;

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
import technology.mainthread.apps.grandmaps.ConnectivityHelper;
import technology.mainthread.apps.grandmaps.R;
import technology.mainthread.apps.grandmaps.RefreshType;
import technology.mainthread.apps.grandmaps.settings.GrandMapsPreferences;
import timber.log.Timber;

public class GrandMapsArtSourceService implements ArtSourceService {

    private static final String PREF_SCHEDULED_UPDATE_TIME_MILLIS = "scheduled_update_time_millis";

    // Distribute update requests over a period of 5 minutes.
    private static final int MAX_JITTER_MILLIS = 5 * 60 * 1000;
    private static final int DEFAULT_REFRESH_TIME = 7200000;

    private final Context context;
    private final Resources resources;
    private final Handler mainThreadHandler;
    private final SharedPreferences sharedPreferences;
    private final GrandMapsPreferences preferences;
    private final GrandMapsApi api;
    private final ConnectivityHelper connectivityHelper;

    public GrandMapsArtSourceService(
            Context context,
            Resources resources,
            Handler mainThreadHandler,
            SharedPreferences sharedPreferences,
            GrandMapsPreferences preferences,
            GrandMapsApi api,
            ConnectivityHelper connectivityHelper) {
        this.context = context;
        this.resources = resources;
        this.mainThreadHandler = mainThreadHandler;
        this.sharedPreferences = sharedPreferences;
        this.preferences = preferences;
        this.api = api;
        this.connectivityHelper = connectivityHelper;
    }

    @Override
    public List<UserCommand> getUserCommands() {
        List<UserCommand> commands = new ArrayList<>();

        if (BuildConfig.DEBUG) {
            // Displays the next scheduled update time.
            commands.add(new UserCommand(COMMAND_ID_DEBUG_INFO, "Update Info"));
        }

        commands.add(new UserCommand(COMMAND_ID_SHARE, resources.getString(R.string.action_share)));

        if (preferences.getRefreshType() == RefreshType.TYPE_RANDOM) {
            commands.add(new UserCommand(MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK));
        }
        return commands;
    }

    @Override
    public UpdateArtResponse updateArt(int reason, Artwork currentArtwork) throws RemoteMuzeiArtSource.RetryException {
        if (reason != MuzeiArtSource.UPDATE_REASON_USER_NEXT && preferences.onlyUpdateOnWifi()
                && !connectivityHelper.isConnectedToWifi()) {
            Timber.d("only update on wifi, skipping");
            return UpdateArtResponse.Builder()
                    .nextUpdateTime(System.currentTimeMillis() + DEFAULT_REFRESH_TIME)
                    .build();
        }

        try {
            Response<GrandMapsResponse> response = null;
            switch (preferences.getRefreshType()) {
                case TYPE_FEATURED:
                    response = api.getFeatured().execute();
                    break;
                case TYPE_RANDOM:
                    String currentToken = currentArtwork != null ? currentArtwork.getToken() : "";
                    response = api.getRandom(currentToken).execute();
                    break;
            }

            if (response.isSuccessful()) {
                GrandMapsResponse responseBody = response.body();
                if (responseBody == null || responseBody.getImageAddress() == null) {
                    throw new RemoteMuzeiArtSource.RetryException(); // TODO: backoff?
                }

                Artwork artwork = new Artwork.Builder()
                        .title(responseBody.getTitle())
                        .byline(String.format(Locale.ENGLISH, "%s, %d", responseBody.getAuthor(), responseBody.getYear()))
                        .token(responseBody.getId())
                        .imageUri(Uri.parse(responseBody.getImageAddress()))
                        .viewIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(responseBody.getReferenceAddress())))
                        .build();

                return UpdateArtResponse.Builder()
                        .artwork(artwork)
                        .nextUpdateTime(getNextUpdateTime(responseBody.getNextUpdate() * 1000L))
                        .build();
            } else {
                // If server error then retry
                int statusCode = response.code();
                if (500 <= statusCode && statusCode < 600) {
                    Timber.w("Network error, code: %d, message: %s", response.code(), response.message());
                    throw new RemoteMuzeiArtSource.RetryException();
                }

                // TODO: implement a backoff
                Timber.d("Wallpaper update failed, retrying in %d minutes", MAX_JITTER_MILLIS * 2 / (60 * 1000));
                return UpdateArtResponse.Builder()
                        .nextUpdateTime(System.currentTimeMillis() + MAX_JITTER_MILLIS * 2)
                        .build();
            }
        } catch (IOException e) {
            Timber.e(e, "IOException");
            throw new RemoteMuzeiArtSource.RetryException();
        }
    }

    @Override
    public void shareArtwork(Artwork currentArtwork) {
        if (currentArtwork == null) {
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
    public void displayRefreshInfo() {
        final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.UK);
        final long nextUpdateTimeMillis = sharedPreferences.getLong(PREF_SCHEDULED_UPDATE_TIME_MILLIS, 0);

        displayToast("Next update time: " + dateFormat.format(new Date(nextUpdateTimeMillis)));
    }

    private long getNextUpdateTime(long nextUpdateTimeMillis) {
        switch (preferences.getRefreshType()) {
            case TYPE_FEATURED:
                Random random = new Random();
                nextUpdateTimeMillis += random.nextInt(MAX_JITTER_MILLIS);
                break;
            case TYPE_RANDOM:
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