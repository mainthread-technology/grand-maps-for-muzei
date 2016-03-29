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
}
