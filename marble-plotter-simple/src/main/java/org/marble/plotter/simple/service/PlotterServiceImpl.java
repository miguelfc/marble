package org.marble.plotter.simple.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.marble.model.domain.model.ProcessedPost;
import org.marble.model.domain.model.Chart;
import org.marble.model.domain.model.Post;
import org.marble.model.domain.model.Topic;
import org.marble.model.model.PlotterInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Service
public class PlotterServiceImpl implements PlotterService {

    private static final Logger log = LoggerFactory.getLogger(PlotterServiceImpl.class);

    private static final String COLUMN_CHART = "ColumnChart";

    // Unprocessed Types
    private static final String PLOT_ALL = "PlotAll";
    private static final String PLOT_CREATED = "PlotCreated";
    private static final String PLOT_RETWEETED = "PlotRetweeted";
    private static final String PLOT_CREATED_RETWEETED = "PlotCreatedRetweeted";
    private static final String PLOT_UNIQUE_USERS = "PlotUniqueUsers";

    // Processed Types
    private static final String PLOT_ALL_PROCESSED = "PlotAllProcessed";
    private static final String PLOT_ORIGINALS_PROCESSED = "PlotOriginalsProcessed";
    private static final String PLOT_RETWEETED_PROCESSED = "PlotRetweetedProcessed";
    private static final String PLOT_AVERAGE_POLARITY = "PlotAveragePolarity";
    private static final String PLOT_TOTAL_RATIO_POLARITY = "PlotTotalRatioPolarity";

    private static final String ALL_POSITIVE = "AllPositive";
    private static final String ALL_NEGATIVE = "AllNegative";
    private static final String ORIGINALS_POSITIVE = "OriginalsPositive";
    private static final String ORIGINALS_NEGATIVE = "OriginalsNegative";
    private static final String RETWEETED_POSITIVE = "RetweetedPositive";
    private static final String RETWEETED_NEGATIVE = "RetweetedNegative";
    private static final String AVERAGE_POLARITY = "AveragePolarity";
    private static final String RATIO = "Ratio";


    @Autowired
    DatastoreService datastoreService;

