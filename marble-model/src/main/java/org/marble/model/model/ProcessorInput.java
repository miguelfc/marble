package org.marble.model.model;

import java.util.Map;
import java.util.TreeMap;

public class ProcessorInput {
    private String message;
    private Map<String, Object> options = new TreeMap<>();
    

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getOptions() {
        return options;
    }
    
    public Object getOption(String key) {
        return options.get(key);
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }
    
    public Object setOption(String key, Object value) {
        return options.put(key, value);
    }

    @Override
    public String toString() {
        return "ProcessingInput [message=" + message + ", options=" + options + "]";
    }


}
