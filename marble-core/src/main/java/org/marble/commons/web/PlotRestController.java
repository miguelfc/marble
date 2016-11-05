package org.marble.commons.web;

import org.marble.commons.model.RestResult;
import org.marble.commons.service.PlotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RepositoryRestController
public class PlotRestController {

    private static final Logger log = LoggerFactory.getLogger(PlotRestController.class);

    @Autowired
    PlotService plotService;

    @RequestMapping(value = "/plots", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<RestResult> deleteByTopicName(@RequestParam(value = "topicName") String topicName) {
        log.debug("Deleting all the plots of topic <" + topicName + ">.");
        Long count = plotService.deleteByTopicName(topicName);
        RestResult restResult = new RestResult();
        restResult.setMessage("A total of <" + count + "> plots from topic <" + topicName + "> were deleted.");
        return new ResponseEntity<RestResult>(restResult, HttpStatus.OK);
    }
}