package org.marble.commons.web;

import org.marble.commons.service.JobService;
import org.marble.commons.service.ModuleService;
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
    ModuleService moduleService;

    @Autowired
    JobService jobService;



}
