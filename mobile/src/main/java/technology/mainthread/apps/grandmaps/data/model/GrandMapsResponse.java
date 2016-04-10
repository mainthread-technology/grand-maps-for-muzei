package technology.mainthread.apps.grandmaps.data.model;

import com.squareup.moshi.Json;

public class GrandMapsResponse {

    @Json(name = "Id")
    private String id;
    @Json(name = "Title")
    private String title;
    @Json(name = "Author")
    private String author;
    @Json(name = "Year")
    private int year;
    @Json(name = "ImageAddress")
    private String imageAddress;
    @Json(name = "ReferenceAddress")
    private String referenceAddress;
    @Json(name = "NextUpdate")
    private long nextUpdate;

    private GrandMapsResponse(Builder builder) {
        id = builder.id;
        title = builder.title;
        author = builder.author;
        year = builder.year;
        imageAddress = builder.imageAddress;
        referenceAddress = builder.referenceAddress;
        nextUpdate = builder.nextUpdate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(GrandMapsResponse copy) {
        Builder builder = new Builder();
        builder.id = copy.id;
        builder.title = copy.title;
        builder.author = copy.author;
        builder.year = copy.year;
        builder.imageAddress = copy.imageAddress;
        builder.referenceAddress = copy.referenceAddress;
        builder.nextUpdate = copy.nextUpdate;
        return builder;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public String getImageAddress() {
        return imageAddress;
    }

    public String getReferenceAddress() {
        return referenceAddress;
    }

    public long getNextUpdate() {
        return nextUpdate;
    }

    public static final class Builder {
        private String id;
        private String title;
        private String author;
        private int year;
        private String imageAddress;
        private String referenceAddress;
        private long nextUpdate;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder imageAddress(String imageAddress) {
            this.imageAddress = imageAddress;
            return this;
        }

        public Builder referenceAddress(String referenceAddress) {
            this.referenceAddress = referenceAddress;
            return this;
        }

        public Builder nextUpdate(long nextUpdate) {
            this.nextUpdate = nextUpdate;
            return this;
        }

        public GrandMapsResponse build() {
            return new GrandMapsResponse(this);
        }
    }
}
