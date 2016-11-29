package org.marble.commons.executor.plotter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.marble.commons.domain.model.ProcessedPost;
import org.marble.commons.domain.model.Topic;
import org.marble.commons.exception.InvalidExecutionException;
import org.marble.commons.model.ExecutorParameter;
import org.marble.commons.service.DatastoreService;
import org.marble.commons.service.JobService;
import org.marble.commons.service.PlotService;
import org.marble.model.domain.model.Job;
import org.marble.model.domain.model.Plot;
import org.marble.model.model.JobStatus;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Scope("prototype")
public class ProcessedPostsPlotterExecutor implements PlotterExecutor {

    private static final Logger log = LoggerFactory.getLogger(ProcessedPostsPlotterExecutor.class);

    private static final String COLUMN_CHART = "ColumnChart";

    public static final String id = ProcessedPostsPlotterExecutor.class.getSimpleName();

    public static final String label = "Plotter for processed posts";

    public static final List<ExecutorParameter> operations;

    public static final List<ExecutorParameter> parameters;

    static {
        List<ExecutorParameter> availableOperations = new ArrayList<>();
        availableOperations.add(new ExecutorParameter("plotAllPosts", "All the extracted posts by polarity over time."));
        availableOperations.add(new ExecutorParameter("plotCreatedPosts", "Only created posts by polarity over time."));
        availableOperations.add(new ExecutorParameter("plotRetweetedPosts", "Only retweeted posts by polarity over time."));
        availableOperations.add(new ExecutorParameter("plotAveragePolarity", "The average polarity of posts over time."));
        availableOperations.add(new ExecutorParameter("plotTotalDifference", "The difference in count of positive and negative posts over time."));
        operations = Collections.unmodifiableList(availableOperations);
    }

    static {

        List<ExecutorParameter> availableParameters = new ArrayList<>();
        parameters = Collections.unmodifiableList(availableParameters);
    }
    
    private enum PlotType
    {
        ALL,
        CREATED,
        RETWEETED,
        AVERAGE_POLARITY,
        TOTAL_RATIO
    }

    private enum DataType
    {
        ALL_POSITIVE,
        ALL_NEGATIVE,
        CREATED_POSITIVE,
        CREATED_NEGATIVE,
        RETWEETED_POSITIVE,
        RETWEETED_NEGATIVE,
        AVERAGE_POLARITY,
        RATIO
    }

    @Autowired
    private DatastoreService datastoreService;

    @Autowired
    private JobService jobService;

    @Autowired
    private PlotService plotService;

    private Job job;

