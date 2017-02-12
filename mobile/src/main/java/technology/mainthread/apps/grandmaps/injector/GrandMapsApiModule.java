package technology.mainthread.apps.grandmaps.injector;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import technology.mainthread.apps.grandmaps.BuildConfig;
import technology.mainthread.apps.grandmaps.R;
import technology.mainthread.apps.grandmaps.data.Clock;
import technology.mainthread.apps.grandmaps.data.ConnectivityHelper;
import technology.mainthread.apps.grandmaps.data.GrandMapsApi;
import technology.mainthread.apps.grandmaps.data.GrandMapsPreferences;
import technology.mainthread.apps.grandmaps.service.Analytics;
import technology.mainthread.apps.grandmaps.service.ArtSourceService;
import technology.mainthread.apps.grandmaps.service.GrandMapsArtSourceService;

@Module
public class GrandMapsApiModule {

    private static final long CACHE_SIZE = 1024L * 1024L; //  1 MB
    private final Context context;
    private final Resources resources;

    public GrandMapsApiModule(Application application) {
        this.context = application.getApplicationContext();
        this.resources = application.getResources();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.cache(new Cache(context.getCacheDir(), CACHE_SIZE));

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(new HttpLoggingInterceptor());
        }

        return builder.build();
    }

    @Provides
    @Singleton
    GrandMapsApi provideGrandMapsService(OkHttpClient okHttpClient) {
        Retrofit restAdapter = new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(resources.getString(R.string.api_endpoint))
                .client(okHttpClient)
                .build();

        return restAdapter.create(GrandMapsApi.class);
    }

    @Provides
    ArtSourceService artSourceService(Handler handler,
                                      GrandMapsPreferences preferences,
                                      GrandMapsApi api,
                                      ConnectivityHelper connectivityHelper,
                                      Clock clock,
                                      Analytics analytics) {
        return new GrandMapsArtSourceService(context, resources, handler, preferences, api, connectivityHelper, clock, analytics);
    }
}
