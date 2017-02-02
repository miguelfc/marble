package org.marble.commons.executor.streamer;

import org.marble.model.domain.model.Job;
import org.marble.model.domain.model.Post;
import org.marble.model.domain.model.Topic;
import org.marble.commons.exception.InvalidExecutionException;
import org.marble.commons.exception.InvalidTopicException;
import org.marble.commons.executor.extractor.TwitterExtractionExecutor;
import org.marble.commons.service.JobService;
import org.marble.commons.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class TwitterStreamingListener implements StatusListener {

    JobService jobService;

    PostService postService;
    private boolean failure = false;
    private boolean stopping = false;
    private String topicName;
    private String keywords;
    private Topic topic;
    private static final Logger log = LoggerFactory.getLogger(TwitterExtractionExecutor.class);
    private long count;
    private Job job;

    public TwitterStreamingListener(Topic topic, Job job, PostService postService, JobService executionService) {
        this.topic = topic;
        this.keywords = topic.getKeywords().toLowerCase();
        this.job = job;
        this.postService = postService;
        this.topicName = topic.getName();
        this.jobService = executionService;
        count = 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((keywords == null) ? 0 : keywords.hashCode());
        result = prime * result + ((topicName == null) ? 0 : topicName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TwitterStreamingListener other = (TwitterStreamingListener) obj;
        if (keywords == null) {
            if (other.keywords != null)
                return false;
        } else if (!keywords.equals(other.keywords))
            return false;
        if (topicName == null) {
            if (other.topicName != null)
                return false;
        } else if (!topicName.equals(other.topicName))
            return false;
        return true;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getKeywords() {
        return keywords;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public String getLanguage() {
        return topic.getLanguage();
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public void onStatus(Status status) {

        String msg = "Received new post <" + status.getId() + ">. Matching for topic <" + topic.getName() + ">.";
        log.debug(msg);
        job.appendLog(msg);

        try {
            jobService.save(job);
        } catch (InvalidExecutionException e) {
            log.error("Couldn't persist job object.", e);
        }

        if (!"".equals(keywords)) {
            String[] kwords = keywords.split(" ");
            String tweetText = status.getText().toLowerCase();
            for (String kword : kwords) {
                if (!tweetText.contains(kword)) {
                    return;
                }
            }
        }

        if (!"".equals(topic.getLanguage())) {
            if (!topic.getLanguage().equals(status.getLang())) {
                return;
            }
        }

        // Save the post
        if (!stopping) {

            msg = "Post <" + status.getId() + "> matched keywords <" + keywords + ">.";
            log.debug(msg);
            job.appendLog(msg);

            try {
                jobService.save(job);
            } catch (InvalidExecutionException e) {
                log.error("Couldn't persist job object.", e);
            }

            Post streamingStatus = new Post(status, topic.getName());
            try {
                if (postService == null)
                    log.error("Data store is null");
                postService.save(streamingStatus);
                failure = false;
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            count++;
        }

        long maxStatuses = 200;
        if (topic.getPostsPerFullExtraction() != null) {
            maxStatuses = topic.getPostsPerFullExtraction();
            if (count > maxStatuses && maxStatuses > 0) {
                if (stopping)
                    return;
                stopping = true;
                try {
                    msg = "Stopping listener as it has reached the maximum count <" + maxStatuses + "> for topic <" + topic.getName() + ">.";
                    log.debug(msg);
                    job.appendLog(msg);

                    try {
                        jobService.save(job);
                    } catch (InvalidExecutionException e) {
                        log.error("Couldn't persist job object.", e);
                    }
                    jobService.stopStreamer(topic.getName());

                } catch (InvalidTopicException e) {
                    log.error("InvalidStreaming", e);
                } catch (InvalidExecutionException e) {
                    log.error("InvalidStreaming", e);
                }

                return;
            } else {
                stopping = false;
            }
        }

    }

    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

    }

    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

    }

    public void onScrubGeo(long userId, long upToStatusId) {

    }

    public void onStallWarning(StallWarning warning) {

    }

    public void onException(Exception ex) {
        if (!failure) {
            failure = true;
            // TODO Check what to do here. They sent an email
        }
        failure = true;
        // executionService.useNextAPIKey();
    }

}