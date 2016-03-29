package technology.mainthread.apps.grandmaps.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiArtSource;
import com.google.android.apps.muzei.api.UserCommand;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import technology.mainthread.apps.grandmaps.ConnectivityHelper;
import technology.mainthread.apps.grandmaps.data.model.GrandMapsResponse;
import technology.mainthread.apps.grandmaps.data.model.RefreshType;
import technology.mainthread.apps.grandmaps.settings.GrandMapsPreferences;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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

    private GrandMapsArtSourceService sut;

    @Before
    public void setUp() throws Exception {
        sut = new GrandMapsArtSourceService(context, resources, handler, sharedPreferences, preferences, api, connectivityHelper);

        when(preferences.getRefreshType()).thenReturn(RefreshType.FEATURED);
        when(api.getFeatured()).thenReturn(retrofitCall);
        when(api.getRandom(anyString())).thenReturn(retrofitCall);
    }

    @Test
    public void getUserCommandsIfFeatured() {
        // When
        List<UserCommand> userCommands = sut.getUserCommands();

        // Then
        assertEquals(2, userCommands.size());
        assertEquals(ArtSourceService.COMMAND_ID_DEBUG_INFO, userCommands.get(0).getId());
        assertEquals(ArtSourceService.COMMAND_ID_SHARE, userCommands.get(1).getId());
    }

    @Test
    public void getUserCommandsIfRandom() {
        // Given
        when(preferences.getRefreshType()).thenReturn(RefreshType.RANDOM);

        // When
        List<UserCommand> userCommands = sut.getUserCommands();

        // Then
        assertEquals(3, userCommands.size());
        assertEquals(ArtSourceService.COMMAND_ID_DEBUG_INFO, userCommands.get(0).getId());
        assertEquals(ArtSourceService.COMMAND_ID_SHARE, userCommands.get(1).getId());
        assertEquals(MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK, userCommands.get(2).getId());
    }

    @Test
    public void updateArt() throws Exception {
        when(retrofitCall.execute()).thenReturn(Response.success(new GrandMapsResponse()));
        sut.updateArt(1, null);
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
        verify(context).startActivity(any(Intent.class));
    }

    @Test
    public void shareArtworkWithData() {
        // Given
        Artwork artwork = new Artwork.Builder().title("title").imageUri(uri).token("token")
                .byline("byline").viewIntent(intent).build();

        // When
        sut.shareArtwork(artwork);

        // Then
        verify(context).startActivity(any(Intent.class));
    }

    @Test
    public void displayRefreshInfo() {
        // When
        sut.displayRefreshInfo();

        // Then
        verify(handler).post(any(Runnable.class));
    }
}