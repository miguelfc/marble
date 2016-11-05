package org.marble.commons.domain.model;

import java.util.Date;


import org.marble.commons.model.SymplifiedProcessingItem;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "validation_items")
public class ValidationItem {

    @Id
    @Indexed
    private long id;
    private String text;
    private Integer polarity;

    public ValidationItem() {
        this.id = 0;
        this.text = "";
        this.polarity = 0;
    }

    public ValidationItem(long id, String text, Integer polarity) {
        this.id = id;
        this.text = text;
        this.polarity = polarity;
    }
    
    
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

    public Integer getPolarity() {
        return polarity;
    }

    public void setPolarity(Integer polarity) {
        this.polarity = polarity;
    }

    public String toString() {
        return "(" + this.text + "; " + this.polarity + ")";
    }
    
    public SymplifiedProcessingItem getSymplifiedProcessingItem() {
        SymplifiedProcessingItem symplifiedProcessingItem = new SymplifiedProcessingItem();
        symplifiedProcessingItem.setId(this.id);
        symplifiedProcessingItem.setCreatedAt(new Date());
        symplifiedProcessingItem.setText(this.text);
        symplifiedProcessingItem.setExpectedPolarity(polarity);
        return symplifiedProcessingItem;
    }
}
