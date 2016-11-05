package org.marble.commons.domain.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;
import org.marble.commons.model.ProcessParameters;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Document(collection = "topics")
@JsonIgnoreProperties({ "changeSet", "executions" })
public class Topic implements Serializable {
    private static final long serialVersionUID = -4417618450499483945L;

    @Id
    //@Pattern(regexp = "[a-zA-Z_0-9-]+")
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

    @NotNull
    private Double processorPositiveBoundary = 0D;

    @NotNull
    private Double processorNegativeBoundary = 0D;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date plotterLeftDateBoundary;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date plotterRightDateBoundary;

    @Min(value = 60000)
    @NotNull
    private Long plotterStepSize = 60000L;
    
    private Set<ProcessParameters> lastProcessParameters;

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

    public Double getProcessorPositiveBoundary() {
        return processorPositiveBoundary;
    }

    public void setProcessorPositiveBoundary(Double processorPositiveBoundary) {
        this.processorPositiveBoundary = processorPositiveBoundary;
    }

    public Double getProcessorNegativeBoundary() {
        return processorNegativeBoundary;
    }

    public void setProcessorNegativeBoundary(Double processorNegativeBoundary) {
        this.processorNegativeBoundary = processorNegativeBoundary;
    }

    public Date getPlotterLeftDateBoundary() {
        return plotterLeftDateBoundary;
    }

    public void setPlotterLeftDateBoundary(Date plotterLeftDateBoundary) {
        this.plotterLeftDateBoundary = plotterLeftDateBoundary;
    }

    public Date getPlotterRightDateBoundary() {
        return plotterRightDateBoundary;
    }

    public void setPlotterRightDateBoundary(Date plotterRightDateBoundary) {
        this.plotterRightDateBoundary = plotterRightDateBoundary;
    }

    public Long getPlotterStepSize() {
        return plotterStepSize;
    }

    public void setPlotterStepSize(Long plotterStepSize) {
        this.plotterStepSize = plotterStepSize;
    }

    public Set<ProcessParameters> getLastProcessParameters() {
        return lastProcessParameters;
    }

    public void setLastProcessParameters(Set<ProcessParameters> processParameters) {
        this.lastProcessParameters = processParameters;
    }
}
