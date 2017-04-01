package org.marble.commons.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.marble.commons.exception.InvalidPostException;
import org.marble.model.domain.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    public Post save(Post post) throws InvalidPostException;

    public Post findOne(String id) throws InvalidPostException;

    List<Post> findAll();

    public void delete(String id);

    Long deleteByTopicName(String topicName);

    Long count();

    public void tagPost(String postId, String user, Integer polarity) throws InvalidPostException;

    Page<Post> findByTopicName(String name, Pageable pageable);
    
    Long addFromFile(InputStream inputStream, String topicName) throws IOException; 

}
