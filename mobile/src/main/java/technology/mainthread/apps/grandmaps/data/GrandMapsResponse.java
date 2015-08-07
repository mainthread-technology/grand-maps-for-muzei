package technology.mainthread.apps.grandmaps.data;

public class GrandMapsResponse {

    private String Id;
    private String Title;
    private String Author;
    private int Year;
    private String ImageAddress;
    private String ReferenceAddress;
    private long NextUpdate;

    public String getId() {
        return Id;
    }

    public String getTitle() {
        return Title;
    }

    public String getAuthor() {
        return Author;
    }

    public int getYear() {
        return Year;
    }

    public String getImageAddress() {
        return ImageAddress;
    }

    public String getReferenceAddress() {
        return ReferenceAddress;
    }

    public long getNextUpdate() {
        return NextUpdate;
    }
}
