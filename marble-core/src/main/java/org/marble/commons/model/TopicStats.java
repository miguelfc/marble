package org.marble.commons.model;

import java.util.Date;

public class TopicStats {
    
    private String topicName;

    private Long totalPostsExtracted;
    private Date oldestPostDate;
    private Long oldestPostId;
    private Date newestPostDate;
    private Long newestPostId;
    private Long totalPostsProcessed;
    private Long totalJobs;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Long getTotalPostsExtracted() {
        return totalPostsExtracted;
    }

    public void setTotalPostsExtracted(Long totalPostsExtracted) {
        this.totalPostsExtracted = totalPostsExtracted;
    }

    public Date getOldestPostDate() {
        return oldestPostDate;
    }

    public void setOldestPostDate(Date oldestPostDate) {
        this.oldestPostDate = oldestPostDate;
    }

    public Long getOldestPostId() {
        return oldestPostId;
    }

    public void setOldestPostId(Long oldestPostId) {
        this.oldestPostId = oldestPostId;
    }

    public Date getNewestPostDate() {
        return newestPostDate;
    }

    public void setNewestPostDate(Date newestPostDate) {
        this.newestPostDate = newestPostDate;
    }

    public Long getNewestPostId() {
        return newestPostId;
    }

    public void setNewestPostId(Long newestPostId) {
        this.newestPostId = newestPostId;
    }

    public Long getTotalPostsProcessed() {
        return totalPostsProcessed;
    }

    public void setTotalPostsProcessed(Long totalPostsProcessed) {
        this.totalPostsProcessed = totalPostsProcessed;
    }

    public Long getTotalJobs() {
        return totalJobs;
    }

    public void setTotalJobs(Long totalJobs) {
        this.totalJobs = totalJobs;
    }

}
