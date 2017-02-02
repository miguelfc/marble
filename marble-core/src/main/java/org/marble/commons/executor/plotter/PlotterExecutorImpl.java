package org.marble.commons.executor.plotter;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.marble.commons.exception.InvalidExecutionException;
import org.marble.commons.service.DatastoreService;
import org.marble.commons.service.JobService;
import org.marble.commons.service.ChartService;
import org.marble.commons.service.ProcessedPostService;
import org.marble.commons.service.TopicService;
import org.marble.model.domain.model.Job;
import org.marble.model.domain.model.Topic;
import org.marble.model.domain.model.Chart;
import org.marble.model.model.JobParameters;
import org.marble.model.model.JobStatus;
import org.marble.model.model.PlotterInput;
import org.marble.model.model.PlotterOutput;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class PlotterExecutorImpl implements PlotterExecutor {

    private static final Logger log = LoggerFactory.getLogger(PlotterExecutorImpl.class);

    public static final String id = PlotterExecutorImpl.class.getSimpleName();

    public static final String label = "Executing";

    @Autowired
    JobService executionService;

    @Autowired
    TopicService topicService;

    @Autowired
    ChartService plotService;
    
    @Autowired
    DatastoreService datastoreService;

    @Autowired
    private EurekaClient discoveryClient;

    private Job execution;

    @Override
    public void setJob(Job execution) {
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

            logMsg("Starting plotter <" + id + ">.", "info", null);

            // Changing execution state
            execution.setStatus(JobStatus.Running);
            execution = executionService.save(execution);

            if (execution.getTopic() != null) {
                plot();
            }

        } catch (Exception e) {
            logMsg("An error ocurred while plotting posts with execution <" + execution.getId() + ">. Execution aborted.", "error", e);
            execution.setStatus(JobStatus.Aborted);
            try {
                execution = executionService.save(execution);
            } catch (InvalidExecutionException e1) {
                log.error("Post couldn't be refreshed on the execution object.");
            }

            return;
        }
    }

    private void plot() throws InvalidExecutionException {

        // Get the associated topic
        Topic topic = execution.getTopic();

        String msg;

        Gson gson = new GsonBuilder().create();

        List<Chart> chartsList = new ArrayList<>();
        
        // Loop for each processing stage
        for (JobParameters parameter : this.execution.getParameters()) {

            String processorName = parameter.getName();
            logMsg("Starting plotter <" + processorName + ">", "info", null);

            
            InstanceInfo hostInfo = null;
            try {
                hostInfo = discoveryClient.getNextServerFromEureka(processorName, Boolean.FALSE);
            }
            catch (Exception e) {
                logMsg("Service <"+processorName+"> doesn't exists. Skipping.", "warn", null);
            }
            String serviceUrl = hostInfo.getHomePageUrl() + "api/plot";
            log.error("Home page: " + serviceUrl);

            AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();

            PlotterInput input = new PlotterInput();
            input.setTopicName(topic.getName());
            input.setOptions(parameter.getOptions());
            
            

            final String url = new String(serviceUrl);
            CompletableFuture<Response> promise = asyncHttpClient
                    .preparePost(url)
                    .setBody(gson.toJson(input))
                    .setHeader("Content-Type", "application/json")
                    .execute()
                    .toCompletableFuture()
                    .thenApply(resp -> {
                        try {
                            log.error("AQui voy: " + resp.getResponseBody());
                            PlotterOutput output = null;
                            output = gson.fromJson(resp.getResponseBody(), PlotterOutput.class);
                            if (output != null) {
                                List<Chart> temporaryChartsList = output.getCharts();
                                for (Chart chart : temporaryChartsList) {
                                    chart.setJobId(execution.getId());
                                    chart.setTopic(topic);
                                    try {
                                        plotService.save(chart);
                                    } catch (Exception e) {
                                        logMsg("Chart for topic <" + topic.getName() + "> couldn't be saved.", "error", e);
                                    }
                                    chartsList.add(chart);
                                }
                                // TODO Create plot object
                            }
                        } catch (JsonSyntaxException e) {
                            // TODO Auto-generated catch block
                            logMsg("Topic <" + topic.getName() + "> couldn't be plotted.", "error", e);
                        }
                        try {
                            asyncHttpClient.close();
                        } catch (IOException e) {
                            logMsg("An error occurred while closing asyncHttpClient .", "error", e);
                        }
                        return resp;
                    })
                    .exceptionally((t) -> {logMsg("An error occurred while using plotter.", "error", t);return null;});

            // Force the operation to be synchronous
            promise.join();
            
            /*
            try {
                asyncHttpClient.close();
            } catch (IOException e) {
                logMsg("An error occurred while closing asyncHttpClient.", "error", e);
            }
            */

        }
        msg = "The plotter operation for topic <" + topic.getName() + "> has finished.";
        log.info(msg);
        execution.appendLog(msg);
        // TODO
        execution.setCharts(chartsList);
        execution.setStatus(JobStatus.Stopped);

        execution = executionService.save(execution);
    }

    public void logMsg(String message, String level, Throwable exception) {
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
        execution.appendLog(message);
    }

}