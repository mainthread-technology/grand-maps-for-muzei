package technology.mainthread.apps.grandmaps.data.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({RefreshType.FEATURED, RefreshType.RANDOM})
public @interface RefreshType {
    String FEATURED = "TYPE_FEATURED";
    String RANDOM = "TYPE_RANDOM";
}
