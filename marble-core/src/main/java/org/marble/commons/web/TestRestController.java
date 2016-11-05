package org.marble.commons.web;

import org.marble.commons.service.JobService;
import org.marble.commons.service.ModuleService;
import org.marble.commons.service.processor.ProcessorService;

import org.marble.model.domain.ProcessorInput;
import org.marble.model.domain.ProcessorOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {
    private static final Logger log = LoggerFactory.getLogger(TestRestController.class);

    @Autowired
    ProcessorService processorSimpleService;
    @Autowired
    ProcessorService processorStanfordService;

    @Autowired
    ModuleService moduleService;

    @Autowired
    JobService jobService;

    @RequestMapping(value = "/api/test/processorSimple", method = RequestMethod.GET)
    public ProcessorOutput processorSimpleService() {
        ProcessorInput input = new ProcessorInput();
        input.setMessage("You suck man, big time");
        ProcessorOutput output = processorSimpleService.processMessage(input);
        return output;
    }

    @RequestMapping(value = "/api/test/processorStanford", method = RequestMethod.GET)
    public ProcessorOutput processorStanfordService() {
        ProcessorInput input = new ProcessorInput();
        input.setMessage("You suck man, big time but you studied");
        ProcessorOutput output = processorStanfordService.processMessage(input);
        return output;
    }

}