    @Override
    public void setExecution(Job execution) {
        this.job = execution;
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

            msg = "Starting plotter <" + id + ">.";
            log.info(msg);
            job.appendLog(msg);

            // Changing execution state
            job.setStatus(JobStatus.Running);
            job = jobService.save(job);

            // Old invocation style
            // Method method =
            // this.getClass().getMethod(this.execution.getModuleParameters().getOperation());
            // method.invoke(this, new Object[] {});
            switch (this.job.getModuleParameters().getOperation()) {
            case "plotAllPosts":
                plot(PlotType.ALL);
                break;
            case "plotCreatedPosts":
                plot(PlotType.CREATED);
                break;
            case "plotRetweetedPosts":
                plot(PlotType.RETWEETED);
                break;
            case "plotAveragePolarity":
                plot(PlotType.AVERAGE_POLARITY);
                break;
            case "plotTotalDifference":
                plot(PlotType.TOTAL_RATIO);
                break;
            }

        } catch (Exception e) {
            msg = "An error ocurred while generating plot with execution <" + job.getId()
                    + ">. Execution aborted.";
            log.error(msg, e);
            job.appendLog(msg);
            job.setStatus(JobStatus.Aborted);
            try {
                job = jobService.save(job);
            } catch (InvalidExecutionException e1) {
                log.error("Post couldn't be refreshed on the execution object.");
            }
            return;
        }
    }

    private void plot(PlotType plotType) throws InvalidExecutionException {
        String msg = "";
        Topic topic = job.getTopic();

        log.info("Creating plot...");

        // Here starts the execution
        Plot plot = new Plot();
        plot.setName(job.getModuleParameters().getName());
        plot.setDescription(job.getModuleParameters().getDescription());
        plot.setTopic(topic);

        plot.setType(COLUMN_CHART);
        
        Map<String, Object> singleData = new HashMap<>();
        singleData.put("cols", getPostsColumns(topic, plotType));
        
        switch (plotType) {
        case ALL: {
            List<Map<String, List<Map<String, Double>>>> positive = getPostsRows(topic, DataType.ALL_POSITIVE);
            List<Map<String, List<Map<String, Double>>>> negative = getPostsRows(topic, DataType.ALL_NEGATIVE);

            singleData.put("rows", mixAndMatch(positive, negative));
        }
            break;
        case CREATED: {
            List<Map<String, List<Map<String, Double>>>> positive = getPostsRows(topic, DataType.CREATED_POSITIVE);
            List<Map<String, List<Map<String, Double>>>> negative = getPostsRows(topic, DataType.CREATED_NEGATIVE);

            singleData.put("rows", mixAndMatch(positive, negative));
        }
            break;
        case RETWEETED: {
            List<Map<String, List<Map<String, Double>>>> positive = getPostsRows(topic, DataType.RETWEETED_POSITIVE);
            List<Map<String, List<Map<String, Double>>>> negative = getPostsRows(topic, DataType.RETWEETED_NEGATIVE);

            singleData.put("rows", mixAndMatch(positive, negative));
        }
            break;
        case AVERAGE_POLARITY: {
            singleData.put("rows", getCalculatedDataRows(topic, DataType.AVERAGE_POLARITY));
        }
            break;
        case TOTAL_RATIO:
            singleData.put("rows", getCalculatedDataRows(topic, DataType.RATIO));
            break;
        }

        plot.setData(singleData);
        plot.setOptions(getOptions(topic, plotType));
        
        plot.setJobId(job.getId());
        try {
            plot = plotService.save(plot);
            job.setPlot(plot);
            job = jobService.save(job);
        } catch (Exception e) {
            msg = "Couldn't create the plot. Aborting the operation.";
            log.error(msg, e);
            job.appendLog(msg);
            job.setStatus(JobStatus.Aborted);
            job.setPlot(null);
            job = jobService.save(job);
            throw new InvalidExecutionException(msg,e);
        }

        // Here finishes the execution
        msg = "Plot generation has finished. The new plot was assigned the id <" + job.getPlot().getId() + ">.";
        log.info(msg);
        job.appendLog(msg);
        // execution.setPlot(plot);
        job.setStatus(JobStatus.Stopped);
        job = jobService.save(job);
    }

    private List<Map<String, Object>> getPostsColumns(Topic topic, PlotType plotType) {
        List<Map<String, Object>> cols = new ArrayList<>();

        Map<String, Object> col = null;

        col = new HashMap<>();
        col.put("id", "time");
        col.put("label", "Time");
        col.put("type", "datetime");
        cols.add(col);

        switch (plotType) {
        case ALL: {
            col = new HashMap<>();
            col.put("id", "positive");
            col.put("label", "Positive Posts");
            col.put("type", "number");
            cols.add(col);
            
            col = new HashMap<>();
            col.put("id", "negative");
            col.put("label", "Negative Posts");
            col.put("type", "number");
            cols.add(col);
        }
            break;
        case CREATED: {
            col = new HashMap<>();
            col.put("id", "positive");
            col.put("label", "Positive Original Posts");
            col.put("type", "number");
            cols.add(col);
            
            col = new HashMap<>();
            col.put("id", "negative");
            col.put("label", "Negative Original Posts");
            col.put("type", "number");
            cols.add(col);
        }
            break;
        case RETWEETED: {
            col = new HashMap<>();
            col.put("id", "positive");
            col.put("label", "Positive Retweets");
            col.put("type", "number");
            cols.add(col);
            
            col = new HashMap<>();
            col.put("id", "negative");
            col.put("label", "Negative Retweets");
            col.put("type", "number");
            cols.add(col);
        }
            break;
        case AVERAGE_POLARITY: {
            col = new HashMap<>();
            col.put("id", "originals");
            col.put("label", "Average Polarity");
            col.put("type", "number");
            cols.add(col);
        }
            break;
        case TOTAL_RATIO:
            col = new HashMap<>();
            col.put("id", "users");
            col.put("label", "Positive Negative Ratio");
            col.put("type", "number");
            cols.add(col);
        }
        return cols;
    }
    
    private List<Map<String, List<Map<String, Double>>>> getPostsRows(Topic topic, DataType dataType) {
        // Get horizontal boundaries
        long leftDateBoundary = 0;
        long rightDateBoundary = 0;

        Long stepSize = topic.getPlotterStepSize();

        try {
            leftDateBoundary = getBoundary(topic.getPlotterLeftDateBoundary(), stepSize);
            rightDateBoundary = getBoundary(topic.getPlotterRightDateBoundary(), stepSize);
        } catch (Exception e) {
            // Ignoring
            log.debug("No valid date boundaries were found for this topic.", e);
        }
        Map<Long, Double> dataMap = new HashMap<>();

        // Building the query according to the data type
        Map<String, Object> query = new HashMap<>();
        query.put("topicName", topic.getName());
        switch (dataType) {
        case ALL_POSITIVE: {
            Map<String, Object> attribute = new HashMap<>();
            attribute.put("$gt", topic.getProcessorPositiveBoundary());
            query.put("polarity", attribute);
        }
            break;
        case ALL_NEGATIVE: {
            Map<String, Object> attribute = new HashMap<>();
            attribute.put("$lt", topic.getProcessorNegativeBoundary());
            query.put("polarity", attribute);
        }
            break;
        case CREATED_POSITIVE: {
            Map<String, Object> attribute = new HashMap<>();
            attribute.put("$gt", topic.getProcessorPositiveBoundary());
            query.put("polarity", attribute);
            query.put("isRetweeted", false);
        }
            break;
        case CREATED_NEGATIVE: {
            Map<String, Object> attribute = new HashMap<>();
            attribute.put("$lt", topic.getProcessorNegativeBoundary());
            query.put("polarity", attribute);
            query.put("isRetweeted", false);
        }
            break;
        case RETWEETED_POSITIVE: {
            Map<String, Object> attribute = new HashMap<>();
            attribute.put("$gt", topic.getProcessorPositiveBoundary());
            query.put("polarity", attribute);
            query.put("isRetweeted", true);
        }
            break;
        case RETWEETED_NEGATIVE: {
            Map<String, Object> attribute = new HashMap<>();
            attribute.put("$lt", topic.getProcessorNegativeBoundary());
            query.put("polarity", attribute);
            query.put("isRetweeted", true);
        }
            break;
        default:
            // Should never occur
            return null;
        }

        DBCursor postsCursor = datastoreService.findCursorByQuery(query, ProcessedPost.class);

        while (postsCursor.hasNext()) {
            // Loop Variables
            Long timeStampSlot;
            
            DBObject rawPost = postsCursor.next();
            ProcessedPost post = datastoreService.getConverter().read(ProcessedPost.class, rawPost);

            Date createdAt = post.getCreatedAt();
            timeStampSlot = (long) Math.floor(createdAt.getTime() / stepSize);

            // Check if date is within boundaries
            if ((leftDateBoundary == 0 || timeStampSlot > leftDateBoundary)
                    && (rightDateBoundary == 0 || timeStampSlot < rightDateBoundary)) {
                // Check date slot and creating it if needed for the first map
                if (!dataMap.containsKey(timeStampSlot)) {
                    dataMap.put(timeStampSlot, 0D);
                }
                dataMap.put(timeStampSlot, dataMap.get(timeStampSlot) + 1);
            }
        }

        // Ready to ship...
        List<Map<String, List<Map<String, Double>>>> finalData = convertAndSortDataMap(dataMap, stepSize);
        return finalData;

    }

    private List<Map<String, List<Map<String, Double>>>> getCalculatedDataRows(Topic topic, DataType dataType) {
        // Get horizontal boundaries
        long leftDateBoundary = 0;
        long rightDateBoundary = 0;

        Long stepSize = topic.getPlotterStepSize();

        try {
            leftDateBoundary = getBoundary(topic.getPlotterLeftDateBoundary(), stepSize);
            rightDateBoundary = getBoundary(topic.getPlotterRightDateBoundary(), stepSize);
        } catch (Exception e) {
            // Ignoring
            log.debug("No valid date boundaries were found for this topic.", e);
        }
        Map<Long, List<Double>> auxiliarDataMap = new HashMap<>();

        // Building the query according to the data type
        Map<String, Object> query = new HashMap<>();
        query.put("topicName", topic.getName());
        switch (dataType) {
        case AVERAGE_POLARITY:
        case RATIO:
            break;
        default:
            // Should never occur
            return null;
        }

        DBCursor postsCursor = datastoreService.findCursorByQuery(query, ProcessedPost.class);

        while (postsCursor.hasNext()) {
            // Loop Variables
            Long timeStampSlot;

            DBObject rawPost = postsCursor.next();
            ProcessedPost post = datastoreService.getConverter().read(ProcessedPost.class, rawPost);

            Date createdAt = post.getCreatedAt();
            timeStampSlot = (long) Math.floor(createdAt.getTime() / stepSize);

            // Check if date is within boundaries
            if ((leftDateBoundary == 0 || timeStampSlot > leftDateBoundary)
                    && (rightDateBoundary == 0 || timeStampSlot < rightDateBoundary)) {
                // Check date slot and creating it if needed for the first map
                if (!auxiliarDataMap.containsKey(timeStampSlot)) {
                    auxiliarDataMap.put(timeStampSlot, new ArrayList<Double>());
                }
                auxiliarDataMap.get(timeStampSlot).add(post.getPolarity());
            }
        }

        Map<Long, Double> dataMap = new HashMap<>();
        for (Entry<Long, List<Double>> entry : auxiliarDataMap.entrySet()) {
            switch (dataType) {
            case AVERAGE_POLARITY: {
                double average = 0D;
                for (double item: entry.getValue()) {
                    average += item;
                }
                average = average / entry.getValue().size();
                dataMap.put(entry.getKey(), average);
            }
                break;
            case RATIO: {
                double ratio = 0D;
                for (double item: entry.getValue()) {
                    if (item > topic.getProcessorPositiveBoundary()) {
                        ratio++;
                    }
                    else if (item < topic.getProcessorNegativeBoundary()) {
                        ratio--;
                    }
                }
                ratio = ratio / entry.getValue().size();
                dataMap.put(entry.getKey(), ratio);
            }
                break;
            default:
                return null;
            }
            
        }

        // Ready to ship...
        List<Map<String, List<Map<String, Double>>>> finalData = convertAndSortDataMap(dataMap, stepSize);
        return finalData;
    }

    private static List<Map<String, List<Map<String, Double>>>> convertAndSortDataMap(Map<Long, Double> hashMap,
            Long stepSize) {
        List<Map<String, List<Map<String, Double>>>> data = new ArrayList<>();

        for (Entry<Long, Double> entry : hashMap.entrySet()) {
            Map<String, List<Map<String, Double>>> cMap = new HashMap<>();

            List<Map<String, Double>> itemList = new ArrayList<>();

            // First, input the time as the axis column value
            Map<String, Double> axisItem = new HashMap<>();
            axisItem.put("v", (double) entry.getKey() * stepSize);
            itemList.add(axisItem);
            // Next, the value
            Map<String, Double> valueItem = new HashMap<>();
            valueItem.put("v", (double) entry.getValue());
            itemList.add(valueItem);

            cMap.put("c", itemList);
            data.add(cMap);
        }

        return data;
    }
    
    private static Map<String, Object> getOptions(Topic topic, PlotType plotType) {

        Map<String, Object> col = new HashMap<>();
        col.put("fontName", "Open Sans");
        col.put("fontSize", "14 px");

        Map<String, Object> legend = new LinkedHashMap<>();
        legend.put("position", "bottom");
        col.put("legend", legend);

        Map<String, Object> chartArea = new LinkedHashMap<>();
        chartArea.put("width", "92%");
        chartArea.put("height", "80%");
        col.put("chartArea", chartArea);

        Map<String, Object> hAxis = new HashMap<>();
        hAxis.put("format", "MMM dd HH:mm:SS");
        Map<String, Object> gridlines = new HashMap<>();
        gridlines.put("count", 15);
        hAxis.put("gridlines", gridlines);
        col.put("hAxis", hAxis);

        switch (plotType) {
        case ALL: {
            col.put("title", "Count of extracted posts for Topic " + topic.getName());
        }
            break;
        case CREATED: {
            col.put("title", "Count of original posts (no retweets) extracted for Topic " + topic.getName());
        }
            break;
        case RETWEETED: {
            col.put("title", "Count of retweets extracted for Topic " + topic.getName());
        }
            break;
        case AVERAGE_POLARITY: {
            col.put("title", "Original posts vs retweets extracted for Topic " + topic.getName());
        }
            break;
        case TOTAL_RATIO: {
            col.put("title", "Unique users per interval for Topic " + topic.getName());
        }
            break;
        }
        return col;

    }

    private static Long getBoundary(Date boundary, Long stepSize) {
        long dateBoundary = 0;
        if (boundary != null) {
            dateBoundary = boundary.getTime() / stepSize;
        }
        return dateBoundary;
    }
    
    private static List<Map<String, List<Map<String, Double>>>> mixAndMatch(
            List<Map<String, List<Map<String, Double>>>> firstSet,
            List<Map<String, List<Map<String, Double>>>> secondSet) {

        Map<Double, Map<String, List<Double>>> valueMap = new TreeMap<>();

        // First pass to get all the time steps between both and values
        for (Map<String, List<Map<String, Double>>> row : firstSet) {
            if (row.get("c") != null && row.get("c").get(0) != null) {
                Double timeStep = row.get("c").get(0).get("v");
                Map<String, List<Double>> map = valueMap.get(timeStep);
                if (map == null) {
                    map = new HashMap<>();
                }
                List<Double> values = new ArrayList<>();
                for (int i = 1; i < row.get("c").size(); i++) {
                    if (row.get("c").get(i).get("v") != null) {
                        values.add(row.get("c").get(i).get("v"));
                    }
                }
                map.put("first", values);
                valueMap.put(timeStep, map);
            }
        }

        for (Map<String, List<Map<String, Double>>> row : secondSet) {
            if (row.get("c") != null && row.get("c").get(0) != null) {
                Double timeStep = row.get("c").get(0).get("v");
                Map<String, List<Double>> map = valueMap.get(timeStep);
                if (map == null) {
                    map = new HashMap<>();
                }
                List<Double> values = new ArrayList<>();
                for (int i = 1; i < row.get("c").size(); i++) {
                    if (row.get("c").get(i).get("v") != null) {
                        values.add(row.get("c").get(i).get("v"));
                    }
                }
                map.put("second", values);
                valueMap.put(timeStep, map);
            }
        }

        // Now, generate the object
        List<Map<String, List<Map<String, Double>>>> data = new ArrayList<>();

        for (Entry<Double, Map<String, List<Double>>> entry : valueMap.entrySet()) {
            Map<String, List<Map<String, Double>>> cMap = new HashMap<>();

            List<Map<String, Double>> itemList = new ArrayList<>();

            // First, input the time as the axis column value
            Map<String, Double> axisItem = new HashMap<>();
            axisItem.put("v", (double) entry.getKey());
            itemList.add(axisItem);
            // Next, the values
            if (entry.getValue().get("first") != null) {
                for (Double value : entry.getValue().get("first")) {
                    Map<String, Double> valueItem = new HashMap<>();
                    valueItem.put("v", (value));
                    itemList.add(valueItem);
                }
            }
            if (entry.getValue().get("second") != null) {
                for (Double value : entry.getValue().get("second")) {
                    Map<String, Double> valueItem = new HashMap<>();
                    valueItem.put("v", (value));
                    itemList.add(valueItem);
                }
            }

            cMap.put("c", itemList);
            data.add(cMap);
        }

        return data;
    }
}
