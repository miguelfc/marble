package org.marble.plotter.simple.service;

import java.util.List;

import org.marble.model.domain.model.Chart;
import org.marble.model.model.PlotterInput;

public interface PlotterService {

    List<Chart> plot(PlotterInput input);

}
