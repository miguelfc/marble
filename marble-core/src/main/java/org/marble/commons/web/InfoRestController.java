package org.marble.commons.web;

import org.marble.commons.model.HomeInformation;
import org.marble.commons.service.InformationService;
import org.marble.commons.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoRestController {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(InfoRestController.class);

    @Autowired
    InformationService informationService;

    
    @Autowired
    JobService jobService;

    @RequestMapping(value = "/api/info/home", method = RequestMethod.GET)
    public HomeInformation getHomeInfo() {
        HomeInformation homeInformation = informationService.getHomeInformation();
        return homeInformation;
    }
    
    /*
    @RequestMapping(value = "/api/info/validation/process", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<JobRestResult> validateProcessInfo(@RequestBody(required = true) JobRestRequest jobRestRequest) {
        BigInteger executionId = null;
        try {
            JobModuleParameters jobModuleParameters = new JobModuleParameters();
            
            List<JobModuleDefinition> modules = moduleService.getProcessorModules();
            for (JobModuleDefinition module : modules) {
                log.error(module.getName());
            }
            
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
