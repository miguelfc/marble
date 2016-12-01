package org.marble.commons.service;

import java.math.BigInteger;
import java.util.List;

import org.marble.commons.exception.InvalidChartException;
import org.marble.model.domain.model.Chart;

public interface ChartService {

    public Chart save(Chart plot) throws InvalidChartException;

    public Chart findOne(BigInteger id) throws InvalidChartException;
    
    public Long deleteByTopicName(String topicName);

    public void delete(BigInteger id);

    Long count();

}
