package ru.mirea.pavlovve.yandexdriver;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class App extends Application {

    private final String MAPKIT_API_KEY =
            "ed683dce-2b5e-4236-a95d-c4caa2e81bb7";

    @Override
    public void onCreate() {
        super.onCreate();

        MapKitFactory.setApiKey(MAPKIT_API_KEY);
    }
}