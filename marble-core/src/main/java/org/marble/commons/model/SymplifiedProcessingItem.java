package org.marble.commons.model;

import java.util.Date;

public class SymplifiedProcessingItem {
    
    private long id;
    private String text;
    private Date createdAt;
    private Integer expectedPolarity;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Integer getExpectedPolarity() {
        return expectedPolarity;
    }
    public void setExpectedPolarity(Integer expectedPolarity) {
        this.expectedPolarity = expectedPolarity;
    }
    
}
