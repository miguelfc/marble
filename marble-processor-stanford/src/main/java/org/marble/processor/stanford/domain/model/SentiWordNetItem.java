package org.marble.processor.stanford.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sentiwordnet3")
public class SentiWordNetItem {

    @Id
    private String id;
    @Indexed
    private String text;
    @Indexed
    private String pos;
    private Double polarity;

    public SentiWordNetItem() {

    }

    public SentiWordNetItem(String compositeText, Double polarity) {
        String[] parts = compositeText.split("#");
        this.text = parts[0];
        this.pos = parts[1];
        this.id = compositeText;
        this.polarity = polarity;
    }
    
    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public Double getPolarity() {
        return polarity;
    }

    public void setPolarity(Double polarity) {
        this.polarity = polarity;
    }

    public String toString() {
        return "(" + this.text + "; " + this.pos + "; " + this.polarity + ")";
    }
}
