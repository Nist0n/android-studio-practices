package ru.mirea.pavlovve.mireaproject;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.BoundingBox;
import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.Arrays;
import java.util.List;

import ru.mirea.pavlovve.mireaproject.places.Place;
import ru.mirea.pavlovve.mireaproject.places.PlacesAdapter;

public class PlacesFragment extends Fragment implements PlacesAdapter.OnPlaceClickListener {

    private boolean mapReady;
    private MapView mapView;
    private Map map;
    private MapObjectCollection placemarksCollection;
    private TextView detailTitle;
    private TextView detailAddress;
    private TextView detailDescription;
    private PlacesAdapter adapter;
    private List<Place> places;
    private ImageProvider iconProvider;

    private final MapObjectTapListener placemarkTapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
            Object data = mapObject.getUserData();
            if (data instanceof Place) {
                selectPlace((Place) data, false);
                return true;
            }
            return false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (BuildConfig.MAPKIT_API_KEY.isEmpty()) {
            Toast.makeText(requireContext(), R.string.mapkit_key_missing, Toast.LENGTH_LONG).show();
            mapReady = false;
            return inflater.inflate(R.layout.fragment_places_placeholder, container, false);
        }
        mapReady = true;
        return inflater.inflate(R.layout.fragment_places, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!mapReady) {
            return;
        }

        places = buildSamplePlaces();

        detailTitle = view.findViewById(R.id.textPlaceDetailTitle);
        detailAddress = view.findViewById(R.id.textPlaceDetailAddress);
        detailDescription = view.findViewById(R.id.textPlaceDetailDescription);
        mapView = view.findViewById(R.id.mapView);
        FloatingActionButton fabFit = view.findViewById(R.id.fabFitBounds);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerPlaces);

        iconProvider = ImageProvider.fromResource(requireContext(), R.drawable.ic_map_pin);

        map = mapView.getMapWindow().getMap();
        placemarksCollection = map.getMapObjects().addCollection();

        for (Place place : places) {
            PlacemarkMapObject placemark = placemarksCollection.addPlacemark();
            placemark.setGeometry(place.getPoint());
            IconStyle iconStyle = new IconStyle();
            iconStyle.setAnchor(new PointF(0.5f, 1f));
            iconStyle.setScale(1.2f);
            placemark.setIcon(iconProvider, iconStyle);
            placemark.setUserData(place);
            placemark.addTapListener(placemarkTapListener);
        }

        fitAllPlacesOnMap(false);

        fabFit.setOnClickListener(v -> fitAllPlacesOnMap(true));

        adapter = new PlacesAdapter(this);
        adapter.setPlaces(places);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        detailTitle.setText(R.string.places_hint_select);
        detailAddress.setText("");
        detailDescription.setText("");
    }

    private List<Place> buildSamplePlaces() {
        return Arrays.asList(
                new Place(
                        "1",
                        getString(R.string.place_1_title),
                        getString(R.string.place_1_address),
                        getString(R.string.place_1_desc),
                        55.7657,
                        37.6087),
                new Place(
                        "2",
                        getString(R.string.place_2_title),
                        getString(R.string.place_2_address),
                        getString(R.string.place_2_desc),
                        55.7522,
                        37.5835),
                new Place(
                        "3",
                        getString(R.string.place_3_title),
                        getString(R.string.place_3_address),
                        getString(R.string.place_3_desc),
                        55.7413,
                        37.6205),
                new Place(
                        "4",
                        getString(R.string.place_4_title),
                        getString(R.string.place_4_address),
                        getString(R.string.place_4_desc),
                        55.6700,
                        37.4800));
    }

    /**
     * Доп. функция карты: камера на весь набор меток (bounding box).
     */
    private void fitAllPlacesOnMap(boolean animated) {
        if (places == null || places.isEmpty()) {
            return;
        }
        double minLat = Double.POSITIVE_INFINITY;
        double maxLat = Double.NEGATIVE_INFINITY;
        double minLon = Double.POSITIVE_INFINITY;
        double maxLon = Double.NEGATIVE_INFINITY;
        for (Place p : places) {
            Point pt = p.getPoint();
            minLat = Math.min(minLat, pt.getLatitude());
            maxLat = Math.max(maxLat, pt.getLatitude());
            minLon = Math.min(minLon, pt.getLongitude());
            maxLon = Math.max(maxLon, pt.getLongitude());
        }
        Point southWest = new Point(minLat, minLon);
        Point northEast = new Point(maxLat, maxLon);
        BoundingBox box = new BoundingBox(southWest, northEast);
        Geometry geometry = Geometry.fromBoundingBox(box);
        CameraPosition position = map.cameraPosition(geometry);
        Animation animation = animated
                ? new Animation(Animation.Type.SMOOTH, 1.0f)
                : new Animation(Animation.Type.LINEAR, 0.05f);
        map.move(position, animation, null);
    }

    @Override
    public void onPlaceClick(Place place) {
        selectPlace(place, true);
    }

    private void selectPlace(Place place, boolean moveCamera) {
        detailTitle.setText(place.getTitle());
        detailAddress.setText(place.getAddress());
        detailDescription.setText(place.getDescription());
        adapter.setSelectedId(place.getId());

        if (moveCamera) {
            map.move(
                    new CameraPosition(place.getPoint(), 16.0f, 0.0f, 0.0f),
                    new Animation(Animation.Type.SMOOTH, 0.8f),
                    null);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mapReady || mapView == null) {
            return;
        }
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        if (mapReady && mapView != null) {
            mapView.onStop();
            MapKitFactory.getInstance().onStop();
        }
        super.onStop();
    }
}
