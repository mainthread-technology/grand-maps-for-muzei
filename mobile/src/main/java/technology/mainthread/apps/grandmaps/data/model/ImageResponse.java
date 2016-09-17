package technology.mainthread.apps.grandmaps.data.model;

public class ImageResponse {

    private String title;
    private String author;
    private int year;
    private String imageUrl;
    private String referenceUrl;

    private ImageResponse(Builder builder) {
        title = builder.title;
        author = builder.author;
        year = builder.year;
        imageUrl = builder.imageUrl;
        referenceUrl = builder.referenceAddress;
    }

    public static Builder builder() {
        return new Builder();
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

    public String getImageUrl() {
        return imageUrl;
    }

    public String getReferenceUrl() {
        return referenceUrl;
    }

    public static final class Builder {
        private String title;
        private String author;
        private int year;
        private String imageUrl;
        private String referenceAddress;

        private Builder() {
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

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder referenceUrl(String referenceUrl) {
            this.referenceAddress = referenceUrl;
            return this;
        }

        public ImageResponse build() {
            return new ImageResponse(this);
        }
    }
}
