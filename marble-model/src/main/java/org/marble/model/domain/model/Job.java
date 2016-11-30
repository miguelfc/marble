package org.marble.model.domain.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.marble.util.MarbleUtil;
import org.marble.model.model.JobParameters;
import org.marble.model.model.JobStatus;
import org.marble.model.model.JobType;
import org.marble.util.BigIntegerSerializer;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Document(collection = "jobs")
public class Job implements Serializable {
    private static final long serialVersionUID = -1285068636097871799L;

    public static final Integer LOG_LIMIT = 50000;

    @Id
    @JsonSerialize(using = BigIntegerSerializer.class)
    private BigInteger id;

    private JobType type;

    private JobStatus status = JobStatus.Initialized;

    private String log = "";

    @DBRef
    private Topic topic;

    @CreatedDate
    public Date createdAt;
    @LastModifiedDate
    public Date updatedAt;

    private Set<JobParameters> jobParameters = new LinkedHashSet<>();

    @DBRef
    @JsonIgnore
    private List<Chart> charts;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public void appendLog(String log) {
        this.log = MarbleUtil.getDatedMessage(log) + "\n" + this.log;
        if (this.log.length() > LOG_LIMIT) {
            this.log = this.log.substring(0, LOG_LIMIT);
            this.log = this.log.substring(0, this.log.lastIndexOf("\n"));

        }
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<JobParameters> getParameters() {
        return jobParameters;
    }

    public void setParameters(Set<JobParameters> jobParameters) {
        this.jobParameters = jobParameters;
    }

    public List<Chart> getCharts() {
        return charts;
    }

    public void setCharts(List<Chart> charts) {
        this.charts = charts;
    }

    @Override
    public String toString() {
        return "Job [id=" + id + ", type=" + type + ", status=" + status + ", log=" + log
                + ", topic=" + topic + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", jobParameters="
                + jobParameters + ", chart=" + charts + "]";
    }
}
