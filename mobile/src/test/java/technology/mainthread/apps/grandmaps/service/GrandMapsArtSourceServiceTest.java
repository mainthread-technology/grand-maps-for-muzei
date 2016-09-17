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

import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import technology.mainthread.apps.grandmaps.BuildConfig;
import technology.mainthread.apps.grandmaps.data.Clock;
import technology.mainthread.apps.grandmaps.data.ConnectivityHelper;
import technology.mainthread.apps.grandmaps.data.GrandMapsApi;
import technology.mainthread.apps.grandmaps.data.GrandMapsPreferences;
import technology.mainthread.apps.grandmaps.data.model.ImageListResponse;
import technology.mainthread.apps.grandmaps.data.model.ImageResponse;
import technology.mainthread.apps.grandmaps.data.model.UpdateArtResponse;

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
    private Call<ImageListResponse> retrofitCall;
    @Mock
    private Handler handler;
    @Mock
    private Uri uri;
    @Mock
    private Intent intent;
    @Mock
    private Clock clock;

    // Stub
    private final ImageResponse image = ImageResponse.builder()
            .title("title")
            .author("author")
            .imageUrl("imageUri")
            .referenceUrl("ref")
            .build();
    private final ImageListResponse apiResponse = ImageListResponse.builder().images(Collections.singletonList(image)).build();
    private Artwork artwork;

    private GrandMapsArtSourceService sut;

    @Before
    public void setUp() throws Exception {
        // Uri
        PowerMockito.mockStatic(Uri.class);
        PowerMockito.when(Uri.class, "parse", anyString()).thenReturn(uri);
        when(uri.toString()).thenReturn("imageUri");
        // Intent
        PowerMockito.whenNew(Intent.class).withArguments(anyString()).thenReturn(intent);
        PowerMockito.mockStatic(Intent.class);
        PowerMockito.when(Intent.class, "createChooser", eq(intent), anyString()).thenReturn(intent);

        when(api.getImages()).thenReturn(retrofitCall);
        when(intent.getDataString()).thenReturn("data string");
        when(clock.currentTimeMillis()).thenReturn(0L);

        artwork = new Artwork.Builder()
                .title("title")
                .byline("byline")
                .imageUri(uri)
                .viewIntent(intent)
                .build();

        sut = new GrandMapsArtSourceService(context, resources, handler, preferences, api, connectivityHelper, clock);
    }

    @Test
    public void getUserCommands() {
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
    public void getNextUpdateTime() {
        // Given
        when(preferences.getNextUpdateTime()).thenReturn(123L);

        // When
        long retval = sut.getNextUpdateTime();

        // Then
        assertEquals(123L, retval);
    }

    @Test
    public void updateArtIsSuccessful() throws Exception {
        // Given
        long nextUpdateTime = 123L;
        when(preferences.getNextUpdateTime()).thenReturn(nextUpdateTime);
        when(retrofitCall.execute()).thenReturn(Response.success(apiResponse));

        // When
        UpdateArtResponse artResponse = sut.updateArt(MuzeiArtSource.UPDATE_REASON_OTHER, artwork);

        // Then
        verify(api).getImages();
        verify(preferences).resetRetryCount();
        verifyArtResponse(artResponse);
        assertEquals(nextUpdateTime, artResponse.getNextUpdateTime());
    }

    @Test(expected = RemoteMuzeiArtSource.RetryException.class)
    public void updateArtThrowsWhenResponseIsNull() throws Exception {
        // Given
        when(preferences.getRetryCount()).thenReturn(0);
        when(retrofitCall.execute()).thenReturn(Response.<ImageListResponse>success(null));

        // When
        sut.updateArt(MuzeiArtSource.UPDATE_REASON_OTHER, artwork);
    }

    @Test(expected = RemoteMuzeiArtSource.RetryException.class)
    public void updateArtThrowsWhenImagesNull() throws Exception {
        // Given
        when(preferences.getRetryCount()).thenReturn(0);
        when(retrofitCall.execute()).thenReturn(Response.success(ImageListResponse.builder().build()));

        // When
        sut.updateArt(MuzeiArtSource.UPDATE_REASON_OTHER, artwork);
    }

    @Test(expected = RemoteMuzeiArtSource.RetryException.class)
    public void updateArtThrowsWhenImagesEmpty() throws Exception {
        // Given
        when(preferences.getRetryCount()).thenReturn(0);
        when(retrofitCall.execute()).thenReturn(Response.success(ImageListResponse.builder().images(Collections.<ImageResponse>emptyList()).build()));

        // When
        sut.updateArt(MuzeiArtSource.UPDATE_REASON_OTHER, artwork);
    }

    @Test(expected = RemoteMuzeiArtSource.RetryException.class)
    public void updateArtThrowsWhen0Retries() throws Exception {
        // Given
        when(preferences.getRetryCount()).thenReturn(0);
        Response<ImageListResponse> error = Response.error(500, ResponseBody.create(MediaType.parse("text/plain"), ""));
        when(retrofitCall.execute()).thenReturn(error);

        // When
        sut.updateArt(MuzeiArtSource.UPDATE_REASON_OTHER, artwork);
    }

    @Test(expected = RemoteMuzeiArtSource.RetryException.class)
    public void updateArtThrowsWhen3Retries() throws Exception {
        // Given
        when(preferences.getRetryCount()).thenReturn(3);
        Response<ImageListResponse> error = Response.error(400, ResponseBody.create(MediaType.parse("text/plain"), ""));
        when(retrofitCall.execute()).thenReturn(error);

        // When
        sut.updateArt(MuzeiArtSource.UPDATE_REASON_OTHER, artwork);
    }

    @Test
    public void updateArtDoesNotThrowAndSchedulesUpdateMoreThan5MinsInFuture() throws Exception {
        // Given
        when(preferences.getRetryCount()).thenReturn(5);
        Response<ImageListResponse> error = Response.error(404, ResponseBody.create(MediaType.parse("text/plain"), ""));
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
        when(preferences.getRetryCount()).thenReturn(8);
        Response<ImageListResponse> error = Response.error(404, ResponseBody.create(MediaType.parse("text/plain"), ""));
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
        when(preferences.getRetryCount()).thenReturn(12);
        Response<ImageListResponse> error = Response.error(404, ResponseBody.create(MediaType.parse("text/plain"), ""));
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
        assertEquals(image.getTitle(), artResponse.getArtwork().getTitle());
        assertEquals(image.getImageUrl(), artResponse.getArtwork().getImageUri().toString());
    }
}