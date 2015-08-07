package technology.mainthread.apps.grandmaps.data;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

public class CrashlyticsTree extends Timber.Tree {

    @Override
    public void e(String message, Object... args) {
        logToCrashlytics(new RuntimeException("Non-Fatal Error Log"), message, args);
    }

    @Override
    public void e(Throwable throwable, String message, Object... args) {
        logToCrashlytics(throwable, message, args);
    }

    private void logToCrashlytics(Throwable throwable, String message, Object... args) {
        Crashlytics.log(String.format(message, args));
        Crashlytics.logException(throwable);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {

    }
}
