package technology.mainthread.apps.grandmaps.service;

import com.google.android.apps.muzei.api.Artwork;

public interface Analytics {

    String ART_UPDATED = "ART_UPDATED";
    String ART_SHARED = "ART_SHARED";

    void artUpdated();

    void artShared(Artwork art);

}
