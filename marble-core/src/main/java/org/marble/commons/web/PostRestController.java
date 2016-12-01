package org.marble.commons.web;

import org.marble.commons.exception.InvalidPostException;
import org.marble.commons.exception.InvalidTopicException;
import org.marble.commons.model.RestResult;
import org.marble.commons.model.TagPostRestRequest;
import org.marble.commons.model.TopicStats;
import org.marble.commons.service.JobService;
import org.marble.commons.service.PostService;
import org.marble.model.domain.model.Post;
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
public class PostRestController {

    private static final Logger log = LoggerFactory.getLogger(PostRestController.class);

    @Autowired
    PostService postService;

    @Autowired
    JobService executionService;

    @RequestMapping(value = "/posts", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<RestResult> delete(@RequestParam(value = "topicName") String topicName) {
        log.debug("Deleting all the posts of topic <" + topicName + ">.");
        Long count = postService.deleteByTopicName(topicName);
        RestResult restResult = new RestResult();
        restResult.setMessage("A total of <" + count + "> posts from topic <" + topicName + "> were deleted.");
        return new ResponseEntity<RestResult>(restResult, HttpStatus.OK);
    }


    @RequestMapping(value = "/posts/tag/{id}", method = RequestMethod.PATCH)
    public @ResponseBody ResponseEntity<RestResult> tag(@PathVariable(value = "id") Long id,
            @RequestBody(required = true) TagPostRestRequest tagRequest) {
        log.debug("Tagging post <" + id + "> by user <" + tagRequest.getUser() + ">.");
        RestResult restResult = new RestResult();

        if (tagRequest.getUser() == null || tagRequest.getPolarity() == null) {
            restResult.setMessage("Invalid request.");
            return new ResponseEntity<RestResult>(restResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        try {
            postService.tagPost(id, tagRequest.getUser(), tagRequest.getPolarity());
        } catch (InvalidPostException e) {
            restResult.setMessage("The post is invalid.");
            return new ResponseEntity<RestResult>(restResult, HttpStatus.NOT_FOUND);
        }
       
        restResult.setMessage("Post <" + id + "> tagged by user <" + tagRequest.getUser() + "> with polarity <" + tagRequest.getPolarity() + ">.");
        return new ResponseEntity<RestResult>(restResult, HttpStatus.OK);
    }
}