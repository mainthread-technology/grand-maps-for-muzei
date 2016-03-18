package technology.mainthread.apps.grandmaps.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GrandMapsApi {

    @GET("/v1/maps/featured")
    Call<GrandMapsResponse> getFeatured();

    @GET("/v1/maps/random/{id}")
    Call<GrandMapsResponse> getRandom(@Path("id") String id);

}