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
import org.marble.commons.executor.processor.ProcessorExecutor;
import org.marble.commons.executor.processor.ProcessorExecutorImpl;
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
import org.marble.model.model.JobParameters;
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

            // Get the associated topic
            Topic topic = topicService.findOne(job.getTopic().getName());

            // Adding streamer processing parameters from topic
            if (topic.getStreamerProcessParameters() != null) {
                Set<JobParameters> parameters = topic.getStreamerProcessParameters();
                // Remove filter, if any
                for (JobParameters parameter : parameters) {
                    if (parameter.getName().equals(ProcessorExecutor.MARBLE_FILTER)) {
                        parameters.remove(parameter);
                    }
                }
                job.setParameters(parameters);
            }

            jobService.save(job);

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
            double[][] locations = getLocations();

            if (keywords.length > 0 || locations.length > 0) {

                if (keywords.length > 0) {
                    log.info("Adding " + keywords.length + " keywords.");
                    query = query.track(keywords);
                }

                if (languages.length > 0) {
                    log.info("Adding " + languages.length + " languages.");
                    query = query.language(languages);
                }

                if (locations.length > 0) {
                    log.info("Adding " + locations.length + " locations.");
                    query = query.locations(locations);
                }

                topic.setStreaming(Boolean.TRUE);
                topicService.save(topic);

                log.info("Adding query...");
                twitterStream.filter(query);
            } else {
                msg = "No keywords or locations defined for the stream. It will not be restarted.";
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

            topic.setStreaming(Boolean.FALSE);
            topicService.save(topic);

            // Changing execution state
            msg = "Stop operation finished.";
            log.info(msg);
            job.appendLog(msg);
            job.setStatus(JobStatus.Stopped);
            job = jobService.save(job);

            FilterQuery query = new FilterQuery();

            TwitterStreamingListener listener = twitterStreamingListeners.get(topic.getName());

            // Removing the listener from the stream (we need to stop the stream
            // and then start it if needed)
            twitterStream.shutdown();
            twitterStream.removeListener(listener);
            twitterStreamingListeners.remove(topic.getName());

            if (!twitterStreamingListeners.isEmpty()) {
                String[] languages = getListenersLanguages();
                String[] keywords = getListenersKeywords();
                double[][] locations = getLocations();

                if (keywords.length > 0 || locations.length > 0) {

                    // Signaling the stopped listener job.
                    msg = "Restarting the stream with keywords <" + Arrays.toString(keywords) + ">.";
                    log.info(msg);
                    job.appendLog(msg);
                    job = jobService.save(job);

                    if (keywords.length > 0) {
                        query = query.track(keywords);
                    }

                    if (languages.length > 0) {
                        query = query.language(languages);
                    }

                    if (locations.length > 0) {
                        query = query.locations(locations);
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
            } else {
                twitterStream.cleanUp();
            }

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
            if (listener.getKeywords() != null && listener.getKeywords().length > 0)
                keywords.addAll(Arrays.asList(listener.getKeywords()));

        }
        String[] result = {};
        return keywords.toArray(result);

    }

    public String[] getListenersLanguages() {
        Set<String> languages = new HashSet<String>();
        for (String topicName : twitterStreamingListeners.keySet()) {
            TwitterStreamingListener listener = twitterStreamingListeners.get(topicName);
            if (listener.getLanguage() != null && listener.getLanguage() != "") {
                languages.add(listener.getLanguage());
            }
        }
        String[] result = {};
        return languages.toArray(result);
    }

    public double[][] getLocations() {
        ArrayList<double[]> locations = new ArrayList<double[]>();
        for (String topicName : twitterStreamingListeners.keySet()) {
            TwitterStreamingListener listener = twitterStreamingListeners.get(topicName);
            ArrayList<double[]> listenerLocation = listener.getLocations();
            if (listenerLocation != null) {
                locations.addAll(listenerLocation);
            }
        }
        double[][] result = {  };
        double[][] returnResult = locations.toArray(result);
        return returnResult;
    }
}
