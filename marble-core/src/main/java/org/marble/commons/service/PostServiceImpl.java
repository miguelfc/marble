package org.marble.commons.service;

import java.util.List;

import org.marble.commons.domain.repository.PostRepository;
import org.marble.commons.exception.InvalidPostException;
import org.marble.commons.model.RestResult;
import org.marble.model.domain.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);
    @Autowired
    PostRepository postRepository;

    @Autowired
    DatastoreService datastoreService;

    @Override
    public Post save(Post post) throws InvalidPostException {
        // TODO Modify this in order to update only certain fields (and do not
        // overwrite the post)
        post = postRepository.save(post);
        if (post == null) {
            throw new InvalidPostException();
        }
        return post;
    }

    @Override
    public Post findOne(Long id) throws InvalidPostException {
        Post post = postRepository.findOne(id);
        if (post == null) {
            throw new InvalidPostException();
        }
        return post;
    }
    
    @Override
    public Page<Post> findByTopicName(@Param("name") String name, Pageable pageable) {
        return postRepository.findByTopicName(name, pageable);
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        postRepository.delete(id);
        return;
    }

    @Override
    public Long deleteByTopicName(String topicName) {
        return postRepository.deleteByTopicName(topicName);
    }

    @Override
    public Long count() {
        return postRepository.count();
    }

    @Override
    public void tagPost(Long postId, String user, Integer polarity) throws InvalidPostException {
        Post post = this.findOne(postId);
        if (polarity != null) {
            post.addPolarityTag(user, polarity);
        }
        else {
            post.removePolarityTag(user);
        }
        this.save(post);
    }

}
