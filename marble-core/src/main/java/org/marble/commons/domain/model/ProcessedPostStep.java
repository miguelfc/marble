package org.marble.commons.domain.model;

import java.util.LinkedHashSet;
import java.util.Set;

import org.marble.model.model.JobParameters;

public class ProcessedPostStep {
    private String inputText;
    private String outputText;
    private Double polarity;
    private JobParameters processParameters;
    private String notes;

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    public Double getPolarity() {
        return polarity;
    }

    public void setPolarity(Double polarity) {
        this.polarity = polarity;
    }

    public JobParameters getProcessParameters() {
        return processParameters;
    }

    public void setProcessParameters(JobParameters processParameters) {
        this.processParameters = processParameters;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
