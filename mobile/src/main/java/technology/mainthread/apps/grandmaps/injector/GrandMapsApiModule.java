package technology.mainthread.apps.grandmaps.injector;

import android.content.res.Resources;

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
import technology.mainthread.apps.grandmaps.BuildConfig;
import technology.mainthread.apps.grandmaps.R;
import technology.mainthread.apps.grandmaps.data.GrandMapsApi;
import technology.mainthread.apps.grandmaps.settings.GrandMapsPreferences;

@Module
public class GrandMapsApiModule {

    private final Resources resources;

    public GrandMapsApiModule(Resources resources) {
        this.resources = resources;
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
                .baseUrl(resources.getString(R.string.api_endpoint))
                .client(okHttpClient)
                .build();

        return restAdapter.create(GrandMapsApi.class);
    }
}
