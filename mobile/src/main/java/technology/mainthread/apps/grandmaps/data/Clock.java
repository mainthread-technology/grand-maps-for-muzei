package technology.mainthread.apps.grandmaps.data;

import javax.inject.Inject;

public class Clock {

    @Inject
    public Clock() {
    }

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}
