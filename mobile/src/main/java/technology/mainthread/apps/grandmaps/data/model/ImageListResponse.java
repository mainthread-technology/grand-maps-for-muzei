package technology.mainthread.apps.grandmaps.data.model;

import java.util.List;

public class ImageListResponse {

    private List<ImageResponse> images;

    public List<ImageResponse> getImages() {
        return images;
    }

    private ImageListResponse(ImageListResponse.Builder builder) {
        images = builder.images;
    }

    public static ImageListResponse.Builder builder() {
        return new ImageListResponse.Builder();
    }

    public static final class Builder {
        private List<ImageResponse> images;

        private Builder() {
        }

        public ImageListResponse.Builder images(List<ImageResponse> images) {
            this.images = images;
            return this;
        }

        public ImageListResponse build() {
            return new ImageListResponse(this);
        }
    }
}
