package org.marble.commons.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.marble.commons.domain.repository.ProcessedPostRepository;
import org.marble.commons.exception.InvalidPostException;
import org.marble.model.domain.model.Post;
import org.marble.model.domain.model.ProcessedPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProcessedPostServiceImpl implements ProcessedPostService {

    private static final Logger log = LoggerFactory.getLogger(ProcessedPostServiceImpl.class);
    @Autowired
    ProcessedPostRepository processedPostRepository;

    @Autowired
    DatastoreService datastoreService;

    @Override
    public ProcessedPost save(ProcessedPost processedPost) throws InvalidPostException {
        // TODO Modify this in order to update only certain fields (and do not
        // overwrite the post)
        processedPost = processedPostRepository.save(processedPost);
        if (processedPost == null) {
            throw new InvalidPostException();
        }
        return processedPost;
    }

    @Override
    public ProcessedPost findOne(Long id) throws InvalidPostException {
        ProcessedPost processedPost = processedPostRepository.findOne(id);
        if (processedPost == null) {
            throw new InvalidPostException();
        }
        return processedPost;
    }

    @Override
    public Page<ProcessedPost> findByTopicName(@Param("name") String name, Pageable pageable) {
        return processedPostRepository.findByTopicName(name, pageable);
    }
    
    @Override
    public List<ProcessedPost> findAll() {
        return processedPostRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        processedPostRepository.delete(id);
        return;
    }
    
    @Override
    public Long deleteByTopicName(String topicName) {
        return processedPostRepository.deleteByTopicName(topicName);
    }

    @Override
    public Long count() {
        return processedPostRepository.count();
    }
    
    @Override
    public Long addFromFile(InputStream inputStream, String topicName) throws IOException {
        Long count = 0L;
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            for (String line; (line = br.readLine()) != null;) {
                // process the line.
                try {
                    ProcessedPost post = mapper.readValue(line, ProcessedPost.class);
                    this.save(post);
                    count++;
                } catch (InvalidPostException e) {
                    log.error("An error occurred while importing a post.", e);
                }
            }
        } catch (IOException e) {
            throw (e);
        }

        log.info("A total of <" + count + "> processed posts were uploaded for topic <" + topicName + ">");
        return count;
    }

}
