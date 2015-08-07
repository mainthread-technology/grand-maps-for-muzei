package technology.mainthread.apps.grandmaps.data;

import retrofit.http.GET;
import retrofit.http.Path;

public interface GrandMapsApi {

    @GET("/v1/maps/featured")
    GrandMapsResponse getFeatured();

    @GET("/v1/maps/random/{id}")
    GrandMapsResponse getRandom(@Path("id") String id);

}