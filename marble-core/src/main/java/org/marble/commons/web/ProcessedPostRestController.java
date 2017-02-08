package org.marble.commons.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

import org.marble.commons.model.RestResult;
import org.marble.commons.service.JobService;
import org.marble.commons.service.ProcessedPostService;
import org.marble.model.domain.model.ProcessedPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import twitter4j.JSONObject;

@RepositoryRestController
public class ProcessedPostRestController {

    private static final Logger log = LoggerFactory.getLogger(ProcessedPostRestController.class);

    @Autowired
    ProcessedPostService processedPostService;

    @Autowired
    JobService executionService;

    @RequestMapping(value = "/processedPosts", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<RestResult> delete(@RequestParam(value = "topicName") String topicName) {
        log.debug("Deleting all the posts of topic <" + topicName + ">.");
        Long count = processedPostService.deleteByTopicName(topicName);
        RestResult restResult = new RestResult();
        restResult.setMessage("A total of <" + count + "> processed posts from topic <" + topicName + "> were deleted.");
        return new ResponseEntity<RestResult>(restResult, HttpStatus.OK);
    }

    @RequestMapping(value = "/processedPosts/download/topic/{topicName}", method = RequestMethod.GET)
    public void downloadByTopicId(@PathVariable(value = "topicName") String topicName, HttpServletResponse response) throws IOException {
        log.debug("Downloading all the posts of topic <" + topicName + ">.");
        response.setHeader("Content-Disposition", "attachment;filename=" + topicName + "_processedPosts.json");

        PrintWriter out = response.getWriter();
        Pageable page = new PageRequest(0, 100);
        Page<ProcessedPost> results;
        do {
            results = processedPostService.findByTopicName(topicName, page);
            for (ProcessedPost post : results.getContent()) {
                JSONObject jsonObject = new JSONObject(post);
                out.println(jsonObject.toString());
            }
            page = page.next();
        } while (results.hasNext());
        out.close();
    }
}