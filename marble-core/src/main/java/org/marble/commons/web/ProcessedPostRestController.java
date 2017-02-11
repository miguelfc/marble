package org.marble.commons.web;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer();
        PrintWriter out = response.getWriter();
        Pageable page = new PageRequest(0, 100);
        Page<ProcessedPost> results;
        do {
            results = processedPostService.findByTopicName(topicName, page);
            for (ProcessedPost post : results.getContent()) {
                out.println(writer.writeValueAsString(post));
            }
            page = page.next();
        } while (results.hasNext());
        out.close();
    }

    @RequestMapping(value = "/processedPosts/upload/topic/{topicName}", method = RequestMethod.POST)
    public ResponseEntity<RestResult> uploadByTopicId(@PathVariable(value = "topicName") String topicName, @RequestParam("file") MultipartFile file) {
        RestResult restResult = new RestResult();

        Long count = 0L;
        try {
            InputStream inputStream = new BufferedInputStream(file.getInputStream());
            count = processedPostService.addFromFile(inputStream, topicName);
        } catch (IOException e) {
            log.error("An error occurred while uploading posts", e);
            return new ResponseEntity<RestResult>(restResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        restResult.setMessage("A total of <" + count + "> processed posts from topic <" + topicName + "> were uploaded.");
        return new ResponseEntity<RestResult>(restResult, HttpStatus.OK);
    }

}