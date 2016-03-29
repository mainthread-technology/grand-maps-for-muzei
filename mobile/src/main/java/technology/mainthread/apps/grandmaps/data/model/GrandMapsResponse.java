package technology.mainthread.apps.grandmaps.data.model;

import com.google.gson.annotations.SerializedName;

public class GrandMapsResponse {

    @SerializedName("Id")
    private String id;
    @SerializedName("Title")
    private String title;
    @SerializedName("Author")
    private String author;
    @SerializedName("Year")
    private int year;
    @SerializedName("ImageAddress")
    private String imageAddress;
    @SerializedName("ReferenceAddress")
    private String referenceAddress;
    @SerializedName("NextUpdate")
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

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder title(String val) {
            title = val;
            return this;
        }

        public Builder author(String val) {
            author = val;
            return this;
        }

        public Builder year(int val) {
            year = val;
            return this;
        }

        public Builder imageAddress(String val) {
            imageAddress = val;
            return this;
        }

        public Builder referenceAddress(String val) {
            referenceAddress = val;
            return this;
        }

        public Builder nextUpdate(long val) {
            nextUpdate = val;
            return this;
        }

        public GrandMapsResponse build() {
            return new GrandMapsResponse(this);
        }
    }
}
