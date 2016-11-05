package org.marble.commons.service;

import java.util.List;

import org.marble.commons.domain.model.ProcessedPost;
import org.marble.commons.exception.InvalidPostException;

public interface ProcessedPostService {

    public ProcessedPost save(ProcessedPost post) throws InvalidPostException;

    public ProcessedPost findOne(Long id) throws InvalidPostException;

    List<ProcessedPost> findAll();

    public void delete(Long id);

    Long deleteByTopicName(String topicName);

    Long count();

}
