package technology.mainthread.apps.grandmaps.data;

import retrofit2.Call;
import retrofit2.http.GET;
import technology.mainthread.apps.grandmaps.data.model.ImageListResponse;

public interface GrandMapsApi {

    @GET("/")
    Call<ImageListResponse> getImages();

}