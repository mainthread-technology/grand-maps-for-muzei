package technology.mainthread.apps.grandmaps.data.model;

import com.google.android.apps.muzei.api.Artwork;

public class UpdateArtResponse {

    private final Artwork artwork;
    private final long nextUpdateTime;

    public Artwork getArtwork() {
        return artwork;
    }

    public long getNextUpdateTime() {
        return nextUpdateTime;
    }

    private UpdateArtResponse(Builder builder) {
        artwork = builder.artwork;
        nextUpdateTime = builder.nextUpdateTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(UpdateArtResponse copy) {
        Builder builder = new Builder();
        builder.artwork = copy.artwork;
        builder.nextUpdateTime = copy.nextUpdateTime;
        return builder;
    }

    public static final class Builder {
        private Artwork artwork;
        private long nextUpdateTime;

        private Builder() {
        }

        public Builder artwork(Artwork val) {
            artwork = val;
            return this;
        }

        public Builder nextUpdateTime(long val) {
            nextUpdateTime = val;
            return this;
        }

        public UpdateArtResponse build() {
            return new UpdateArtResponse(this);
        }
    }
}
