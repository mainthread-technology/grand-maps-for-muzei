package technology.mainthread.apps.grandmaps;

import javax.inject.Singleton;

import dagger.Component;
import technology.mainthread.apps.grandmaps.data.GrandMapsApiModule;

@Singleton
@Component(modules = {GrandMapsAppModule.class, GrandMapsApiModule.class})
public interface GrandMapsComponent extends GrandMapsGraph {

    final class Initializer {
        public static GrandMapsComponent init(GrandMapsApp app) {
            return DaggerGrandMapsComponent.builder()
                    .grandMapsAppModule(new GrandMapsAppModule(app))
                    .grandMapsApiModule(new GrandMapsApiModule(app.getResources()))
                    .build();
        }

        private Initializer() {
        } // No instances.
    }

}
