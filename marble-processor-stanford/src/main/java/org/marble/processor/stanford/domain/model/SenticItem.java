package org.marble.processor.stanford.domain.model;



import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "senticnet2")
public class SenticItem {

    @Id
    @Indexed
    private String text;
    private Float polarity;

    public SenticItem() {

    }

    public SenticItem(String text, Float polarity) {
        this.text = text;
        this.polarity = polarity;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Float getPolarity() {
        return polarity;
    }

    public void setPolarity(Float polarity) {
        this.polarity = polarity;
    }

    public String toString() {
        return "(" + this.text + "; " + this.polarity + ")";
    }
}
