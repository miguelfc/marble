package org.marble.plotter.simple.service;

import java.util.List;

import org.marble.model.domain.model.Plot;
import org.marble.model.model.PlotterInput;

public interface PlotterService {

    List<Plot> plot(PlotterInput input);

}
