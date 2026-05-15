package ru.mirea.pavlovve.yandexdriver;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.Error;
import com.yandex.runtime.network.NetworkError;

import java.util.ArrayList;
import java.util.List;

import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;

import com.yandex.runtime.network.RemoteError;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;

import com.yandex.runtime.image.ImageProvider;


public class MainActivity extends AppCompatActivity
        implements DrivingSession.DrivingRouteListener {

    private final Point ROUTE_START_LOCATION =
            new Point(55.5, 37.701448);

    private final Point ROUTE_END_LOCATION =
            new Point(55.670005, 37.479894);

    private final Point SCREEN_CENTER = new Point(
            (ROUTE_START_LOCATION.getLatitude()
                    + ROUTE_END_LOCATION.getLatitude()) / 2,

            (ROUTE_START_LOCATION.getLongitude()
                    + ROUTE_END_LOCATION.getLongitude()) / 2
    );

    private MapView mapView;

    private MapObjectCollection mapObjects;

    private DrivingRouter drivingRouter;

    private DrivingSession drivingSession;

    private int[] colors = {
            0xFFFF0000,
            0xFF00FF00,
            0xFF0000FF,
            0xFFFFFF00
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapKitFactory.initialize(this);

        DirectionsFactory.initialize(this);

        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapview);

        mapView.getMap().setRotateGesturesEnabled(false);

        mapView.getMap().move(
                new CameraPosition(
                        SCREEN_CENTER,
                        10.0f,
                        0.0f,
                        0.0f
                )
        );

        drivingRouter =
                DirectionsFactory.getInstance().createDrivingRouter();

        mapObjects =
                mapView.getMap().getMapObjects().addCollection();

        submitRequest();

        PlacemarkMapObject marker =
                mapView.getMap().getMapObjects().addPlacemark(
                        ROUTE_END_LOCATION
                );

        marker.addTapListener(new MapObjectTapListener() {

            @Override
            public boolean onMapObjectTap(
                    @NonNull MapObject mapObject,
                    @NonNull Point point
            ) {

                Toast.makeText(
                        getApplicationContext(),
                        "РТУ МИРЭА\nМосква, проспект Вернадского",
                        Toast.LENGTH_SHORT
                ).show();

                return true;
            }
        });
    }

    private void submitRequest() {

        DrivingOptions drivingOptions =
                new DrivingOptions();

        VehicleOptions vehicleOptions =
                new VehicleOptions();

        drivingOptions.setRoutesCount(4);

        ArrayList<RequestPoint> requestPoints =
                new ArrayList<>();

        requestPoints.add(
                new RequestPoint(
                        ROUTE_START_LOCATION,
                        RequestPointType.WAYPOINT,
                        null
                )
        );

        requestPoints.add(
                new RequestPoint(
                        ROUTE_END_LOCATION,
                        RequestPointType.WAYPOINT,
                        null
                )
        );

        drivingSession =
                drivingRouter.requestRoutes(
                        requestPoints,
                        drivingOptions,
                        vehicleOptions,
                        this
                );
    }

    @Override
    public void onDrivingRoutes(
            @NonNull List<DrivingRoute> routes
    ) {

        for (int i = 0; i < routes.size(); i++) {

            mapObjects.addPolyline(
                    routes.get(i).getGeometry()
            ).setStrokeColor(colors[i]);
        }
    }

    @Override
    public void onDrivingRoutesError(
            @NonNull Error error
    ) {

        String errorMessage = "unknown error";

        if (error instanceof RemoteError) {
            errorMessage = "remote error";
        }

        else if (error instanceof NetworkError) {
            errorMessage = "network error";
        }

        Toast.makeText(
                this,
                errorMessage,
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        MapKitFactory.getInstance().onStart();

        mapView.onStart();
    }

    @Override
    protected void onStop() {

        mapView.onStop();

        MapKitFactory.getInstance().onStop();

        super.onStop();
    }
}