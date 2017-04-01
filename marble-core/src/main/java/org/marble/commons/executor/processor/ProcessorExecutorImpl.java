package org.marble.commons.executor.processor;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.marble.commons.exception.InvalidExecutionException;
import org.marble.commons.service.DatastoreService;
import org.marble.commons.service.JobService;
import org.marble.commons.service.ProcessedPostService;
import org.marble.commons.service.TopicService;
import org.marble.model.domain.model.Job;
import org.marble.model.domain.model.Post;
import org.marble.model.domain.model.ProcessedPost;
import org.marble.model.domain.model.Topic;
import org.marble.model.model.JobParameters;
import org.marble.model.model.JobStatus;
import org.marble.model.model.JobType;
import org.marble.model.model.ProcessedPostStep;
import org.marble.model.model.ProcessorInput;
import org.marble.model.model.ProcessorOutput;
import org.marble.util.MarbleUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ProcessorExecutorImpl implements ProcessorExecutor {

    private static final Logger log = LoggerFactory.getLogger(ProcessorExecutorImpl.class);

    public static final String id = ProcessorExecutorImpl.class.getSimpleName();

    public static final String label = "Executing";

    @Autowired
    JobService jobService;

    @Autowired
    TopicService topicService;

    @Autowired
    DatastoreService datastoreService;

    @Autowired
    ProcessedPostService processedPostService;

    @Autowired
    private DiscoveryClient discoveryClient;

    private Job job;

    private Set<JobParameters> extraParameters;

    @Override
    public void setJob(Job job) {
        this.job = job;
    }

    @Override
    public void setExtraParameters(Set<JobParameters> extraParameters) {
        this.extraParameters = extraParameters;
    }

    @Override
    public void run() {
        try {
            log.info("Initializing execution...");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        try {

            BigInteger id = job.getId();

            logMsg("Starting processor <" + id + ">.", "info", null);

            // Changing execution state
            job.setStatus(JobStatus.Running);
            job = jobService.save(job);

            if (job.getTopic() != null) {
                process();
            } else {
                // validate();
            }

        } catch (Exception e) {
            logMsg("An error ocurred while processing posts with execution <" + job.getId() + ">. Execution aborted.", "error", e);
            job.setStatus(JobStatus.Aborted);
            try {
                job = jobService.save(job);
            } catch (InvalidExecutionException e1) {
                log.error("Post couldn't be refreshed on the execution object.");
            }

            return;
        }
    }

    private void process() throws InvalidExecutionException {

        // Get the associated topic
        Topic topic = job.getTopic();

        String msg;

        Gson gson = new GsonBuilder().create();

        DBCursor processingItemsCursor;

        // This is a regular process

        // Drop current processed posts
        // processedPostService.deleteByTopicName(topic.getName());

        logMsg("Getting posts for topic <" + topic.getName() + ">.", "info", null);

        // Add extraparameters, if any
        Set<JobParameters> parameters = new HashSet<>();
        if (extraParameters != null) {
            parameters.addAll(extraParameters);
        }
        parameters.addAll(this.job.getParameters());

        // Check for any filters in the job parameters
        Map<String, Object> filterOptions = new HashMap<>();

        for (JobParameters parameter : parameters) {
            if (parameter.getName().equals(MARBLE_FILTER)) {
                filterOptions = parameter.getOptions();
                break;
            }
        }
        processingItemsCursor = this.getCursorUsingFilters(topic.getName(), Post.class, filterOptions);

        logMsg("There are <" + processingItemsCursor.count() + "> items to process.", "info", null);

        while (processingItemsCursor.hasNext()) {
            DBObject rawStatus = processingItemsCursor.next();

            Post post = datastoreService.getConverter().read(Post.class, rawStatus);
            if (post.getCreatedAt() == null) {
                continue;
            }
            String text = post.getText();
            if (text == null) {
                logMsg("Post text for id <" + post.getOriginalId() + "> is null. Skipping...", "debug", null);
                continue;
            }
            ProcessedPost processedPost = new ProcessedPost(post);
            datastoreService.save(processedPost);
        }

        // Loop for each processing stage
        for (JobParameters parameter : parameters) {

            if (parameter.getName().equals(MARBLE_FILTER)) {
                continue;
            }

            String processorName = parameter.getName();
            logMsg("Starting processor <" + processorName + ">", "info", null);

            // String serviceUrl = "http://" + processorName + "/api/process";

            List<ServiceInstance> serviceInstances = discoveryClient.getInstances(processorName);

            if (serviceInstances.size() <= 0) {
                logMsg("No instances available for processor <" + processorName + ">. Skipping.", "error", null);
                continue;
            }
            Map<String, Integer> serviceInstancesRunning = new HashMap<>();

            for (ServiceInstance serviceInstance : serviceInstances) {
                serviceInstancesRunning.put(serviceInstance.getUri().toString(), 0);
            }

            // Get Processed Statuses
            log.info("Getting processed posts for topic <" + topic.getName() + ">.");
            processingItemsCursor = this.getCursorUsingFilters(topic.getName(), ProcessedPost.class, filterOptions);

            log.info("There are <" + processingItemsCursor.count() + "> items to process.");
            Integer count = 0;
            while (processingItemsCursor.hasNext()) {

                DBObject rawStatus = processingItemsCursor.next();

                ProcessedPost processedPost = datastoreService.getConverter().read(ProcessedPost.class, rawStatus);

                String initialMessage = processedPost.getText();
                log.debug("Analysing text: " + initialMessage.replaceAll("\n", ""));

                ProcessorInput input = new ProcessorInput();
                input.setMessage(initialMessage);
                input.setOptions(parameter.getOptions());

                ProcessedPostStep step = new ProcessedPostStep();
                step.setInputText(initialMessage);

                String serviceInstanceAvailableUrl = null;
                do {
                    for (String serviceInstanceUrl : serviceInstancesRunning.keySet()) {
                        if (serviceInstancesRunning.get(serviceInstanceUrl) < 1) {
                            serviceInstanceAvailableUrl = serviceInstanceUrl;
                            serviceInstancesRunning.put(serviceInstanceUrl, serviceInstancesRunning.get(serviceInstanceUrl) + 1);
                            break;
                        }
                    }
                    if (serviceInstanceAvailableUrl == null) {
                        log.info("Waiting for an instance for processor <" + processorName + ">.");
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            log.error("Error sleeping...", e);
                        }
                    }
                } while (serviceInstanceAvailableUrl == null);
                log.info("Instance selected: <" + serviceInstanceAvailableUrl + ">");
                final String url = new String(serviceInstanceAvailableUrl);
                AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
                
                @SuppressWarnings("unused")
                CompletableFuture<Response> promise = asyncHttpClient.preparePost(serviceInstanceAvailableUrl + "/api/process").setBody(gson.toJson(input))
                        .setHeader("Content-Type", "application/json").execute().toCompletableFuture().thenApply(resp -> {
                            serviceInstancesRunning.put(url, serviceInstancesRunning.get(url) - 1);
                            try {

                                ProcessorOutput output = null;
                                output = gson.fromJson(resp.getResponseBody(), ProcessorOutput.class);
                                if (output != null) {
                                    if (output.getMessage() != null) {
                                        step.setOutputText(output.getMessage());
                                        log.debug("Modified message for  for text <" + initialMessage.replaceAll("\n", "") + "> is <" + processedPost.getText().replaceAll("\n", "")
                                                + ">");
                                    }
                                    if (output.getPolarity() != null) {
                                        step.setPolarity(output.getPolarity());
                                        log.debug("Polarity for text <" + initialMessage.replaceAll("\n", "") + "> is <" + processedPost.getPolarity() + ">");
                                    }
                                    step.setProcessParameters(parameter);
                                    step.setNotes(output.getNotes());
                                }
                                processedPost.addStep(step);
                                datastoreService.save(processedPost);
                                asyncHttpClient.close();
                            } catch (JsonSyntaxException | IOException e) {
                                // TODO Auto-generated catch block
                                logMsg("Message <" + input.getMessage() + "> couldn't be processed.", "error", e);
                            }
                            return resp;
                        });

                count++;

                if ((count % 100) == 0) {
                    msg = "Items processed so far: <" + count + ">";
                    log.info(msg);
                    job.appendLog(msg);
                    jobService.save(job);
                }
            }

            msg = "Total of items processed: <" + count + ">";
            log.info(msg);
            job.appendLog(msg);

        }
        msg = "The processor operation for topic <" + topic.getName() + "> has finished.";
        log.info(msg);
        job.appendLog(msg);
        if (job.getType().equals(JobType.Processor)) {
            job.setStatus(JobStatus.Stopped);
        }

        job = jobService.save(job);
    }

    private <T> DBCursor getCursorUsingFilters(String topicName, Class<T> classToSearch, Map<String, Object> filterOptions) {
        DBCursor cursor;
        if (filterOptions.containsKey(MARBLE_FILTER_FROM_DATE) || filterOptions.containsKey(MARBLE_FILTER_TO_DATE)) {
            Date fromDate = null;
            Date toDate = null;
            if (filterOptions.get(MARBLE_FILTER_FROM_DATE) != null) {
                fromDate = MarbleUtil.convertStringToDate((String) filterOptions.get(MARBLE_FILTER_FROM_DATE));
            }
            if (filterOptions.get(MARBLE_FILTER_TO_DATE) != null) {
                toDate = MarbleUtil.convertStringToDate((String) filterOptions.get(MARBLE_FILTER_TO_DATE));
            }
            cursor = datastoreService.findCursorByTopicNameAndBetweenDates(topicName, fromDate, toDate, classToSearch);

        } else if (filterOptions.containsKey(MARBLE_FILTER_FROM_ID) || filterOptions.containsKey(MARBLE_FILTER_TO_ID)) {
            Long fromId = null;
            Long toId = null;
            if (filterOptions.get(MARBLE_FILTER_FROM_ID) != null) {
                fromId = Long.parseLong((String) filterOptions.get(MARBLE_FILTER_FROM_ID));
            }
            if (filterOptions.get(MARBLE_FILTER_TO_ID) != null) {
                toId = Long.parseLong((String) filterOptions.get(MARBLE_FILTER_TO_ID));
            }
            cursor = datastoreService.findCursorByTopicNameAndBetweenIds(topicName, fromId, toId, classToSearch);
        } else {
            cursor = datastoreService.findCursorByTopicName(topicName, classToSearch);
        }
        return cursor;
    }

    public void logMsg(String message, String level, Exception exception) {
        if (level != null) {
            switch (level) {
            case "error":
                if (exception == null)
                    log.error(message);
                else
                    log.error(message, exception);
                break;
            case "warn":
                if (exception == null)
                    log.warn(message);
                else
                    log.warn(message, exception);
                break;
            case "debug":
                if (exception == null)
                    log.debug(message);
                else
                    log.debug(message, exception);
                break;
            case "trace":
                if (exception == null)
                    log.trace(message);
                else
                    log.trace(message, exception);
                break;
            case "info":
            default:
                if (exception == null)
                    log.info(message);
                else
                    log.info(message, exception);
                break;
            }
        } else {
            if (exception == null)
                log.info(message);
            else
                log.info(message, exception);
        }
        job.appendLog(message);
    }
}