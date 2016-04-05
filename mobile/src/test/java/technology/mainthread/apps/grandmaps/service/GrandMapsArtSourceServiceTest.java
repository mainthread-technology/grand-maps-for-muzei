package technology.mainthread.apps.grandmaps.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiArtSource;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import com.google.android.apps.muzei.api.UserCommand;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import technology.mainthread.apps.grandmaps.BuildConfig;
import technology.mainthread.apps.grandmaps.data.Clock;
import technology.mainthread.apps.grandmaps.data.ConnectivityHelper;
import technology.mainthread.apps.grandmaps.data.GrandMapsApi;
import technology.mainthread.apps.grandmaps.data.model.GrandMapsResponse;
import technology.mainthread.apps.grandmaps.data.model.RefreshType;
import technology.mainthread.apps.grandmaps.data.model.UpdateArtResponse;
import technology.mainthread.apps.grandmaps.data.GrandMapsPreferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Uri.class, Intent.class, System.class, GrandMapsArtSourceService.class})
public class GrandMapsArtSourceServiceTest {

    @Mock
    private Context context;
    @Mock
    private Resources resources;
    @Mock
    private SharedPreferences sharedPreferences;
    @Mock
    private GrandMapsPreferences preferences;
    @Mock
    private GrandMapsApi api;
    @Mock
    private ConnectivityHelper connectivityHelper;
    @Mock
    private Call<GrandMapsResponse> retrofitCall;
    @Mock
    private Handler handler;
    @Mock
    private Uri uri;
    @Mock
    private Intent intent;
    @Mock
    private Clock clock;

    // Stub
    private final GrandMapsResponse apiResponse = GrandMapsResponse.builder()
            .title("Title")
            .author("author")
            .id("id")
            .imageAddress("image")
            .referenceAddress("ref")
            .nextUpdate(120)
            .build();
    private Artwork artwork;

    private GrandMapsArtSourceService sut;

    @Before
    public void setUp() throws Exception {
        // Uri
        PowerMockito.mockStatic(Uri.class);
        PowerMockito.when(Uri.class, "parse", anyString()).thenReturn(uri);
        // Intent
        PowerMockito.whenNew(Intent.class).withArguments(anyString()).thenReturn(intent);
        PowerMockito.mockStatic(Intent.class);
        PowerMockito.when(Intent.class, "createChooser", eq(intent), anyString()).thenReturn(intent);

        when(preferences.getRefreshType()).thenReturn(RefreshType.FEATURED);
        when(api.getFeatured()).thenReturn(retrofitCall);
        when(api.getRandom(anyString())).thenReturn(retrofitCall);
        when(intent.getDataString()).thenReturn("data string");
        when(clock.currentTimeMillis()).thenReturn(0L);

        artwork = new Artwork.Builder()
                .title("title")
                .token("token")
                .byline("byline")
                .imageUri(uri)
                .viewIntent(intent)
                .build();

        sut = new GrandMapsArtSourceService(context, resources, handler, preferences, api, connectivityHelper, clock);
    }

    @Test
    public void getUserCommandsIfFeatured() {
        // When
        List<UserCommand> userCommands = sut.getUserCommands();

        // Then
        assertEquals(BuildConfig.DEBUG ? 2 : 1, userCommands.size());
        if (BuildConfig.DEBUG) {
            assertEquals(ArtSourceService.COMMAND_ID_DEBUG_INFO, userCommands.get(0).getId());
            assertEquals(ArtSourceService.COMMAND_ID_SHARE, userCommands.get(1).getId());
        } else {
            assertEquals(ArtSourceService.COMMAND_ID_SHARE, userCommands.get(0).getId());
        }
    }

