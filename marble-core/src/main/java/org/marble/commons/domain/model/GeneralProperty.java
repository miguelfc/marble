package org.marble.commons.domain.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "configuration")
public class GeneralProperty implements Serializable {

    private static final long serialVersionUID = -946199796905909629L;

    // TODO MFC Not sure if this one is going to survive

    @Id
    @NotNull
    @NotEmpty
    private String name;

    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}