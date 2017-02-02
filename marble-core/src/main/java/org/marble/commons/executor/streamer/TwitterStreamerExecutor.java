package org.marble.commons.executor.streamer;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.marble.commons.domain.model.TwitterApiKey;
import org.marble.commons.exception.InvalidExecutionException;
import org.marble.commons.service.DatastoreService;
import org.marble.commons.service.JobService;
import org.marble.commons.service.PostService;
import org.marble.commons.service.TopicService;
import org.marble.commons.service.TwitterApiKeyService;
import org.marble.commons.service.TwitterSearchService;
import org.marble.commons.service.TwitterStreamService;
import org.marble.model.domain.model.Job;
import org.marble.model.domain.model.Post;
import org.marble.model.domain.model.Topic;
import org.marble.model.model.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;

@Component
@Scope("singleton")
public class TwitterStreamerExecutor implements StreamerExecutor {

    private static final Logger log = LoggerFactory.getLogger(TwitterStreamerExecutor.class);

    @Autowired
    JobService jobService;

    @Autowired
    TopicService topicService;

    @Autowired
    PostService postService;

    @Autowired
    TwitterApiKeyService twitterApiKeyService;

    @Autowired
    DatastoreService datastoreService;

    @Autowired
    TwitterStreamService twitterStreamingService;

    Job job;

    Map<String, TwitterStreamingListener> twitterStreamingListeners;

    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    // Twitter API restrictions limits to one the available streams connected.
    TwitterStream twitterStream = null;

    @Override
    public void setJob(Job job) {
        this.job = job;
    }

