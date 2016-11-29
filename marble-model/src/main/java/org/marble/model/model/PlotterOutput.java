package org.marble.model.model;

import java.util.ArrayList;
import java.util.List;

import org.marble.model.domain.model.Plot;

public class PlotterOutput {
    private List<Plot> charts = new ArrayList<>();
    private String notes;

    public List<Plot> getCharts() {
        return charts;
    }

    public void setCharts(List<Plot> charts) {
        this.charts = charts;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "PlotterOutput [charts=" + charts + ", notes=" + notes + "]";
    }

}
