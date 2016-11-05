package org.marble.commons.domain.model;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.marble.commons.util.LongSerializer;
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
    
    private Set<String> processorNotes = new LinkedHashSet<>();

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

    public void setPolarity(Double polarity) {
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

    public void setText(String text) {
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

    public Set<String> getProcessorNotes() {
        return processorNotes;
    }

    public void setProcessorNotes(Set<String> processorNotes) {
        this.processorNotes = processorNotes;
    }

    public void appendProcessorNotes(String notes) {
        this.processorNotes.add(notes);
    }

}