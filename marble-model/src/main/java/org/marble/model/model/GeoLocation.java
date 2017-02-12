package org.marble.model.model;

public class GeoLocation {
    private double latitude;
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    private double longitude;
    
    public GeoLocation() {
        
    }
    public GeoLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public GeoLocation(twitter4j.GeoLocation geoLocation) {
        this.latitude = geoLocation.getLatitude();
        this.longitude = geoLocation.getLongitude();
    }
}
