package org.marble.commons.model;

public class ExecutorParameter {
    private String id;
    private String label;
    private String help;
    private String placeholder;

    public ExecutorParameter() {
    }

    public ExecutorParameter(String id) {
        this.id = id;
    }
    
    public ExecutorParameter(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public ExecutorParameter(String id, String label, String help, String placeholder) {
        this.id = id;
        this.label = label;
        this.help = help;
        this.placeholder = placeholder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExecutorParameter other = (ExecutorParameter) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ExecutorParameter [id=" + id + ", label=" + label + ", help=" + help + ", placeholder=" + placeholder + "]";
    }
}
