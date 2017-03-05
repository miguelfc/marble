package org.marble.model.model;

public class Place {
    private String name;
    private String streetAddress;
    private String countryCode;
    private String id;
    private String country;
    private String placeType;
    private String url;
    private String fullName;
    private String boundingBoxType;
    private GeoLocation[][] boundingBoxCoordinates;
    private String geometryType;
    private GeoLocation[][] geometryCoordinates;
    private Place[] containedWithIn;

    public Place() {

    }

    public Place(twitter4j.Place place) {
        if (place != null) {
            this.name = place.getName();
            this.streetAddress = place.getStreetAddress();
            this.countryCode = place.getCountryCode();
            this.id = place.getId();
            this.country = place.getCountry();
            this.placeType = place.getPlaceType();
            this.url = place.getURL();
            this.fullName = place.getFullName();
            this.boundingBoxType = place.getBoundingBoxType();
            if (place.getBoundingBoxCoordinates() != null) {
                this.boundingBoxCoordinates = new GeoLocation[place.getBoundingBoxCoordinates().length][];
                for (int i = 0; i < place.getBoundingBoxCoordinates().length; i++) {
                    twitter4j.GeoLocation[] row = place.getBoundingBoxCoordinates()[i];
                    for (int j = 0; j < row.length; j++) {
                        this.boundingBoxCoordinates[i][j] = new GeoLocation(row[j]);
                    }
                }
            }

            this.geometryType = place.getGeometryType();

            if (place.getGeometryCoordinates() != null) {
                this.geometryCoordinates = new GeoLocation[place.getGeometryCoordinates().length][];
                for (int i = 0; i < place.getGeometryCoordinates().length; i++) {
                    twitter4j.GeoLocation[] row = place.getGeometryCoordinates()[i];
                    for (int j = 0; j < row.length; j++) {
                        this.geometryCoordinates[i][j] = new GeoLocation(row[j]);
                    }
                }
            }

            if (place.getContainedWithIn() != null) {
                this.containedWithIn = new Place[place.getContainedWithIn().length];
                for (int i = 0; i < place.getContainedWithIn().length; i++) {
                    this.containedWithIn[i] = new Place(place.getContainedWithIn()[i]);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBoundingBoxType() {
        return boundingBoxType;
    }

    public void setBoundingBoxType(String boundingBoxType) {
        this.boundingBoxType = boundingBoxType;
    }

    public GeoLocation[][] getBoundingBoxCoordinates() {
        return boundingBoxCoordinates;
    }

    public void setBoundingBoxCoordinates(GeoLocation[][] boundingBoxCoordinates) {
        this.boundingBoxCoordinates = boundingBoxCoordinates;
    }

    public String getGeometryType() {
        return geometryType;
    }

    public void setGeometryType(String geometryType) {
        this.geometryType = geometryType;
    }

    public GeoLocation[][] getGeometryCoordinates() {
        return geometryCoordinates;
    }

    public void setGeometryCoordinates(GeoLocation[][] geometryCoordinates) {
        this.geometryCoordinates = geometryCoordinates;
    }

    public Place[] getContainedWithIn() {
        return containedWithIn;
    }

    public void setContainedWithIn(Place[] containedWithIn) {
        this.containedWithIn = containedWithIn;
    }
}
