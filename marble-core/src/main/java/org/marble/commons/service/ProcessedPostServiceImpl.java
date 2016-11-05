package org.marble.commons.service;

import java.util.List;

import org.marble.commons.domain.repository.ProcessedPostRepository;
import org.marble.commons.domain.model.ProcessedPost;
import org.marble.commons.exception.InvalidPostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
