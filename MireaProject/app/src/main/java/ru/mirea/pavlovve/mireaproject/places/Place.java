package ru.mirea.pavlovve.mireaproject.places;

import com.yandex.mapkit.geometry.Point;

public final class Place {

    private final String id;
    private final String title;
    private final String address;
    private final String description;
    private final double latitude;
    private final double longitude;

    public Place(
            String id,
            String title,
            String address,
            String description,
            double latitude,
            double longitude) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public Point getPoint() {
        return new Point(latitude, longitude);
    }
}
