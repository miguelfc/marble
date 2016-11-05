package org.marble.commons.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

public class ProcessParameters implements Serializable {

    private static final long serialVersionUID = 8266285660578157515L;

    private String name;
    private Map<String, Object> options;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "ProcessParameters [name=" + name + ", options=" + options + "]";
    }

}
