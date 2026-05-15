package ru.mirea.pavlovve.osmmaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.api.IMapController;

import org.osmdroid.views.overlay.Marker;

import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;

import org.osmdroid.views.overlay.ScaleBarOverlay;

import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;

    private MyLocationNewOverlay locationNewOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(
                        getApplicationContext()
                )
        );

        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);

        mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.setMultiTouchControls(true);

        mapView.setBuiltInZoomControls(true);

        IMapController mapController = mapView.getController();

        mapController.setZoom(15.0);

        GeoPoint startPoint =
                new GeoPoint(55.794229, 37.700772);

        mapController.setCenter(startPoint);

        CompassOverlay compassOverlay =
                new CompassOverlay(
                        getApplicationContext(),
                        new InternalCompassOrientationProvider(
                                getApplicationContext()
                        ),
                        mapView
                );

        compassOverlay.enableCompass();

        mapView.getOverlays().add(compassOverlay);

        final DisplayMetrics dm =
                getResources().getDisplayMetrics();

        ScaleBarOverlay scaleBarOverlay =
                new ScaleBarOverlay(mapView);

        scaleBarOverlay.setCentred(true);

        scaleBarOverlay.setScaleBarOffset(
                dm.widthPixels / 2,
                10
        );

        mapView.getOverlays().add(scaleBarOverlay);

        Marker marker = new Marker(mapView);

        marker.setPosition(
                new GeoPoint(55.794229, 37.700772)
        );

        marker.setTitle("РТУ МИРЭА");

        marker.setOnMarkerClickListener(
                new Marker.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(
                            Marker marker,
                            MapView mapView
                    ) {

                        Toast.makeText(
                                getApplicationContext(),
                                "РТУ МИРЭА",
                                Toast.LENGTH_SHORT
                        ).show();

                        return true;
                    }
                }
        );

        mapView.getOverlays().add(marker);

        checkPermissions();
    }

    private void checkPermissions() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    200
            );

        } else {

            enableLocation();
        }
    }

    private void enableLocation() {

        locationNewOverlay =
                new MyLocationNewOverlay(
                        new GpsMyLocationProvider(
                                getApplicationContext()
                        ),
                        mapView
                );

        locationNewOverlay.enableMyLocation();

        mapView.getOverlays().add(locationNewOverlay);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == 200) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                enableLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mapView.onResume();
    }

    @Override
    public void onPause() {

        mapView.onPause();

        super.onPause();
    }
}