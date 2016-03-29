package technology.mainthread.apps.grandmaps.injector;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import technology.mainthread.apps.grandmaps.BuildConfig;
import technology.mainthread.apps.grandmaps.data.ConnectivityHelper;
import technology.mainthread.apps.grandmaps.R;
import technology.mainthread.apps.grandmaps.data.ArtSourceService;
import technology.mainthread.apps.grandmaps.data.GrandMapsApi;
import technology.mainthread.apps.grandmaps.data.GrandMapsArtSourceService;
import technology.mainthread.apps.grandmaps.settings.GrandMapsPreferences;

@Module
public class GrandMapsApiModule {

    private final Context context;
    private final Resources resources;

    public GrandMapsApiModule(Application application) {
        this.context = application.getApplicationContext();
        this.resources = application.getResources();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(final GrandMapsPreferences preferences) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(new HttpLoggingInterceptor());
        }

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("Authentication", resources.getString(R.string.api_key))
                        .addHeader("X-Client-ID", preferences.getClientId())
                        .build();

                return chain.proceed(request);
            }
        });

        return builder.build();
    }

    @Provides
    @Singleton
    GrandMapsApi provideGrandMapsService(OkHttpClient okHttpClient) {
        Retrofit restAdapter = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(resources.getString(R.string.api_endpoint))
                .client(okHttpClient)
                .build();

        return restAdapter.create(GrandMapsApi.class);
    }

    @Provides
    ArtSourceService artSourceService(Handler handler, SharedPreferences sharedPreferences, GrandMapsPreferences preferences,
                                      GrandMapsApi api, ConnectivityHelper connectivityHelper) {
        return new GrandMapsArtSourceService(context, resources, handler, sharedPreferences,
                preferences, api, connectivityHelper);
    }
}
