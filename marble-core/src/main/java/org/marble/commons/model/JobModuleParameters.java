package org.marble.commons.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

public class JobModuleParameters implements Serializable {

    private static final long serialVersionUID = 8266285660578157515L;
    
    @NotEmpty(message="{ExecutionModuleParameters.name.not_empty.validation}")
    @Pattern(regexp = "[a-zA-Z_0-9- ]+", message="{ExecutionModuleParameters.name.pattern.validation}")
    private String name;
    private String description;
    @NotEmpty(message="{ExecutionModuleParameters.module.validation}")
    private String module;
    private String simpleModule;
    @NotEmpty(message="{ExecutionModuleParameters.operation.validation}")
    private String operation;
    private Map<String, String> parameters = new HashMap<>();

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
        this.simpleModule = module.replaceAll("^.*\\.([^.]+)$", "$1");
    }

    public String getSimpleModule() {
        return simpleModule;
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
}
