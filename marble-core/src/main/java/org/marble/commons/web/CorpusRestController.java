package org.marble.commons.web;

import javax.servlet.annotation.MultipartConfig;

import org.marble.commons.exception.InvalidTopicException;
import org.marble.commons.model.RestResult;
import org.marble.commons.model.TopicStats;
import org.marble.commons.service.SentiWordNetService;
import org.marble.commons.service.SenticNetService;
import org.marble.commons.service.TopicService;
import org.marble.commons.service.ValidationDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@RestController
@MultipartConfig(fileSizeThreshold = 20971520)
public class CorpusRestController {

    private static final Logger log = LoggerFactory.getLogger(CorpusRestController.class);

    @Autowired
    SenticNetService senticNetService;
    @Autowired
    SentiWordNetService sentiWordNetService;
    @Autowired
    ValidationDataService validationDataService;

    @RequestMapping(value = "/api/corpus/senticnet", method = RequestMethod.POST, consumes = "multipart/form-data")
    public @ResponseBody ResponseEntity<RestResult> uploadSenticNet(@RequestParam("file") MultipartFile file) {
        try {
            senticNetService.insertDataFromFile(file);
            return new ResponseEntity<RestResult>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<RestResult>(new RestResult(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "/api/corpus/sentiwordnet", method = RequestMethod.POST, consumes = "multipart/form-data")
    public @ResponseBody ResponseEntity<RestResult> uploadSentiWordNet(@RequestParam("file") MultipartFile file) {
        try {
            sentiWordNetService.insertDataFromFile(file);
            return new ResponseEntity<RestResult>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<RestResult>(new RestResult(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "/api/corpus/validation", method = RequestMethod.POST, consumes = "multipart/form-data")
    public @ResponseBody ResponseEntity<RestResult> uploadValidation(@RequestParam("file") MultipartFile file) {
        try {
            validationDataService.insertDataFromFile(file);
            return new ResponseEntity<RestResult>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception",e);
            return new ResponseEntity<RestResult>(new RestResult(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}