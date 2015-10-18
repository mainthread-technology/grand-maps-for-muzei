package technology.mainthread.apps.grandmaps.data;

import android.content.res.Resources;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import technology.mainthread.apps.grandmaps.BuildConfig;
import technology.mainthread.apps.grandmaps.R;
import technology.mainthread.apps.grandmaps.settings.GrandMapsPreferences;

@Module
public class GrandMapsApiModule {

    private final Resources resources;

    public GrandMapsApiModule(Resources resources) {
        this.resources = resources;
    }

    @Provides
    @Singleton
    GrandMapsApi provideGrandMapsService(final GrandMapsPreferences preferences) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(resources.getString(R.string.api_endpoint))
                .setLogLevel(BuildConfig.DEBUG
                        ? RestAdapter.LogLevel.FULL
                        : RestAdapter.LogLevel.NONE)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        // Add the API key to the request headers.
                        request.addHeader("Authentication", resources.getString(R.string.api_key));
                        request.addHeader("X-Client-ID", preferences.getClientId());
                    }
                })
                .build();

        return restAdapter.create(GrandMapsApi.class);
    }
}
