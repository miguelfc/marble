package org.marble.model.domain.model;

import java.io.Serializable;
import java.util.Set;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;
import org.marble.model.model.JobParameters;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "topics")
@JsonIgnoreProperties({ "changeSet", "executions" })
public class Topic implements Serializable {
    private static final long serialVersionUID = -4417618450499483945L;

    @Id
    @NotEmpty
    private String name;

    private String description;

    @NotNull
    @NotEmpty
    private String keywords;

    @Digits(fraction = 0, integer = 24)
    private Long upperLimit;

    @Digits(fraction = 0, integer = 24)
    private Long lowerLimit;

    @Pattern(regexp = "[a-zA-Z]{2}|")
    private String language = "en";

    @Min(1)
    @Max(100)
    private Integer postsPerCall;

    @NotNull
    @Digits(fraction = 0, integer = 5)
    private Integer postsPerFullExtraction = 1000;

    private Set<JobParameters> lastProcessParameters;
    private Set<JobParameters> lastPlotterParameters;

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

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Long getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Long upperLimit) {
        this.upperLimit = upperLimit;
    }

    public Long getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(Long lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getPostsPerCall() {
        return postsPerCall;
    }

    public void setPostsPerCall(Integer postsPerCall) {
        this.postsPerCall = postsPerCall;
    }

    public Integer getPostsPerFullExtraction() {
        return postsPerFullExtraction;
    }

    public void setPostsPerFullExtraction(Integer postsPerFullExtraction) {
        this.postsPerFullExtraction = postsPerFullExtraction;
    }

    public Set<JobParameters> getLastProcessParameters() {
        return lastProcessParameters;
    }

    public void setLastProcessParameters(Set<JobParameters> processParameters) {
        this.lastProcessParameters = processParameters;
    }

    public Set<JobParameters> getLastPlotterParameters() {
        return lastPlotterParameters;
    }

    public void setLastPlotterParameters(Set<JobParameters> lastPlotterParameters) {
        this.lastPlotterParameters = lastPlotterParameters;
    }
}
