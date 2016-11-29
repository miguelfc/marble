package org.marble.model.model;

import java.util.Map;
import java.util.TreeMap;

public class PlotterInput {
    private String topicName;
    private Map<String, Object> options = new TreeMap<>();
    
    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
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
        return "PlotterInput [topicName=" + topicName + ", options=" + options + "]";
    }


}
