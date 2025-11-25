package com.example.myapplication.data.model;

public class GpsData {
    private double latitude;
    private double longitude;
    private long timestamp;
    private float accuracy;

    public GpsData(double latitude, double longitude, long timestamp, float accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.accuracy = accuracy;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public long getTimestamp() { return timestamp; }
    public float getAccuracy() { return accuracy; }
}
