package org.marble.commons.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);
    @Autowired
    PostRepository postRepository;

    @Autowired
    DatastoreService datastoreService;

    @Override
    public Post save(Post post) throws InvalidPostException {
        post = postRepository.save(post);
        if (post == null) {
            throw new InvalidPostException();
        }
        return post;
    }

    @Override
    public Post findOne(String id) throws InvalidPostException {
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
    public void delete(String id) {
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
    public void tagPost(String postId, String user, Integer polarity) throws InvalidPostException {
        Post post = this.findOne(postId);
        if (polarity != null) {
            post.addPolarityTag(user, polarity);
        } else {
            post.removePolarityTag(user);
        }
        this.save(post);
    }

    @Override
    public Long addFromFile(InputStream inputStream, String topicName) throws IOException {
        Long count = 0L;
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        log.info("Adding posts from file to topic <" + topicName + ">");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            for (String line; (line = br.readLine()) != null;) {
                // process the line.

                {
                    // Replacing odd "createdAt" : { "$date" :
                    // "2011-06-12T13:02:01.000Z"} definitions
                    Pattern createdAtPattern = Pattern.compile("\"createdAt\" *: *\\{ *\"\\$date\" *: *\"([^\"]+)\" *\\}");
                    Matcher createdAtMatcher = createdAtPattern.matcher(line);
                    while (createdAtMatcher.find()) {
                        line = line.replace(createdAtMatcher.group(0), "\"createdAt\": \"" + createdAtMatcher.group(1) + "\"");
                    }
                    line = line.replaceAll("\"_id\"", "\"id\"");
                }

                try {
                    Post post = mapper.readValue(line, Post.class);
                    // Temporary provision to allow uploads from previous marble versions
                    if (post.getOriginalId() == 0) {
                      post.composeId(Long.parseLong(post.getId()), topicName);
                    }
                    post.setTopicId(topicName);
                    this.save(post);
                    count++;
                } catch (InvalidPostException e) {
                    log.error("An error occurred while importing a post.", e);
                }
                
                if (count % 100 == 0) {
                    log.info("Posts uploaded so far: <" + count + ">");
                }
            }
        } catch (IOException e) {
            throw (e);
        }
        log.info("A total of <" + count + "> posts were uploaded for topic <" + topicName + ">");
        return count;
    }

}
