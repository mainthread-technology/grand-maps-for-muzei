package technology.mainthread.apps.grandmaps.service;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.ShareEvent;
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
        String uri = art.getImageUri().toString();
        answers.logShare(new ShareEvent()
                .putContentId(uri)
                .putContentName(art.getTitle())
                .putCustomAttribute("uri", uri)
        );
    }
}
