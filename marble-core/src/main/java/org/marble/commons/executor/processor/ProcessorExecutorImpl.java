package org.marble.commons.executor.processor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.marble.commons.domain.model.Job;
import org.marble.commons.domain.model.Post;
import org.marble.commons.domain.model.ProcessedPost;
import org.marble.commons.domain.model.Topic;
import org.marble.commons.domain.model.ValidationItem;
import org.marble.commons.exception.InvalidExecutionException;
import org.marble.commons.model.Constants;
import org.marble.commons.model.ExecutorParameter;
import org.marble.commons.model.JobStatus;
import org.marble.commons.model.ProcessParameters;
import org.marble.commons.model.SymplifiedProcessingItem;
import org.marble.commons.model.ValidationResult;
import org.marble.commons.service.DatastoreService;
import org.marble.commons.service.JobService;
import org.marble.commons.service.ProcessedPostService;
import org.marble.commons.service.SenticNetService;
import org.marble.commons.service.TopicService;
import org.marble.commons.service.processor.ProcessorService;
import org.marble.model.domain.ProcessorInput;
import org.marble.model.domain.ProcessorOutput;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ProcessorExecutorImpl implements ProcessorExecutor {

    private static final Logger log = LoggerFactory.getLogger(ProcessorExecutorImpl.class);

    public static final String id = ProcessorExecutorImpl.class.getSimpleName();

    public static final String label = "Executing";

    private enum OperationType {
        REGULAR
    }

    @Autowired
    JobService executionService;

    @Autowired
    TopicService topicService;

    @Autowired
    DatastoreService datastoreService;

    @Autowired
    ProcessedPostService processedPostService;

    @Autowired
    private ApplicationContext context;

    private Job execution;

    // Custom parameters
    private Boolean ignoreNeutralSentences = Boolean.FALSE;

    @Override
    public void setExecution(Job execution) {
        this.execution = execution;
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

            BigInteger id = execution.getId();

            msg = "Starting Bag of Words Sentic processor <" + id + ">.";
            log.info(msg);
            execution.appendLog(msg);

            // Changing execution state
            execution.setStatus(JobStatus.Running);
            execution = executionService.save(execution);

            if (execution.getTopic() != null) {
                process();
            } else {
                // validate();
            }

        } catch (Exception e) {
            msg = "An error ocurred while processing posts with execution <" + execution.getId() + ">. Execution aborted.";
            log.error(msg, e);
            execution.appendLog(msg);
            execution.setStatus(JobStatus.Aborted);
            try {
                execution = executionService.save(execution);
            } catch (InvalidExecutionException e1) {
                log.error("Post couldn't be refreshed on the execution object.");
            }

            return;
        }
    }

    private void process() throws InvalidExecutionException {

        // Get the associated topic
        Topic topic = execution.getTopic();

        String msg;

        DBCursor processingItemsCursor;

        // This is a regular process

        // Drop current processed posts
        processedPostService.deleteByTopicName(topic.getName());

        log.info("Getting posts for topic <" + topic.getName() + ">.");
        processingItemsCursor = datastoreService.findCursorByTopicName(topic.getName(), Post.class);

        log.info("There are <" + processingItemsCursor.count() + "> items to process.");
        while (processingItemsCursor.hasNext()) {
            DBObject rawStatus = processingItemsCursor.next();

            Post post = datastoreService.getConverter().read(Post.class, rawStatus);
            if (post.getCreatedAt() == null) {
                continue;
            }
            String text = post.getText();
            if (text == null) {
                log.debug("Post text for id <" + post.getId() + "> is null. Skipping...");
                continue;
            }
            ProcessedPost processedPost = new ProcessedPost(post);
            datastoreService.save(processedPost);
        }

        // Loop for each processing stage
        for (ProcessParameters parameter : this.execution.getParameters()) {

            String processorName = parameter.getName();
            msg = "Starting processor <" + processorName + ">";
            log.info(msg);
            execution.appendLog(msg);

            ProcessorService processorService = null;
            try {
                processorService = (ProcessorService) context.getBean(processorName);
            } catch (NoSuchBeanDefinitionException e) {
                msg = "Processor <" + processorName + "> doesn't exists. Skipping.";
                log.error(msg, e);
                execution.appendLog(msg);
                continue;
            } catch (Exception e) {
                msg = "An error ocurred while using processor <" + processorName + ">. Skipping.";
                log.error(msg, e);
                execution.appendLog(msg);
                continue;
            }

            // Get Processed Statuses
            log.info("Getting processed posts for topic <" + topic.getName() + ">.");
            processingItemsCursor = datastoreService.findCursorByTopicName(topic.getName(), ProcessedPost.class);

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

                ProcessorOutput output = null;
                output = processorService.processMessage(input);

                if (output.getMessage() != null) {
                    processedPost.setText(output.getMessage());
                    log.debug("Modified message for  for text <" + initialMessage.replaceAll("\n", "") + "> is <" + processedPost.getText().replaceAll("\n", "") + ">");
                }
                if (output.getPolarity() != null) {
                    processedPost.setPolarity(output.getPolarity());
                    log.debug("Polarity for text <" + initialMessage.replaceAll("\n", "") + "> is <" + processedPost.getPolarity() + ">");
                }
                processedPost.appendProcessorNotes(output.getNotes());

                datastoreService.save(processedPost);

                count++;

                if ((count % 100) == 0) {
                    msg = "Items processed so far: <" + count + ">";
                    log.info(msg);
                    execution.appendLog(msg);
                    executionService.save(execution);
                }
            }

            msg = "Total of items processed: <" + count + ">";
            log.info(msg);
            execution.appendLog(msg);

        }
        msg = "The processor operation for topic <" + topic.getName() + "> has finished.";
        log.info(msg);
        execution.appendLog(msg);
        execution.setStatus(JobStatus.Stopped);

        execution = executionService.save(execution);
    }

    /*
     * public void validate() throws InvalidExecutionException {
     * // TODO Include ignore neutral sentences from global configuration
     * this.ignoreNeutralSentences = Boolean.FALSE;
     * 
     * String msg = "";
     * 
     * ValidationResult validationResult = new ValidationResult();
     * 
     * // Define Boundaries
     * Float positiveBoundary, negativeBoundary;
     * try {
     * positiveBoundary = Float.parseFloat(this.execution.getModuleParameters().getParameters().get("positiveBoundary"));
     * } catch (Exception e) {
     * positiveBoundary = 0F;
     * 
     * this.execution.getModuleParameters().getParameters().put("positiveBoundary", positiveBoundary.toString());
     * }
     * try {
     * negativeBoundary = Float.parseFloat(this.execution.getModuleParameters().getParameters().get("negativeBoundary"));
     * } catch (Exception e) {
     * negativeBoundary = 0F;
     * this.execution.getModuleParameters().getParameters().put("negativeBoundary", negativeBoundary.toString());
     * }
     * 
     * DBCursor processingItemsCursor;
     * 
     * // This is a validation
     * processingItemsCursor = datastoreService.findCursorForAll(ValidationItem.class);
     * 
     * log.info("There are <" + processingItemsCursor.count() + "> items to validate.");
     * 
     * Integer count = 0;
     * while (processingItemsCursor.hasNext()) {
     * 
     * DBObject rawStatus = processingItemsCursor.next();
     * 
     * ValidationItem item = datastoreService.getConverter().read(ValidationItem.class, rawStatus);
     * SymplifiedProcessingItem symplifiedProcessingItem = item.getSymplifiedProcessingItem();
     * 
     * log.debug("Item text is <" + symplifiedProcessingItem.getText() + ">");
     * if (symplifiedProcessingItem.getCreatedAt() == null) {
     * continue;
     * }
     * String text = symplifiedProcessingItem.getText();
     * if (text == null) {
     * log.debug("Post text for id <" + symplifiedProcessingItem.getId() + "> is null. Skipping...");
     * continue;
     * }
     * log.debug("Analysing text: " + text.replaceAll("\n", ""));
     * 
     * Float polarity = this.processStatus(text);
     * 
     * log.debug("Polarity for text <" + text.replaceAll("\n", "") + "> is <" + polarity + ">");
     * 
     * // In case of validation, results will be shown in screen and in
     * // counters
     * if (polarity > positiveBoundary) {
     * validationResult.addPositiveResult(symplifiedProcessingItem.getExpectedPolarity());
     * } else if (polarity < negativeBoundary) {
     * validationResult.addNegativeResult(symplifiedProcessingItem.getExpectedPolarity());
     * } else {
     * validationResult.addNeutralResult(symplifiedProcessingItem.getExpectedPolarity());
     * }
     * msg = "Polarity: " + polarity + ". Expected: " + symplifiedProcessingItem.getExpectedPolarity() + ".";
     * log.debug(msg);
     * 
     * count++;
     * 
     * if ((count % 100) == 0) {
     * msg = "Items processed so far: <" + count + ">";
     * log.info(msg);
     * execution.appendLog(msg);
     * executionService.save(execution);
     * }
     * }
     * 
     * msg = "Total of items processed: <" + count + ">";
     * log.info(msg);
     * execution.appendLog(msg);
     * 
     * msg = validationResult.getResults();
     * log.info(msg);
     * execution.appendLog(msg);
     * msg = "The bag of words sentic processor validation has finished.";
     * 
     * log.info(msg);
     * execution.appendLog(msg);
     * execution.setStatus(JobStatus.Stopped);
     * 
     * execution = executionService.save(execution);
     * 
     * }
     * 
     */

}