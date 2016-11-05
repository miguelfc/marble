package org.marble.commons.web;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import org.marble.commons.exception.InvalidExecutionException;
import org.marble.commons.exception.InvalidModuleException;
import org.marble.commons.exception.InvalidTopicException;
import org.marble.commons.model.JobModuleDefinition;
import org.marble.commons.model.JobModuleParameters;
import org.marble.commons.model.JobRestRequest;
import org.marble.commons.model.JobRestResult;
import org.marble.commons.model.ProcessParameters;
import org.marble.commons.model.RestResult;
import org.marble.commons.model.TopicStats;
import org.marble.commons.service.TopicService;
import org.marble.commons.service.JobService;
import org.marble.commons.service.ModuleService;
import org.marble.commons.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RepositoryRestController
public class TopicRestController {

    private static final Logger log = LoggerFactory.getLogger(TopicRestController.class);

    @Autowired
    TopicService topicService;

    @Autowired
    PostService postService;

    @Autowired
    JobService jobService;

    @Autowired
    ModuleService moduleService;

    @RequestMapping(value = "/topics/{topicName}/info", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<TopicStats> info(@PathVariable(value = "topicName") String topicName) {

        TopicStats topicInfo;
        try {
            topicInfo = topicService.getStats(topicName);
            return new ResponseEntity<TopicStats>(topicInfo, HttpStatus.OK);
        } catch (InvalidTopicException e) {
            return new ResponseEntity<TopicStats>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/topics/{topicName}/extract", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<JobRestResult> extract(
            @PathVariable(value = "topicName") String topicName) {
        BigInteger executionId;
        try {
            executionId = jobService.executeExtractor(topicName);
            JobRestResult executionResult = new JobRestResult(executionId);
            executionResult.setMessage("Execution started.");
            return new ResponseEntity<JobRestResult>(executionResult, HttpStatus.OK);
        } catch (InvalidTopicException | InvalidExecutionException e) {
            JobRestResult executionResult = new JobRestResult(e.getMessage());
            return new ResponseEntity<JobRestResult>(executionResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/topics/{topicName}/process", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<JobRestResult> process(
            @PathVariable(value = "topicName") String topicName, @RequestBody(required = true) Set<ProcessParameters> processParameters) throws InvalidModuleException {
        BigInteger executionId = null;
        try {
            
            // TODO Check getProcessorModules, it seems broken
            List<JobModuleDefinition> modules = moduleService.getProcessorModules();
            for (JobModuleDefinition module : modules) {
                log.error(module.getName());
            }
            
            // TODO Validate the module name
            if (processParameters != null && processParameters.size() != 0) {
                
            }

            executionId = jobService.executeProcessor(topicName, processParameters);
            JobRestResult executionResult = new JobRestResult(executionId);
            executionResult.setMessage("Execution started.");
            return new ResponseEntity<JobRestResult>(executionResult, HttpStatus.OK);
        } catch (Exception e) {
            JobRestResult executionResult = new JobRestResult(e.getMessage());
            return new ResponseEntity<JobRestResult>(executionResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/topics/{topicName}/plot", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<JobRestResult> plot(
            @PathVariable(value = "topicName") String topicName,
            @RequestBody(required = true) JobRestRequest jobRestRequest) throws InvalidModuleException {

        BigInteger executionId = null;
        try {
            JobModuleParameters jobModuleParameters = new JobModuleParameters();
            // TODO Check getPlotterModules, it seems broken
            List<JobModuleDefinition> modules = moduleService.getPlotterModules();
            for (JobModuleDefinition module : modules) {
                log.error(module.getName());
            }
            // TODO Validate the module name
            jobModuleParameters.setModule("org.marble.commons.executor.plotter." + jobRestRequest.getModule());
            jobModuleParameters.setOperation(jobRestRequest.getOperation());
            jobModuleParameters.setName(jobRestRequest.getName());
            jobModuleParameters.setDescription(jobRestRequest.getDescription());

            executionId = jobService.executePlotter(topicName, jobModuleParameters);
            JobRestResult executionResult = new JobRestResult(executionId);
            executionResult.setMessage("Execution started.");
            return new ResponseEntity<JobRestResult>(executionResult, HttpStatus.OK);
        } catch (Exception e) {
            JobRestResult executionResult = new JobRestResult(e.getMessage());
            return new ResponseEntity<JobRestResult>(executionResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/topics/{topicName}", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<RestResult> delete(
            @PathVariable(value = "topicName") String topicName) {
        try {
            topicService.delete(topicName);
            RestResult restResult = new RestResult();
            restResult.setMessage("Topic <" + topicName + "> and its related information was deleted.");
            return new ResponseEntity<RestResult>(restResult, HttpStatus.OK);

        } catch (Exception e) {
            RestResult executionResult = new RestResult(e.getMessage());
            return new ResponseEntity<RestResult>(executionResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}