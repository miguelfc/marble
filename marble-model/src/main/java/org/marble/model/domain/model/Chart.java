package org.marble.model.domain.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;
import org.marble.model.domain.model.Topic;
import org.marble.util.BigIntegerSerializer;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mongodb.BasicDBObject;

@Document(collection = "charts")
@JsonIgnoreProperties({ "topic" })
public class Chart implements Serializable {

    private static final long serialVersionUID = 6936532299491147949L;

    @Id
    @JsonSerialize(using = BigIntegerSerializer.class)
    private BigInteger id;

    @Pattern(regexp = "[a-zA-Z_ 0-9-]+")
    @NotEmpty
    private String name;

    private String description;

    private String type;

    @DBRef
    private Topic topic;

    @JsonSerialize(using = BigIntegerSerializer.class)
    private BigInteger jobId;

    private BasicDBObject options;

    private BasicDBObject data;

    @CreatedDate
    public Date createdAt;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public BasicDBObject getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = new BasicDBObject(options);

    }

    public BasicDBObject getData() {
        return data;
    }

    @Deprecated
    public void setData(List<Map<String, Object>> data) {
        this.data = null;
    }

    public void setData(Map<String, Object> data) {
        this.data = new BasicDBObject(data);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public BigInteger getJobId() {
        return jobId;
    }

    public void setJobId(BigInteger job) {
        this.jobId = job;
    }

    @Override
    public String toString() {
        return "Plot [id=" + id + ", name=" + name + ", description=" + description + ", topic=" + topic
                + ", options=" + options
                + ", data=" + data + ", createdAt=" + createdAt + "]";
    }
}