    @Override
    public List<Chart> plot(PlotterInput input) {

        List<Chart> plotList = new ArrayList<>();
        // Get parameters
        String topicName = input.getTopicName();
        String plotTitle = "My Plot";
        String plotDescription = "Default description for my plot.";
        String plotGraphic = COLUMN_CHART;
        String plotType = PLOT_ALL;
        Long stepSize = 60L;
        Long leftBoundary = null;
        Long rightBoundary = null;

        Map<String, Object> options = input.getOptions();
        if (options != null) {
            if (options.get("title") != null) {
                plotTitle = (String) options.get("title");
            }
            if (options.get("description") != null) {
                plotDescription = (String) options.get("description");
            }
            if (options.get("graphic") != null) {
                plotGraphic = (String) options.get("graphic");
            }
            if (options.get("type") != null) {
                plotType = (String) options.get("type");
            }
            if (options.get("stepSize") != null) {
                stepSize = ((Integer) options.get("stepSize")).longValue();
            }
            if (options.get("leftBoundary") != null) {
                leftBoundary = ((Integer) options.get("leftBoundary")).longValue();
            }
            if (options.get("rightBoundary") != null) {
                rightBoundary = ((Integer) options.get("rightBoundary")).longValue();
            }
        }

        if (topicName == null) {
            log.error("TODO: TopicName is null. Error message pending.");
            return null;
        }

        Topic topic = datastoreService.findOneByTopicName(topicName, Topic.class);

        if (topic == null) {
            log.error("TODO: Topic was not found. Error message pending.");
            return null;
        }

        log.info("Creating plot for topic <" + topicName + ">...");

        // Here starts the execution
        Chart chart = new Chart();
        chart.setTopic(topic);
        chart.setName(plotTitle);
        chart.setType(Chart.TYPE_GOOGLE_CHART);
        chart.setDescription(plotDescription);
        chart.setCustomType(plotGraphic);

        Map<String, Object> singleData = new HashMap<>();
        singleData.put("cols", getPostsColumns(topic, plotType));

        switch (plotType) {
        case PLOT_ALL:
        case PLOT_CREATED:
        case PLOT_RETWEETED: {
            singleData.put("rows", getPostsRows(topic, plotType, stepSize, leftBoundary, rightBoundary));
        }
            break;

        case PLOT_CREATED_RETWEETED: {
            List<Map<String, List<Map<String, Double>>>> originals = getPostsRows(topic, PLOT_CREATED, stepSize, leftBoundary, rightBoundary);
            List<Map<String, List<Map<String, Double>>>> retweets = getPostsRows(topic, PLOT_RETWEETED, stepSize, leftBoundary, rightBoundary);

            singleData.put("rows", mixAndMatch(originals, retweets));
        }
            break;
        case PLOT_UNIQUE_USERS: {
            singleData.put("rows", getUsersRows(topic, plotType, stepSize, leftBoundary, rightBoundary));
        }
            break;
        case PLOT_ALL_PROCESSED: {
            List<Map<String, List<Map<String, Double>>>> positive = getPostsRows(topic, ALL_POSITIVE, stepSize, leftBoundary, rightBoundary);
            List<Map<String, List<Map<String, Double>>>> negative = getPostsRows(topic, ALL_NEGATIVE, stepSize, leftBoundary, rightBoundary);

            singleData.put("rows", mixAndMatch(positive, negative));
        }
            break;
        case PLOT_ORIGINALS_PROCESSED: {
            List<Map<String, List<Map<String, Double>>>> positive = getPostsRows(topic, ORIGINALS_POSITIVE, stepSize, leftBoundary, rightBoundary);
            List<Map<String, List<Map<String, Double>>>> negative = getPostsRows(topic, ORIGINALS_NEGATIVE, stepSize, leftBoundary, rightBoundary);

            singleData.put("rows", mixAndMatch(positive, negative));
        }
            break;
        case PLOT_RETWEETED_PROCESSED: {
            List<Map<String, List<Map<String, Double>>>> positive = getPostsRows(topic, RETWEETED_POSITIVE, stepSize, leftBoundary, rightBoundary);
            List<Map<String, List<Map<String, Double>>>> negative = getPostsRows(topic, RETWEETED_NEGATIVE, stepSize, leftBoundary, rightBoundary);

            singleData.put("rows", mixAndMatch(positive, negative));
        }
            break;
        case PLOT_AVERAGE_POLARITY: {
            singleData.put("rows", getCalculatedDataRows(topic, AVERAGE_POLARITY, stepSize, leftBoundary, rightBoundary));
        }
            break;
        case PLOT_TOTAL_RATIO_POLARITY: {
            singleData.put("rows", getCalculatedDataRows(topic, RATIO, stepSize, leftBoundary, rightBoundary));
        }
            break;
        }
        //missing default case
        default:
            // add default case
            break;

        chart.setData(singleData);
        chart.setOptions(getOptions(topic, plotType));

        plotList.add(chart);
        return plotList;
    }

