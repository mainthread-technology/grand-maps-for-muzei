package technology.mainthread.apps.grandmaps.data;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GrandMapsApi {

    @GET("/v1/maps/featured")
    Response<GrandMapsResponse> getFeatured();

    @GET("/v1/maps/random/{id}")
    Response<GrandMapsResponse> getRandom(@Path("id") String id);

}