package org.marble.commons.model;

import java.util.List;
import java.util.Map;

public class JobModuleDefinition {
    private String name;
    private String simpleName;
    private String label;
    private List<ExecutorParameter> operations;
    private List<ExecutorParameter> parameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<ExecutorParameter> getOperations() {
        return operations;
    }

    public void setOperations(List<ExecutorParameter> operations) {
        this.operations = operations;
    }

    public List<ExecutorParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ExecutorParameter> parameters) {
        this.parameters = parameters;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JobModuleDefinition other = (JobModuleDefinition) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

}
