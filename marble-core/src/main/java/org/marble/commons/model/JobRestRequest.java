package org.marble.commons.model;

import java.util.Map;

public class JobRestRequest extends RestRequest {

    private String name;
    private String description;
    private String module;
    private String operation;
    private Map<String, String> parameters;

    public JobRestRequest() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "JobRestRequest [name=" + name + ", description=" + description + ", module=" + module + ", operation=" + operation + ", parameters=" + parameters + "]";
    }


}
