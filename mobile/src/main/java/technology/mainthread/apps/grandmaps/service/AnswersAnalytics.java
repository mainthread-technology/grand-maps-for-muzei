package technology.mainthread.apps.grandmaps.service;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.apps.muzei.api.Artwork;

public class AnswersAnalytics implements Analytics {

    private Answers answers;

    public AnswersAnalytics(Answers answers) {
        this.answers = answers;
    }

    @Override
    public void artUpdated() {
        answers.logCustom(new CustomEvent(ART_UPDATED));
    }

    @Override
    public void artShared(Artwork art) {
        answers.logCustom(new CustomEvent(ART_SHARED)
                .putCustomAttribute("title", art.getTitle())
                .putCustomAttribute("uri", art.getImageUri() != null ? art.getImageUri().toString() : "")
        );
    }
}
