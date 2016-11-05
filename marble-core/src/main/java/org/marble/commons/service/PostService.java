package org.marble.commons.service;

import java.util.List;

import org.marble.commons.domain.model.Post;
import org.marble.commons.exception.InvalidPostException;

public interface PostService {

    public Post save(Post post) throws InvalidPostException;

    public Post findOne(Long id) throws InvalidPostException;

    List<Post> findAll();

    public void delete(Long id);

    Long deleteByTopicName(String topicName);

    Long count();

}
