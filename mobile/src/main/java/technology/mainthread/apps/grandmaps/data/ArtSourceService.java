package technology.mainthread.apps.grandmaps.data;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import com.google.android.apps.muzei.api.UserCommand;

import java.util.List;

public interface ArtSourceService {

    int COMMAND_ID_SHARE = 1;
    int COMMAND_ID_DEBUG_INFO = 51;

    /**
     * Fetches the User commands for the current setting
     * @return List of {@link UserCommand}s
     */
    List<UserCommand> getUserCommands();

    /**
     * Updates the next map
     * @param reason muzei reason for updating
     * @param currentArtwork current artwork that is being shown
     * @return {@link UpdateArtResponse} - container object
     */
    UpdateArtResponse updateArt(int reason, Artwork currentArtwork) throws RemoteMuzeiArtSource.RetryException;

    /**
     * Share the passed in artwork
     * @param artwork to share
     */
    void shareArtwork(Artwork artwork);

    /**
     * Toast the next refresh information
     */
    void displayRefreshInfo();

}