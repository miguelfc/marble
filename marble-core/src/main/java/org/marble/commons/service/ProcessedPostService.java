package org.marble.commons.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.marble.commons.exception.InvalidPostException;
import org.marble.model.domain.model.ProcessedPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProcessedPostService {

    public ProcessedPost save(ProcessedPost post) throws InvalidPostException;

    public ProcessedPost findOne(Long id) throws InvalidPostException;

    Page<ProcessedPost> findByTopicName(String name, Pageable pageable);

    List<ProcessedPost> findAll();

    public void delete(Long id);

    Long deleteByTopicName(String topicName);

    Long count();

    Long addFromFile(InputStream inputStream, String topicName) throws IOException;

}
