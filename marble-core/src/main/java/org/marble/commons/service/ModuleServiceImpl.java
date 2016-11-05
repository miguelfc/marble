package org.marble.commons.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.marble.commons.executor.plotter.PlotterExecutor;
import org.marble.commons.executor.processor.ProcessorExecutor;
import org.marble.commons.model.ExecutorParameter;
import org.marble.commons.model.JobModuleDefinition;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ModuleServiceImpl implements ModuleService {

    private final String plotterPackage = "org.marble.commons.executor.plotter";
    private final String processorPackage = "org.marble.commons.executor.processor";
    private static final Logger log = LoggerFactory.getLogger(ModuleServiceImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<JobModuleDefinition> getModules(String packageString, Class<T> superType) {
        List<JobModuleDefinition> modules = new ArrayList<>();

        Reflections reflections = new Reflections(packageString);
        Set<Class<? extends T>> implementors = reflections.getSubTypesOf(superType);

        for (Class<? extends T> implementor : implementors) {
            // Get Name
            JobModuleDefinition module = new JobModuleDefinition();
            module.setName(implementor.getName());
            module.setSimpleName(implementor.getSimpleName());
            // Get Label
            try {
                Field label = implementor.getField("label");
                module.setLabel((String) label.get(null));
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                log.error("An error ocurred while extracting label for class " + implementor.getName());
                continue;
            }
            // Get Operations
            try {
                Field operations = implementor.getField("operations");
                module.setOperations((List<ExecutorParameter>) operations.get(null));
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                log.error("An error ocurred while extracting operations for class " + implementor.getName());
                continue;
            }
            // Get Parameters
            try {
                Field parameters = implementor.getField("parameters");
                module.setParameters((List<ExecutorParameter>) parameters.get(null));
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                log.error("An error ocurred while extracting parameters for class " + implementor.getName());
                continue;
            }

            modules.add(module);
        }
        return modules;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> JobModuleDefinition getModule(String moduleName, String packageString, Class<T> superType) {
        Reflections reflections = new Reflections(packageString);
        Set<Class<? extends T>> implementors = reflections.getSubTypesOf(superType);
        JobModuleDefinition module = null;
        for (Class<? extends T> implementor : implementors) {
            if (implementor.getName().equals(moduleName)) {
                module = new JobModuleDefinition();
                module.setName(implementor.getName());
                module.setSimpleName(implementor.getSimpleName());
                // Get Label
                try {
                    Field label = implementor.getField("label");
                    module.setLabel((String) label.get(null));
                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                    log.error("An error ocurred while extracting label for class " + implementor.getName());
                    continue;
                }
                // Get Operations
                try {
                    Field operations = implementor.getField("operations");
                    module.setOperations((List<ExecutorParameter>) operations.get(null));
                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                    log.error("An error ocurred while extracting operations for class " + implementor.getName());
                    continue;
                }
                // Get Parameters
                try {
                    Field parameters = implementor.getField("parameters");
                    module.setParameters((List<ExecutorParameter>) parameters.get(null));
                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                    log.error("An error ocurred while extracting parameters for class " + implementor.getName());
                    continue;
                }
                break;
            }
        }
        return module;

    }

    @Override
    public JobModuleDefinition getPlotterModule(String moduleName) {
        return getModule(moduleName, this.plotterPackage, PlotterExecutor.class);
    }
    
    @Override
    public JobModuleDefinition getProcessorModule(String moduleName) {
        return getModule(moduleName, this.processorPackage, ProcessorExecutor.class);
    }

    @Override
    public List<JobModuleDefinition> getPlotterModules() {
        return getModules(this.plotterPackage, PlotterExecutor.class);
    }
    
    @Override
    public List<JobModuleDefinition> getProcessorModules() {
        return getModules(this.processorPackage, ProcessorExecutor.class);
    }

}