    private List<Map<String, Object>> getPostsColumns(Topic topic, String plotType) {
        List<Map<String, Object>> cols = new ArrayList<>();

        Map<String, Object> col = null;

        col = new HashMap<>();
        col.put("id", "time");
        col.put("label", "Time");
        col.put("type", "datetime");
        cols.add(col);

        switch (plotType) {
        case PLOT_ALL: {
            col = new HashMap<>();
            col.put("id", "posts");
            col.put("label", "Posts");
            col.put("type", "number");
            cols.add(col);
        }
            break;
        case PLOT_CREATED: {
            col = new HashMap<>();
            col.put("id", "originals");
            col.put("label", "Original Posts");
            col.put("type", "number");
            cols.add(col);
        }
            break;
        case PLOT_RETWEETED: {
            col = new HashMap<>();
            col.put("id", "retweets");
            col.put("label", "Retweets");
            col.put("type", "number");
            cols.add(col);
        }
            break;
        case PLOT_CREATED_RETWEETED: {
            col = new HashMap<>();
            col.put("id", "originals");
            col.put("label", "Original Posts");
            col.put("type", "number");
            cols.add(col);

            col = new HashMap<>();
            col.put("id", "retweets");
            col.put("label", "Retweets");
            col.put("type", "number");
            cols.add(col);
        }
            break;
        case PLOT_UNIQUE_USERS: {
            col = new HashMap<>();
            col.put("id", "users");
            col.put("label", "Unique Users");
            col.put("type", "number");
            cols.add(col);
        }
        case PLOT_ALL_PROCESSED: {
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
        case PLOT_ORIGINALS_PROCESSED: {
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
        case PLOT_RETWEETED_PROCESSED: {
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
        case PLOT_AVERAGE_POLARITY: {
            col = new HashMap<>();
            col.put("id", "originals");
            col.put("label", "Average Polarity");
            col.put("type", "number");
            cols.add(col);
        }
            break;
        case PLOT_TOTAL_RATIO_POLARITY: {
            col = new HashMap<>();
            col.put("id", "users");
            col.put("label", "Positive Negative Ratio");
            col.put("type", "number");
            cols.add(col);
        }
        //missing default case
        default:
            // add default case
            break;

        }
        return cols;
    }

    private List<Map<String, List<Map<String, Double>>>> getPostsRows(Topic topic, String plotType, Long stepSize, Long leftBoundary, Long rightBoundary) {
        return this.getPostsRows(topic, plotType, stepSize, leftBoundary, rightBoundary, 0L, 0L);
    }
    
    private List<Map<String, List<Map<String, Double>>>> getPostsRows(Topic topic, String plotType, Long stepSize, Long leftBoundary, Long rightBoundary, Long positiveBoundary, Long negativeBoundary) {
        // Get horizontal boundaries
        long leftDateBoundary = 0;
        long rightDateBoundary = 0;

        try {
            leftDateBoundary = getBoundary(leftBoundary, stepSize);
            rightDateBoundary = getBoundary(rightBoundary, stepSize);
        } catch (Exception e) {
            // Ignoring
            log.debug("No valid date boundaries were found for this topic.", e);
        }

        Map<Long, Double> dataMap = new TreeMap<>();

        @SuppressWarnings("rawtypes")
        Class classToSearch = Post.class;
        
        // Building the query according to the data type
        Map<String, Object> query = new HashMap<>();
        query.put("topicName", topic.getName());
        switch (plotType) {
        case PLOT_ALL:
            classToSearch = Post.class;
            break;
        case PLOT_CREATED: {
            Map<String, Object> attribute = new HashMap<>();
            attribute.put("$exists", false);
            query.put("retweetedPost", attribute);
            classToSearch = Post.class;
        }
            break;
        case PLOT_RETWEETED: {
            Map<String, Object> attribute = new HashMap<>();
            attribute.put("$exists", true);
            query.put("retweetedPost", attribute);
            classToSearch = Post.class;
        }
            break;
        case ALL_POSITIVE: {
            Map<String, Object> attribute = new HashMap<>();
            attribute.put("$gt", positiveBoundary);
            query.put("polarity", attribute);
            classToSearch = ProcessedPost.class;
        }
            break;
        case ALL_NEGATIVE: {
            Map<String, Object> attribute = new HashMap<>();
            attribute.put("$lt", negativeBoundary);
            query.put("polarity", attribute);
            classToSearch = ProcessedPost.class;
        }
            break;
        case ORIGINALS_POSITIVE: {
            Map<String, Object> attribute = new HashMap<>();
            attribute.put("$gt", positiveBoundary);
            query.put("polarity", attribute);
            query.put("isRetweeted", false);
            classToSearch = ProcessedPost.class;
        }
            break;
        case ORIGINALS_NEGATIVE: {
            Map<String, Object> attribute = new HashMap<>();
            attribute.put("$lt", negativeBoundary);
            query.put("polarity", attribute);
            query.put("isRetweeted", false);
            classToSearch = ProcessedPost.class;
        }
            break;
        case RETWEETED_POSITIVE: {
            Map<String, Object> attribute = new HashMap<>();
            attribute.put("$gt", positiveBoundary);
            query.put("polarity", attribute);
            query.put("isRetweeted", true);
            classToSearch = ProcessedPost.class;
        }
            break;
        case RETWEETED_NEGATIVE: {
            Map<String, Object> attribute = new HashMap<>();
            attribute.put("$lt", negativeBoundary);
            query.put("polarity", attribute);
            query.put("isRetweeted", true);
            classToSearch = ProcessedPost.class;
        }
            break;
        default:
            // Should never occur
            return null;
        }

        @SuppressWarnings("unchecked")
        DBCursor postsCursor = datastoreService.findCursorByQuery(query, classToSearch);

        while (postsCursor.hasNext()) {
            // Loop Variables
            Long timeStampSlot;

            DBObject rawPost = postsCursor.next();
            Post status = datastoreService.getConverter().read(Post.class, rawPost);

            Date createdAt = status.getCreatedAt();
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

    private List<Map<String, List<Map<String, Double>>>> getUsersRows(Topic topic, String plotType, Long stepSize, Long leftBoundary, Long rightBoundary) {
        // Get horizontal boundaries
        long leftDateBoundary = 0;
        long rightDateBoundary = 0;

        try {
            leftDateBoundary = getBoundary(leftBoundary, stepSize);
            rightDateBoundary = getBoundary(rightBoundary, stepSize);
        } catch (Exception e) {
            // Ignoring
            log.debug("No valid date boundaries were found for this topic.", e);
        }
        Map<Long, Set<String>> auxiliarDataMap = new HashMap<>();

        // Building the query according to the data type
        Map<String, Object> query = new HashMap<>();
        query.put("topicName", topic.getName());
        switch (plotType) {
        case PLOT_UNIQUE_USERS:
            break;
        default:
            // Should never occur
            return null;
        }

        DBCursor postsCursor = datastoreService.findCursorByQuery(query, Post.class);

        while (postsCursor.hasNext()) {
            // Loop Variables
            Long timeStampSlot;
            DBObject rawPost = postsCursor.next();
            Post status = datastoreService.getConverter().read(Post.class, rawPost);

            Date createdAt = status.getCreatedAt();
            timeStampSlot = (long) Math.floor(createdAt.getTime() / stepSize);

            // Check if date is within boundaries
            if ((leftDateBoundary == 0 || timeStampSlot > leftDateBoundary)
                    && (rightDateBoundary == 0 || timeStampSlot < rightDateBoundary)) {
                // Check date slot and creating it if needed for the first map
                if (!auxiliarDataMap.containsKey(timeStampSlot)) {
                    auxiliarDataMap.put(timeStampSlot, new HashSet<String>());
                }
                auxiliarDataMap.get(timeStampSlot).add(status.getUser().getScreenName());
            }
        }

        Map<Long, Double> dataMap = new TreeMap<>();
        for (Entry<Long, Set<String>> entry : auxiliarDataMap.entrySet()) {
            double userCount = entry.getValue().size();
            dataMap.put(entry.getKey(), userCount);
        }

        // Ready to ship...
        List<Map<String, List<Map<String, Double>>>> finalData = convertAndSortDataMap(dataMap, stepSize);
        return finalData;
    }

    private List<Map<String, List<Map<String, Double>>>> getCalculatedDataRows(Topic topic, String dataType, Long stepSize, Long leftBoundary, Long rightBoundary) {
        return this.getCalculatedDataRows(topic, dataType, stepSize, leftBoundary, rightBoundary, 0L, 0L);
    }
    
    private List<Map<String, List<Map<String, Double>>>> getCalculatedDataRows(Topic topic, String dataType, Long stepSize, Long leftBoundary, Long rightBoundary, Long positiveBoundary, Long negativeBoundary) {
     // Get horizontal boundaries
        long leftDateBoundary = 0;
        long rightDateBoundary = 0;

        try {
            leftDateBoundary = getBoundary(leftBoundary, stepSize);
            rightDateBoundary = getBoundary(rightBoundary, stepSize);
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
                for (double item : entry.getValue()) {
                    average += item;
                }
                average = average / entry.getValue().size();
                dataMap.put(entry.getKey(), average);
            }
                break;
            case RATIO: {
                double ratio = 0D;
                for (double item : entry.getValue()) {
                    if (item > positiveBoundary) {
                        ratio++;
                    } else if (item < negativeBoundary) {
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

    private static Map<String, Object> getOptions(Topic topic, String plotType) {

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
        case PLOT_ALL: {
            col.put("title", "Count of extracted posts for Topic " + topic.getName());
        }
            break;
        case PLOT_CREATED: {
            col.put("title", "Count of original posts (no retweets) extracted for Topic " + topic.getName());
        }
            break;
        case PLOT_RETWEETED: {
            col.put("title", "Count of retweets extracted for Topic " + topic.getName());
        }
            break;
        case PLOT_CREATED_RETWEETED: {
            col.put("title", "Original posts vs retweets extracted for Topic " + topic.getName());
        }
            break;
        case PLOT_UNIQUE_USERS: {
            col.put("title", "Unique users per interval for Topic " + topic.getName());
        }
            break;
        }
        //missing default case
        default:
            // add default case
            break;

        return col;

    }

    private static Long getBoundary(Long boundary, Long stepSize) {
        long dateBoundary = 0;
        if (boundary != null) {
            dateBoundary = boundary / stepSize;
        }
        return dateBoundary;
    }
}