    @Test
    public void getUserCommandsIfRandom() {
        // Given
        when(preferences.getRefreshType()).thenReturn(RefreshType.RANDOM);

        // When
        List<UserCommand> userCommands = sut.getUserCommands();

        // Then
        assertEquals(BuildConfig.DEBUG ? 3 : 2, userCommands.size());
        if (BuildConfig.DEBUG) {
            assertEquals(ArtSourceService.COMMAND_ID_DEBUG_INFO, userCommands.get(0).getId());
            assertEquals(ArtSourceService.COMMAND_ID_SHARE, userCommands.get(1).getId());
            assertEquals(MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK, userCommands.get(2).getId());
        } else {
            assertEquals(ArtSourceService.COMMAND_ID_SHARE, userCommands.get(0).getId());
            assertEquals(MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK, userCommands.get(1).getId());
        }
    }

    @Test
    public void getNewRandomUpdateTime() {
        // Given
        when(preferences.getNextRandomUpdateTime()).thenReturn(123L);
        when(preferences.getRefreshType()).thenReturn(RefreshType.RANDOM);

        // When
        long retval = sut.getNewRandomUpdateTime();

        // Then
        assertEquals(123L, retval);
    }

    @Test(expected = RuntimeException.class)
    public void getNewRandomUpdateTimeInFeaturedMode() {
        // Given
        when(preferences.getRefreshType()).thenReturn(RefreshType.FEATURED);

        // When
        sut.getNewRandomUpdateTime();
    }

    @Test
    public void updateArtFeaturedIsSuccessful() throws Exception {
        // Given
        when(retrofitCall.execute()).thenReturn(Response.success(apiResponse));

        // When
        UpdateArtResponse artResponse = sut.updateArt(MuzeiArtSource.UPDATE_REASON_OTHER, artwork);

        // Then
        verify(api).getFeatured();
        verify(preferences).resetRetryCount();
        verifyArtResponse(artResponse);
        assertTrue(artResponse.getNextUpdateTime() > 0L);
    }

    @Test
    public void updateArtRandomIsSuccessful() throws Exception {
        // Given
        long nextRandomUpdateTime = 123L;
        when(preferences.getNextRandomUpdateTime()).thenReturn(nextRandomUpdateTime);
        when(preferences.getRefreshType()).thenReturn(RefreshType.RANDOM);
        when(retrofitCall.execute()).thenReturn(Response.success(apiResponse));

        // When
        UpdateArtResponse artResponse = sut.updateArt(MuzeiArtSource.UPDATE_REASON_OTHER, artwork);

        // Then
        verify(api).getRandom(artwork.getToken());
        verify(preferences).resetRetryCount();
        verifyArtResponse(artResponse);
        assertEquals(nextRandomUpdateTime, artResponse.getNextUpdateTime());
    }

    @Test(expected = RemoteMuzeiArtSource.RetryException.class)
    public void updateArtThrowsWhenImageUriIsNull() throws Exception {
        // Given
        when(preferences.getRefreshType()).thenReturn(RefreshType.FEATURED);
        when(preferences.getRetryCount()).thenReturn(0);
        when(retrofitCall.execute()).thenReturn(Response.success(GrandMapsResponse.builder().build()));

        // When
        sut.updateArt(MuzeiArtSource.UPDATE_REASON_OTHER, artwork);
    }

    @Test(expected = RemoteMuzeiArtSource.RetryException.class)
    public void updateArtThrowsWhen0Retries() throws Exception {
        // Given
        when(preferences.getRefreshType()).thenReturn(RefreshType.FEATURED);
        when(preferences.getRetryCount()).thenReturn(0);
        Response<GrandMapsResponse> error = Response.error(500, ResponseBody.create(MediaType.parse("text/plain"), ""));
        when(retrofitCall.execute()).thenReturn(error);

        // When
        sut.updateArt(MuzeiArtSource.UPDATE_REASON_OTHER, artwork);
    }

