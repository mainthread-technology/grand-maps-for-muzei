package technology.mainthread.apps.grandmaps.service;

import android.content.SharedPreferences;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import com.google.android.apps.muzei.api.UserCommand;

import java.util.List;

import technology.mainthread.apps.grandmaps.data.model.UpdateArtResponse;

public interface ArtSourceService {

    int COMMAND_ID_SHARE = 1;
    int COMMAND_ID_DEBUG_INFO = 51;

    /**
     * Fetches the User commands for the current setting
     *
     * @return List of {@link UserCommand}s
     */
    List<UserCommand> getUserCommands();

    /**
     * Gets the next update time
     *
     * @return time in milli seconds to next update
     */
    long getNextUpdateTime();

    /**
     * Updates the next map
     *
     * @param reason         muzei reason for updating
     * @return {@link UpdateArtResponse} - container object
     */
    UpdateArtResponse updateArt(int reason) throws RemoteMuzeiArtSource.RetryException;

    /**
     * Share the passed in artwork
     *
     * @param artwork to share
     */
    void shareArtwork(Artwork artwork);

    /**
     * Toast the next refresh information
     */
    void displayRefreshInfo(SharedPreferences sharedPreferences);

}
