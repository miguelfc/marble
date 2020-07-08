package org.marble.commons.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.marble.commons.model.HomeInformation;
import org.marble.commons.service.InformationService;
import org.marble.commons.service.JobService;
import org.marble.commons.service.MongoFileService;
import org.marble.model.domain.model.MongoFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.DBObject;

@RestController
public class MongoFileRestController {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(MongoFileRestController.class);

    @Autowired
    MongoFileService mongoFileService;


    @RequestMapping(value = "/api/mongo_file/{id}", method = RequestMethod.GET)
    public void getHomeInfo(@PathVariable(value = "id") String id, HttpServletResponse response) throws IOException {
      MongoFile<DBObject> file = mongoFileService.findById(id);
      response.setHeader("Content-Disposition", "inline");
      response.setHeader("Content-Type", file.getContentType());
      
      OutputStream output = response.getOutputStream();
      InputStream input = file.getContent();
      byte[] buffer = new byte[10240];
      for (int length = 0; (length = input.read(buffer)) > 0;) {
        output.write(buffer, 0, length);
      }
      output.close();
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
