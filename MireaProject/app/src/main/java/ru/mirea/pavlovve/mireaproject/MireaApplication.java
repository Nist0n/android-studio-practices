package ru.mirea.pavlovve.mireaproject;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class MireaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        String key = BuildConfig.MAPKIT_API_KEY;
        if (key == null || key.isEmpty()) {
            return;
        }
        MapKitFactory.setApiKey(key);
        MapKitFactory.initialize(this);
    }
}
