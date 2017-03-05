package org.marble.model.model;

public class Scopes {

    private String[] placeIds;

    public Scopes() {
    }

    public Scopes(twitter4j.Scopes scopes) {
        if (scopes != null) {
            this.placeIds = scopes.getPlaceIds();
        }
    }

    public Scopes(final String[] placeIds) {
        this.placeIds = placeIds;
    }

    public String[] getPlaceIds() {
        return placeIds;
    }

}