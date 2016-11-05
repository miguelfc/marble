package org.marble.commons.service;

import java.math.BigInteger;
import java.util.List;
import org.marble.commons.domain.model.Plot;
import org.marble.commons.exception.InvalidPlotException;

public interface PlotService {

    public Plot save(Plot plot) throws InvalidPlotException;

    public Plot findOne(BigInteger id) throws InvalidPlotException;
    
    public Long deleteByTopicName(String topicName);

    public void delete(BigInteger id);

    Long count();

}