    @Test(expected = RemoteMuzeiArtSource.RetryException.class)
    public void updateArtThrowsWhen3Retries() throws Exception {
        // Given
        when(preferences.getRefreshType()).thenReturn(RefreshType.FEATURED);
        when(preferences.getRetryCount()).thenReturn(3);
        Response<GrandMapsResponse> error = Response.error(400, ResponseBody.create(MediaType.parse("text/plain"), ""));
        when(retrofitCall.execute()).thenReturn(error);

        // When
        sut.updateArt(MuzeiArtSource.UPDATE_REASON_OTHER, artwork);
    }

    @Test
    public void updateArtDoesNotThrowAndSchedulesUpdateMoreThan5MinsInFuture() throws Exception {
        // Given
        when(preferences.getRefreshType()).thenReturn(RefreshType.FEATURED);
        when(preferences.getRetryCount()).thenReturn(5);
        Response<GrandMapsResponse> error = Response.error(404, ResponseBody.create(MediaType.parse("text/plain"), ""));
        when(retrofitCall.execute()).thenReturn(error);

        // When
        UpdateArtResponse artResponse = sut.updateArt(MuzeiArtSource.UPDATE_REASON_OTHER, artwork);

        // Then
        int twentyFiveMins = 25 * 60 * 1000;
        assertTrue(artResponse.getNextUpdateTime() > 0);
        assertTrue(artResponse.getNextUpdateTime() < twentyFiveMins);
        assertNull(artResponse.getArtwork());
    }

    @Test
    public void updateArtDoesNotThrowAndSchedulesUpdate12HoursInFuture() throws Exception {
        // Given
        when(preferences.getRefreshType()).thenReturn(RefreshType.FEATURED);
        when(preferences.getRetryCount()).thenReturn(8);
        Response<GrandMapsResponse> error = Response.error(404, ResponseBody.create(MediaType.parse("text/plain"), ""));
        when(retrofitCall.execute()).thenReturn(error);

        // When
        UpdateArtResponse artResponse = sut.updateArt(MuzeiArtSource.UPDATE_REASON_OTHER, artwork);

        // Then
        int twelveHours = 12 * 60 * 60 * 1000;
        assertEquals(twelveHours, artResponse.getNextUpdateTime());
        assertNull(artResponse.getArtwork());
    }

    @Test
    public void updateArtDoesNotThrowAndSchedulesUpdate24HoursInFuture() throws Exception {
        // Given
        when(preferences.getRefreshType()).thenReturn(RefreshType.FEATURED);
        when(preferences.getRetryCount()).thenReturn(12);
        Response<GrandMapsResponse> error = Response.error(404, ResponseBody.create(MediaType.parse("text/plain"), ""));
        when(retrofitCall.execute()).thenReturn(error);

        // When
        UpdateArtResponse artResponse = sut.updateArt(MuzeiArtSource.UPDATE_REASON_OTHER, artwork);

        // Then
        int twentyFourHours = 24 * 60 * 60 * 1000;
        assertEquals(twentyFourHours, artResponse.getNextUpdateTime());
        assertNull(artResponse.getArtwork());
    }

    @Test
    public void shareArtworkIfNull() {
        // When
        sut.shareArtwork(null);

        // Then
        verify(handler).post(any(Runnable.class));
    }

    @Test
    public void shareArtworkEmpty() {
        // When
        sut.shareArtwork(new Artwork.Builder().build());

        // Then
        verify(context, never()).startActivity(any(Intent.class));
    }

    @Test
    public void shareArtworkWithData() throws Exception {
        // When
        sut.shareArtwork(artwork);

        // Then
        verifyNew(Intent.class).withArguments(anyString());
        verify(context).startActivity(intent);
    }

    @Test
    public void displayRefreshInfo() {
        // When
        sut.displayRefreshInfo(sharedPreferences);

        // Then
        verify(handler).post(any(Runnable.class));
    }

    private void verifyArtResponse(UpdateArtResponse artResponse) {
        assertEquals(apiResponse.getTitle(), artResponse.getArtwork().getTitle());
        assertEquals(apiResponse.getId(), artResponse.getArtwork().getToken());
    }
}