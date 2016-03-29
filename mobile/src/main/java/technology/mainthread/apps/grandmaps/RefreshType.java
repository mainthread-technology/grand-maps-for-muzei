package technology.mainthread.apps.grandmaps;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({RefreshType.FEATURED, RefreshType.RANDOM})
public @interface RefreshType {
    int FEATURED = 0;
    int RANDOM = 1;
}
