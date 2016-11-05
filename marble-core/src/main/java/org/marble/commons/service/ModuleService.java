package org.marble.commons.service;

import java.util.List;
import org.marble.commons.model.JobModuleDefinition;

public interface ModuleService {

    List<JobModuleDefinition> getPlotterModules();

    JobModuleDefinition getPlotterModule(String module);
    
    List<JobModuleDefinition> getProcessorModules();
    
    JobModuleDefinition getProcessorModule(String module);

    <T> List<JobModuleDefinition> getModules(String packageString, Class<T> superType);

    <T> JobModuleDefinition getModule(String moduleName, String packageString, Class<T> superType);

}
