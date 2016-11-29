package org.marble.commons.service;

import java.util.List;

import org.marble.commons.exception.InvalidPostException;
import org.marble.model.domain.model.Post;

public interface PostService {

    public Post save(Post post) throws InvalidPostException;

    public Post findOne(Long id) throws InvalidPostException;

    List<Post> findAll();

    public void delete(Long id);

    Long deleteByTopicName(String topicName);

    Long count();

}
