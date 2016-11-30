package org.marble.commons.service;

import java.math.BigInteger;
import java.util.List;

import org.marble.commons.exception.InvalidPlotException;
import org.marble.model.domain.model.Chart;

public interface PlotService {

    public Chart save(Chart plot) throws InvalidPlotException;

    public Chart findOne(BigInteger id) throws InvalidPlotException;
    
    public Long deleteByTopicName(String topicName);

    public void delete(BigInteger id);

    Long count();

}