    @Override
    public void run() {
        String msg = "";
        try {
            log.info("Initializing execution...");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        try {
            BigInteger id = job.getId();
            msg = "Starting twitter streaming extraction <" + id + ">.";
            log.info(msg);
            job.appendLog(msg);

            // Changing execution state
            job.setStatus(JobStatus.Running);
            jobService.save(job);

            // Get the associated topic
            Topic topic = topicService.findOne(job.getTopic().getName());

            // Get twitter keys
            List<TwitterApiKey> apiKeys = twitterApiKeyService.getEnabledTwitterApiKeys();
            for (TwitterApiKey key : apiKeys) {
                log.info("Key available: " + key);
            }

            Integer apiKeysCount = apiKeys.size();
            if (apiKeysCount == 0) {
                msg = "There are no Api Keys available. Aborting execution.";
                log.info(msg);
                job.appendLog(msg);
                job.setStatus(JobStatus.Aborted);
                jobService.save(job);
                return;
            }

            // The twitter stream is only created if it doesn't exists
            Integer apiKeysIndex = 0;
            if (twitterStream == null) {
                twitterStream = twitterStreamingService.configure(apiKeys.get(apiKeysIndex));
            }

            if (twitterStreamingListeners == null) {
                twitterStreamingListeners = new LinkedHashMap<String, TwitterStreamingListener>();
            }

            msg = "Extraction will begin with Api Key <" + apiKeys.get(apiKeysIndex).getDescription() + ">";
            log.info(msg);
            job.appendLog(msg);
            jobService.save(job);

            FilterQuery query = new FilterQuery();
            TwitterStreamingListener listener = new TwitterStreamingListener(topic, job, postService, jobService);

            // Adding the listener to the stream (we need to stop the stream
            // and then start it if needed)
            twitterStream.shutdown();
            twitterStream.addListener(listener);
            twitterStreamingListeners.put(topic.getName(), listener);

            String[] languages = getListenersLanguages();
            String[] keywords = getListenersKeywords();

            if (keywords.length > 0) {
                query = query.track(keywords);

                if (languages.length > 0) {
                    query = query.language(languages);
                }

                topic.setStreaming(Boolean.TRUE);
                topicService.save(topic);

                log.error("Adding query...");
                twitterStream.filter(query);
            } else {
                msg = "No keywords defined for the stream. It will not be restarted.";
                log.info(msg);
                job.appendLog(msg);
                job.setStatus(JobStatus.Aborted);
                job = jobService.save(job);
            }

        } catch (Exception e) {
            msg = "An error ocurred while manipulating execution <" + job.getId() + ">. Execution aborted.";
            log.error(msg, e);
            job.appendLog(msg);
            job.setStatus(JobStatus.Aborted);
            try {
                job = jobService.save(job);
            } catch (InvalidExecutionException e1) {
                log.error("Status couldn't be refreshed on the execution object.");
            }
            return;
        }

    }

    public void stopStreaming(Job job) {

        String msg = "";
        try {
            log.info("Initializing execution...");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        try {
            BigInteger id = job.getId();
            msg = "Stopping twitter streaming extraction <" + id + ">.";
            log.info(msg);
            job.appendLog(msg);
            job.setStatus(JobStatus.Running);
            jobService.save(job);

            Topic topic = topicService.findOne(job.getTopic().getName());
            FilterQuery query = new FilterQuery();

            TwitterStreamingListener listener = twitterStreamingListeners.get(topic.getName());
            Job listenerJob = listener.getJob();

            // Removing the listener from the stream (we need to stop the stream
            // and then start it if needed)
            twitterStream.shutdown();
            twitterStream.removeListener(listener);
            twitterStreamingListeners.remove(topic.getName());
            
            topic.setStreaming(Boolean.FALSE);
            topicService.save(topic);

            // Signaling the stopped listener job.
            msg = "Stopping the listener from job <" + job.getId() + ">.";
            log.info(msg);
            listenerJob.appendLog(msg);
            listenerJob.setStatus(JobStatus.Stopped);
            listenerJob = jobService.save(listenerJob);

            if (!twitterStreamingListeners.isEmpty()) {
                String[] languages = getListenersLanguages();
                String[] keywords = getListenersKeywords();

                if (keywords.length > 0) {
                    // Signaling the stopped listener job.
                    msg = "Restarting the stream with keywords <" + Arrays.toString(keywords) + ">.";
                    log.info(msg);
                    job.appendLog(msg);
                    job = jobService.save(job);

                    query = query.track(keywords);
                    if (languages.length > 0) {
                        query = query.language(languages);
                    }

                    topic.setStreaming(Boolean.TRUE);
                    topicService.save(topic);

                    twitterStream.filter(query);
                    msg = "Stop operation finished.";
                    log.info(msg);
                    job.appendLog(msg);
                } else {
                    msg = "No keywords defined for the stream. It will not be restarted.";
                    log.info(msg);
                    job.appendLog(msg);
                    job = jobService.save(job);
                }
            }
            else {
                twitterStream.cleanUp();
            }

            // Changing execution state
            msg = "Stop operation finished.";
            log.info(msg);
            job.appendLog(msg);
            job.setStatus(JobStatus.Stopped);
            job = jobService.save(job);
        } catch (Exception e) {
            msg = "An error ocurred while manipulating jobs <" + job.getId() + ">. Execution aborted.";
            log.error(msg, e);
            job.appendLog(msg);
            job.setStatus(JobStatus.Aborted);
            try {
                job = jobService.save(job);
            } catch (InvalidExecutionException e1) {
                log.error("An error occurred while persisting the job");
            }
            return;
        }
    }

    public String[] getListenersKeywords() {
        Set<String> keywords = new HashSet<String>();
        for (String topicName : twitterStreamingListeners.keySet()) {
            TwitterStreamingListener listener = twitterStreamingListeners.get(topicName);
            if (listener.getKeywords() != "")
                keywords.add(listener.getKeywords());

        }
        String[] result = {};
        return keywords.toArray(result);

    }

    public String[] getListenersLanguages() {
        Set<String> languages = new HashSet<String>();
        for (String topicName : twitterStreamingListeners.keySet()) {
            TwitterStreamingListener listener = twitterStreamingListeners.get(topicName);
            if (!"".equals(listener.getLanguage())) {
                languages.add(listener.getLanguage());
            }
        }
        String[] result = {};
        return languages.toArray(result);
    }
}
