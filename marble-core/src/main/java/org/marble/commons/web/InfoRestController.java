package org.marble.commons.web;

import org.marble.commons.model.ExecutorInfo;
import org.marble.commons.model.HomeInformation;
import org.marble.commons.model.JobModuleDefinition;
import org.marble.commons.model.JobModuleParameters;
import org.marble.commons.model.JobRestRequest;
import org.marble.commons.model.JobRestResult;
import org.marble.commons.service.InformationService;
import org.marble.commons.service.JobService;
import org.marble.commons.service.ModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.Principal;
import java.util.List;

@RestController
public class InfoRestController {
    private static final Logger log = LoggerFactory.getLogger(InfoRestController.class);

    @Autowired
    InformationService informationService;
    
    @Autowired
    ModuleService moduleService;
    
    @Autowired
    JobService jobService;

    @RequestMapping(value = "/api/info/home", method = RequestMethod.GET)
    public HomeInformation getHomeInfo() {
        HomeInformation homeInformation = informationService.getHomeInformation();
        return homeInformation;
    }
    
    @RequestMapping(value = "/api/info/processors", method = RequestMethod.GET)
    public List<ExecutorInfo> getProcessorsInfo() {
        List<ExecutorInfo> info = informationService.getProcessorsInfo();
        return info;
    }
    
    @RequestMapping(value = "/api/info/plotters", method = RequestMethod.GET)
    public List<ExecutorInfo> getPlottersInfo() {
        List<ExecutorInfo> info = informationService.getPlottersInfo();
        return info;
    }
    
    /*
    @RequestMapping(value = "/api/info/validation/process", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<JobRestResult> validateProcessInfo(@RequestBody(required = true) JobRestRequest jobRestRequest) {
        BigInteger executionId = null;
        try {
            JobModuleParameters jobModuleParameters = new JobModuleParameters();
            
            // TODO Check getProcessorModules, it seems broken
            List<JobModuleDefinition> modules = moduleService.getProcessorModules();
            for (JobModuleDefinition module : modules) {
                log.error(module.getName());
            }
            
            // TODO Validate the module name
            if (jobRestRequest != null && jobRestRequest.getModule() != null) {
                jobModuleParameters.setModule("org.marble.commons.executor.processor." + jobRestRequest.getModule());
                jobModuleParameters.setOperation(jobRestRequest.getOperation());
            }

            if (jobRestRequest.getParameters() != null) {
                jobModuleParameters.setParameters(jobRestRequest.getParameters());
            }
            executionId = jobService.executeProcessor(null, jobModuleParameters);
            JobRestResult executionResult = new JobRestResult(executionId);
            executionResult.setMessage("Execution started.");
            return new ResponseEntity<JobRestResult>(executionResult, HttpStatus.OK);
        } catch (Exception e) {
            JobRestResult executionResult = new JobRestResult(e.getMessage());
            return new ResponseEntity<JobRestResult>(executionResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    */
}