package org.marble.commons.service;

import java.util.List;

import org.marble.commons.domain.repository.PostRepository;
import org.marble.commons.domain.model.Post;
import org.marble.commons.exception.InvalidPostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);
    @Autowired
    PostRepository postDao;

    @Autowired
    DatastoreService datastoreService;

    @Override
    public Post save(Post post) throws InvalidPostException {
        // TODO Modify this in order to update only certain fields (and do not
        // overwrite the post)
        post = postDao.save(post);
        if (post == null) {
            throw new InvalidPostException();
        }
        return post;
    }

    @Override
    public Post findOne(Long id) throws InvalidPostException {
        Post post = postDao.findOne(id);
        if (post == null) {
            throw new InvalidPostException();
        }
        return post;
    }

    @Override
    public List<Post> findAll() {
        return postDao.findAll();
    }

    @Override
    public void delete(Long id) {
        postDao.delete(id);
        return;
    }
    
    @Override
    public Long deleteByTopicName(String topicName) {
        return postDao.deleteByTopicName(topicName);
    }

    @Override
    public Long count() {
        return postDao.count();
    }

}
