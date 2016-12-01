package org.marble.commons.service;

import java.util.List;

import org.marble.commons.domain.repository.TopicRepository;
import org.marble.commons.exception.InvalidTopicException;
import org.marble.commons.model.TopicStats;
import org.marble.model.domain.model.Post;
import org.marble.model.domain.model.ProcessedPost;
import org.marble.model.domain.model.Topic;

import com.mongodb.MongoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TopicServiceImpl implements TopicService {

    private static final Logger log = LoggerFactory.getLogger(TopicServiceImpl.class);
    @Autowired
    TopicRepository topicRepository;

    @Autowired
    DatastoreService datastoreService;

    @Autowired
    JobService jobService;

    @Autowired
    ChartService plotService;

    @Autowired
    PostService postService;

    @Override
    public Topic save(Topic topic) throws InvalidTopicException {
        // TODO Modify this in order to update only certain fields (and do not
        // overwrite the post)
        topic = topicRepository.save(topic);
        if (topic == null) {
            throw new InvalidTopicException();
        }
        return topic;
    }

    @Override
    public Topic findOne(String name) throws InvalidTopicException {
        Topic topic = topicRepository.findOne(name);
        if (topic == null) {
            throw new InvalidTopicException();
        }
        return topic;
    }

    @Override
    public List<Topic> findAll() {
        List<Topic> topics = topicRepository.findAll();
        return topics;
    }

    @Override
    public void delete(String name) {
        topicRepository.delete(name);
        // Remove all the related posts and jobs from the database
        postService.deleteByTopicName(name);
        jobService.deleteByTopicName(name);
        plotService.deleteByTopicName(name);
        // TODO remove processed posts
        return;
    }

    @Override
    public TopicStats getStats(String name) throws InvalidTopicException {
        // This is only to check if exists
        Topic topic = topicRepository.findOne(name);
        if (topic == null) {
            throw new InvalidTopicException();
        }

        TopicStats topicStats = new TopicStats();
        topicStats.setTopicName(name);
        try {
            topicStats.setTotalPostsExtracted(datastoreService.countByTopicId(name, Post.class));
            topicStats.setTotalPostsProcessed(datastoreService.countByTopicId(name, ProcessedPost.class));

            if (topicStats.getTotalPostsExtracted() > 0) {
                Post post = datastoreService.findOneByTopicIdSortBy(name, "createdAt", Sort.Direction.ASC,
                        Post.class);
                topicStats.setOldestPostDate(post.getCreatedAt());
                topicStats.setOldestPostId(post.getId());

                post = datastoreService
                        .findOneByTopicIdSortBy(name, "createdAt", Sort.Direction.DESC, Post.class);
                topicStats.setNewestPostDate(post.getCreatedAt());
                topicStats.setNewestPostId(post.getId());

                topicStats.setTotalJobs(jobService.countByTopicName(name));
            }
        } catch (MongoException e) {
            log.warn(
                    "Exception caught while extracting the topic info.",
                    e);
        }
        return topicStats;
    }

    @Override
    public Long count() {
        return topicRepository.count();
    }

}
