package org.marble.commons.service;

import java.util.List;

import org.marble.commons.exception.InvalidPostException;
import org.marble.model.domain.model.ProcessedPost;

public interface ProcessedPostService {

    public ProcessedPost save(ProcessedPost post) throws InvalidPostException;

    public ProcessedPost findOne(Long id) throws InvalidPostException;

    List<ProcessedPost> findAll();

    public void delete(Long id);

    Long deleteByTopicName(String topicName);

    Long count();

}
