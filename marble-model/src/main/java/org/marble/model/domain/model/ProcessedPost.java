package org.marble.model.domain.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.marble.model.model.ProcessedPostStep;
import org.marble.util.LongSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Document(collection = "processed_posts")
public class ProcessedPost {

    @Indexed
    private String topicName;
    @Indexed
    private Double polarity;

    private Date createdAt;

    @Id
    @JsonSerialize(using = LongSerializer.class)
    private long id;
    private String text;
    private String originalText;

    @Indexed
    private boolean isRetweeted;
    private String screenName;
    private String timeZone;
    private Date originalCreatedAt;

    private List<ProcessedPostStep> steps = new ArrayList<>();

    public ProcessedPost() {

    }

    public ProcessedPost(ProcessedPost post) {
        this.topicName = post.getTopicName();
        this.polarity = post.getPolarity();
        this.createdAt = post.getCreatedAt();
        this.id = post.getId();
        this.text = post.getText();
        this.originalText = post.getText();
        this.isRetweeted = post.isRetweeted();
        this.screenName = post.getScreenName();
        this.timeZone = post.getTimeZone();
        this.originalCreatedAt = post.getOriginalCreatedAt();
    }

    public ProcessedPost(Post post) {
        this.topicName = post.getTopicName();
        this.polarity = null;
        this.createdAt = post.getCreatedAt();
        this.id = post.getId();
        this.text = post.getText();
        this.originalText = post.getText();
        if (post.getUser() != null) {
            this.screenName = post.getUser().getScreenName();
            this.timeZone = post.getUser().getTimeZone();
        }
        if (post.getRetweetedStatus() != null) {
            this.originalCreatedAt = post.getRetweetedStatus().getCreatedAt();
            this.isRetweeted = true;
        }
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Double getPolarity() {
        return polarity;
    }

    private void setPolarity(Double polarity) {
        this.polarity = polarity;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    private void setText(String text) {
        this.text = text;
    }

    public String getOriginalText() {
        return originalText;
    }

    public boolean isRetweeted() {
        return isRetweeted;
    }

    public void setRetweeted(boolean isRetweeted) {
        this.isRetweeted = isRetweeted;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Date getOriginalCreatedAt() {
        return originalCreatedAt;
    }

    public void setOriginalCreatedAt(Date originalCreatedAt) {
        this.originalCreatedAt = originalCreatedAt;
    }

    public List<ProcessedPostStep> getSteps() {
        return steps;
    }

    public void setSteps(List<ProcessedPostStep> steps) {
        this.steps = steps;
    }

    public ProcessedPostStep getLatestStep() {
        if (this.steps.size() > 0) {
            return this.steps.get(this.steps.size() - 1);
        } else {
            return null;
        }
    }

    public void addStep(ProcessedPostStep step) {
        if (step.getPolarity() != null) {
            this.setPolarity(step.getPolarity());
        }
        if (step.getOutputText() != null) {
            this.setText(step.getOutputText());
        }
        this.steps.add(step);
    }

}