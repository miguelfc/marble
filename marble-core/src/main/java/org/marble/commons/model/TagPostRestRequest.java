package org.marble.commons.model;

public class TagPostRestRequest extends RestRequest {

    private String user;
    private Integer polarity;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getPolarity() {
        return polarity;
    }

    public void setPolarity(Integer polarity) {
        this.polarity = polarity;
    }

}
